package br.com.cantinho.tcpspringbootstarter.applications.chat;

import br.com.cantinho.tcpspringbootstarter.applications.Application;
import br.com.cantinho.tcpspringbootstarter.applications.chat.domain.Bag;
import br.com.cantinho.tcpspringbootstarter.applications.chat.domain.ChatCommands;
import br.com.cantinho.tcpspringbootstarter.applications.chat.domain.UserIdentifier;
import br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions.DistinctRoomException;
import br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions.RoomAlreadyExistsException;
import br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions.RoomNotFoundException;
import br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions
    .UserConnectedToAnotherRoomException;
import br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions
    .UserDoesNotBelongToAnyRoomException;
import br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions.UserNotConnectedException;
import br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions
    .UserOwnerOfAnotherRoomException;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.ChatData;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ChatApplication implements Application {

  /**
   * A logger instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ChatApplication.class.getCanonicalName());

  private final List<UserIdentifier> userIdentifiers = Collections.synchronizedList(new ArrayList<>());

  /**
   * Map containing rooms and user owners.
   * - rooms.key : room name
   * - rooms.value : owner user
   */
  private final Map<String, String> rooms = Collections.synchronizedMap(new HashMap<>());

  @Override
  public Object process(Object... parameters) {
    LOGGER.debug("process");
    final String uci = (String) parameters[0];
    final Class clazz = (Class) parameters[1];
    final ChatData request = (ChatData) parameters[2];

    List<Bag> responseBags = new LinkedList<>();
    try {

      responseBags.addAll(processCommand(clazz, uci, request));

    } catch (UserOwnerOfAnotherRoomException
        | RoomAlreadyExistsException
        | UserNotConnectedException
        | UserConnectedToAnotherRoomException
        | RoomNotFoundException
        | DistinctRoomException
        | UserDoesNotBelongToAnyRoomException exc) {
      LOGGER.error("process: {}", exc.getMessage());
      responseBags.add(createDirectResponse(clazz, uci, request.getFrom(), request.getCmd(),
          ChatCommands.ResponseCode.ERROR, exc.getMessage()));
    }
    return responseBags;
  }

  @Override
  public void onConnect(String uci) {
    LOGGER.debug("onConnect:{}", uci);
  }

  @Override
  public void onDisconnect(final String uci) {
    final Iterator<UserIdentifier> iterator = userIdentifiers.iterator();
    while(iterator.hasNext()) {
      final UserIdentifier userIdentifier = iterator.next();
      if(userIdentifier.getUci().equals(uci)) {
        iterator.remove();
      }
    }
    LOGGER.debug("onDisconnect:{}", uci);
  }

  /**
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception
   */
  private List<Bag> processCommand(final Class clazz, final String uci, final ChatData data)
      throws UserOwnerOfAnotherRoomException, RoomAlreadyExistsException,
      UserConnectedToAnotherRoomException, UserNotConnectedException, RoomNotFoundException,
      UserDoesNotBelongToAnyRoomException, DistinctRoomException {

    switch (data.getCmd()) {
      case ChatCommands.CONNECT:
        return connect(clazz, uci, data);
      case ChatCommands.CREATE_ROOM:
        return createRoom(clazz, uci, data);
      case ChatCommands.DISCONNECT:
        return disconnect(clazz, uci, data);
      case ChatCommands.JOIN_ROOM:
        return join(clazz, uci, data);
      case ChatCommands.KEEP_ALIVE:
        return keepAlive(clazz, uci, data);
      case ChatCommands.LEAVE_ROOM:
        return leaveRoom(clazz, uci, data);
      case ChatCommands.SEND_SUR:
        return sendMessageToSpecificUserInRoom(clazz, uci, data);
      case ChatCommands.SEND_BUR:
        return sendMessageToAllUsersInRoom(clazz, uci, data);
      case ChatCommands.SEND_SGU:
        return sendMessageToSpecificUser(clazz, uci, data);
      case ChatCommands.SEND_BGU:
        return sendMessageToAllUsers(clazz, uci, data);
      case ChatCommands.USERS_ROOM:
        return retrieveUsersFromRoom(clazz, uci, data);
    }
    throw new IllegalStateException("Something wrong happened.");
  }

  /**
   * Connects an user to the server. If user is already connected, updates it's information.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   */
  private List<Bag> connect(final Class clazz, final String uci, final ChatData data) {
    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        listIterator.set(new UserIdentifier(clazz, uci, userIdentifier.getName()));
        found = true;
      }
    }
    if(!found) {
      userIdentifiers.add(new UserIdentifier(clazz, uci, data.getFrom()));
    }

    final List<Bag> responseList = new LinkedList<>();
    responseList.add(createDirectResponse(clazz, uci, data.getFrom(), data.getCmd(),
        ChatCommands.ResponseCode.OK, "You are now connected to the server."));
    return responseList;
  }

  /**
   * Disconnects an user from server. If user is in a room, leaves the room and notifies all users
   * in the same room. If user is owner of the room, notify and remove all users from the room,
   * and remove the room.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   */
  private List<Bag> disconnect(final Class clazz, final String uci, final ChatData data)
      throws UserDoesNotBelongToAnyRoomException, UserConnectedToAnotherRoomException,
      UserNotConnectedException, RoomNotFoundException {

    final List<Bag> responseBags = new LinkedList<>();
    boolean found = false;
    final Iterator<UserIdentifier> iterator = userIdentifiers.iterator();
    while (iterator.hasNext()) {
      final UserIdentifier user = iterator.next();
      if(user.getName().equals(data.getFrom())) {
        found = true;
        final String room = user.getRoom();
        if(!StringUtils.isBlank(room)) {
          if(!rooms.containsKey(room)) {
            throw new IllegalStateException("An inconsistency was found in room properties.");
          }
          final ChatData chatData = new ChatData(data);
          chatData.setMsg(room);
          responseBags.addAll(leaveRoom(clazz, uci, chatData));
        }
        iterator.remove();
        break;
      }
    }

    if(!found) {
      throw new UserNotConnectedException(uci, data.getFrom(), "User " + data.getFrom() + " is " +
          "not connected to the server.");
    }

    responseBags.add(createDirectResponse(clazz, uci, data.getFrom(), data.getCmd(),
        ChatCommands.ResponseCode.OK, "User " + data.getFrom() + " successfully disconnected."));

    return responseBags;
  }

  /**
   * Makes a user ingress into a room for conversation.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception if room does not exist, if user is connected to another room, if user is
   * not connected to the server.
   */
  private List<Bag> join(final Class clazz, final String uci, final ChatData data)
      throws RoomNotFoundException, UserConnectedToAnotherRoomException, UserNotConnectedException {
    final UserIdentifier updatedUserIdentifier = new UserIdentifier(clazz, uci, data.getFrom(),
        data.getMsg());

    final List<Bag> responseBags = new ArrayList<>();

    if(!rooms.containsKey(data.getMsg())) {
      throw new RoomNotFoundException(data.getMsg(), "The " + data.getMsg() + " room was not found.");
    }

    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        if(!StringUtils.isBlank(userIdentifier.getRoom())) {
          throw new UserConnectedToAnotherRoomException(uci, data.getFrom(),
              userIdentifier.getRoom(), data.getMsg(),
              "User " + userIdentifier.getName() + " is connected to another room " +
                  userIdentifier.getRoom());
        }
        listIterator.set(updatedUserIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new UserNotConnectedException(uci, data.getFrom(),
          "User " + data.getFrom() + " is not connected to the server.");
    }

    responseBags.add(createDirectResponse(clazz, uci, data.getFrom(), data.getCmd(),
        ChatCommands.ResponseCode.OK, "User " + data.getFrom()
            + " has successfully joined the room " + data.getMsg() + "."));

    return responseBags;
  }

  /**
   * Removes user from room. If user has created the room, everybody is gonna leave the room.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception if room does not exist, if user is not in the room he is trying to leave,
   * if user is not connected.
   */
  private List<Bag> leaveRoom(final Class clazz, final String uci, final ChatData data)
      throws RoomNotFoundException, UserConnectedToAnotherRoomException, UserNotConnectedException,
      UserDoesNotBelongToAnyRoomException {

    final List<Bag> responseBags = new LinkedList<>();

    if(!rooms.containsKey(data.getMsg())) {
      throw new RoomNotFoundException(data.getMsg(),
          "The " + data.getMsg() + " room was not found.");
    }

    // rooms.key : room name
    // rooms.value : owner user
    for(final Map.Entry<String, String> entry : rooms.entrySet()) {
      if(entry.getValue().equals(data.getFrom())) {
        if(entry.getKey().equals(data.getMsg())) {
          final String room = data.getMsg();

          ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
          while (listIterator.hasNext()) {
            UserIdentifier userIdentifier = listIterator.next();
            if(room.equals(userIdentifier.getRoom())) {
              userIdentifier.setRoom("");
              listIterator.set(userIdentifier);

              // notifying me and others
              responseBags.add(createDirectResponse(clazz, userIdentifier.getUci(), userIdentifier.getName(),
                  ChatCommands.LEAVE_ROOM, ChatCommands.ResponseCode.OK,
                  "User " + userIdentifier.getName() + " was removed from room " + data.getMsg()));
            }
          }

          rooms.remove(room);
          LOGGER.info("Everyone leaves the room because I'm owner.");
          return responseBags;
        } else {
          // I'm owner of a room and I'm trying to leave another room. I'm can only be in one
          // room at a time.
          throw new IllegalStateException("An inconsistency was found in room properties.");
        }
      }
    }

    boolean found = false;
    // when I'm not room's owner.
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        if(StringUtils.isBlank(userIdentifier.getRoom())) {
          throw new UserDoesNotBelongToAnyRoomException(uci, data.getFrom(), data.getMsg(),
              "User " + userIdentifier.getName() + " does not belong to any room.");
        }
        if(!data.getMsg().equals(userIdentifier.getRoom())) {
          throw new UserConnectedToAnotherRoomException(uci, data.getFrom(),
              userIdentifier.getRoom(), data.getMsg(),
              "User " + userIdentifier.getName() + " is connected to another room " +
                  userIdentifier.getRoom());
        }
        userIdentifier.setRoom("");
        listIterator.set(userIdentifier);
        found = true;

        // notifying me about leaving room
        responseBags.add(0, createDirectResponse(clazz, uci, userIdentifier.getName(),
            ChatCommands.LEAVE_ROOM, ChatCommands.ResponseCode.OK,
            "User " + userIdentifier.getName() + " was removed from room " + data.getMsg()));
      } else {
        responseBags.add(createDirectResponse(clazz, userIdentifier.getUci(), userIdentifier.getName(),
            ChatCommands.LEAVE_ROOM, ChatCommands.ResponseCode.OK,
            "User " + data.getFrom() + " left the room " + data.getMsg()));
      }
    }
    if(found) {
      return responseBags;
    }

    throw new UserNotConnectedException(uci, data.getFrom(),
        "User " + data.getFrom() + " is not connected to the server.");
  }

  /**
   * Updates user last connection date.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception if user is not connected.
   */
  private List<Bag> keepAlive(final Class clazz, final String uci, final ChatData data)
      throws UserNotConnectedException {
    final List<Bag> responseBags = new LinkedList<>();

    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        if(!userIdentifier.isActive()) {
          listIterator.remove();
          throw new UserNotConnectedException(uci, data.getFrom(),
              "User " + data.getFrom() + " is not connected to the server.");
        }
        userIdentifier.keepAlive();
        listIterator.set(userIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new UserNotConnectedException(uci, data.getFrom(),
          "User " + data.getFrom() + " is not connected to the server.");
    }

    responseBags.add(createDirectResponse(clazz, uci, data.getFrom(), data.getCmd(),
        ChatCommands.ResponseCode.OK, "Keep alive success for user " + data.getFrom() + "."));
    return responseBags;
  }

  /**
   * Creates a room and sets himself as owner, if it does not exist. You can only create a room
   * if you are connected and if you are not in another room.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception if room already exists, if user is already connected to another room, if
   * user is trying to create a room and is not connected.
   */
  private List<Bag> createRoom(final Class clazz, final String uci, final ChatData data)
      throws RoomAlreadyExistsException, UserOwnerOfAnotherRoomException,
      UserConnectedToAnotherRoomException, UserNotConnectedException {

    if(rooms.containsKey(data.getMsg())) {
      throw new RoomAlreadyExistsException(data.getMsg(),
          "The room " + data.getMsg() + " already exists.");
    }

    for(Map.Entry<String, String> entry : rooms.entrySet()) {
      if(entry.getValue().equals(data.getFrom())) {
        throw new UserOwnerOfAnotherRoomException(uci, data.getFrom(), entry.getKey(),
            data.getMsg(), "The user " + data.getFrom() + " is owner of another room "
            + entry.getKey() + ".");
      }
    }

    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        if(!StringUtils.isBlank(userIdentifier.getRoom())) {
          if(userIdentifier.getRoom().equals(data.getMsg())) {
            throw new IllegalStateException("User ["+data.getFrom()+"] is connected to a room " +
                "that does not exist.");
          }
          throw new UserConnectedToAnotherRoomException(uci, data.getFrom(), userIdentifier.getRoom(),
              data.getMsg(), "The user " + data.getFrom() + " is connected to another room " +
              userIdentifier.getRoom() + ".");
        }
        userIdentifier.keepAlive();
        userIdentifier.setRoom(data.getMsg());
        listIterator.set(userIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new UserNotConnectedException(uci, data.getFrom(),
          "User " + data.getFrom() + " is not connected to the server.");
    }

    rooms.put(data.getMsg(), data.getFrom());

    final List<Bag> responseBags = new LinkedList<>();
    responseBags.add(createDirectResponse(clazz, uci, data.getFrom(), data.getCmd(),
        ChatCommands.ResponseCode.OK, "Room " + data.getMsg() + " successfully created."));

    return responseBags;
  }

  /**
   * Sends a message to a specific user. The user trying to send a message should be connected to
   * the system and in an existent room. The destination user should be connected in the server and
   * be in the same room as the sender user.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception if sender is not connected to the server, if sender room does not exist, if
   * destination is not connected to the server, if destination is not in the same room as sender.
   */
  private List<Bag> sendMessageToSpecificUserInRoom(
      final Class clazz,
      final String uci,
      final ChatData data) throws RoomNotFoundException, UserNotConnectedException, DistinctRoomException {
    final List<Bag> responseBags = new LinkedList<>();

    final Iterator<UserIdentifier> iterator = userIdentifiers.iterator();
    UserIdentifier sender = null;
    UserIdentifier destination = null;
    while(iterator.hasNext()) {
      final UserIdentifier user = iterator.next();
      if(user.getName().equals(data.getFrom())) {
        sender = user;
      }
      if(user.getName().equals(data.getTo())) {
        destination = user;
      }
    }
    if(null == sender) {
      throw new UserNotConnectedException(uci, data.getFrom(),
          "Sender user " + data.getFrom() + " is not connected to the server.");
    }
    if(null == destination) {
      throw new UserNotConnectedException(uci, data.getFrom(),
          "Destination user " + data.getFrom() + " is not connected to the server.");
    }
    if(!rooms.containsKey(sender.getRoom())) {
      throw new RoomNotFoundException(sender.getRoom(), "The " + sender.getRoom()
          + " room was not found.");
    }
    if(!sender.getRoom().equals(destination.getRoom())) {
      throw new DistinctRoomException(sender.getRoom(), destination.getRoom(),
          "The recipient user is not in this room.");
    }

    responseBags.add(createDirectResponse(clazz, uci, data.getFrom(), data.getCmd(),
        ChatCommands.ResponseCode.OK, "Message successfully sent to user " + data.getTo()));
    responseBags.add(new Bag(destination.getUci(), new ChatData(data), destination.getVersion()));

    return responseBags;
  }

  /**
   * Sends a message to all users in a room. The user should be connected to the server. The room
   * should be an existent room. The user should be in the room.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception if sender is not connected to the server, if sender room does not exist,
   * if sender is not in the room he is trying to send a message to.
   */
  private List<Bag> sendMessageToAllUsersInRoom(
      final Class clazz,
      final String uci,
      final ChatData data) throws UserNotConnectedException, RoomNotFoundException, DistinctRoomException {
    final Iterator<UserIdentifier> iterator = userIdentifiers.iterator();
    UserIdentifier sender = null;
    while(iterator.hasNext()) {
      final UserIdentifier user = iterator.next();
      if(user.getName().equals(data.getFrom())) {
        sender = user;
      }
    }
    if(null == sender) {
      throw new UserNotConnectedException(uci, data.getFrom(),
          "Sender user " + data.getFrom() + " is not connected to the server.");
    }
    if(!rooms.containsKey(sender.getRoom())) {
      throw new RoomNotFoundException(sender.getRoom(), "The " + sender.getRoom() + " room was not " +
          "found.");
    }
    if(!sender.getRoom().equals(data.getTo())) {
      throw new DistinctRoomException(sender.getRoom(), data.getTo(),
          "User is trying to send a message to a different room.");
    }

    // TODO: iterate over all users and build the correct data to send back
    return null;
  }

  /**
   * Sends a message to a specific user. The user trying to send a message should be connected to
   * the system. The destination user should be connected in the server.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception if sender is not connected to the server, if destination is not connected
   * to the server.
   */
  private List<Bag> sendMessageToSpecificUser(final Class clazz, final String uci, final ChatData
      data) throws UserNotConnectedException {
    final Iterator<UserIdentifier> iterator = userIdentifiers.iterator();
    UserIdentifier sender = null;
    UserIdentifier destination = null;
    while(iterator.hasNext()) {
      final UserIdentifier user = iterator.next();
      if(user.getName().equals(data.getFrom())) {
        sender = user;
      }
      if(user.getName().equals(data.getTo())) {
        destination = user;
      }
    }
    if(null == sender) {
      throw new UserNotConnectedException(uci, data.getFrom(),
          "Sender user " + data.getFrom() + " is not connected to the server.");
    }
    if(null == destination) {
      throw new UserNotConnectedException(uci, data.getFrom(),
          "Destination user " + data.getFrom() + " is not connected to the server.");
    }

    // TODO: build correct data to send back
    return null;
  }

  /**
   * Sends a message to all users. The user should be connected to the server.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception if sender is not connected to the server.
   */
  private List<Bag> sendMessageToAllUsers(final Class clazz, final String uci, final ChatData data)
      throws UserNotConnectedException {
    final Iterator<UserIdentifier> iterator = userIdentifiers.iterator();
    UserIdentifier sender = null;
    while(iterator.hasNext()) {
      final UserIdentifier user = iterator.next();
      if(user.getName().equals(data.getFrom())) {
        sender = user;
      }
    }
    if(null == sender) {
      throw new UserNotConnectedException(uci, data.getFrom(),
          "Sender user " + data.getFrom() + " is not connected to the server.");
    }

    // TODO: iterate over all users and build the correct data to send back
    return null;
  }

  /**
   * Retrieve all users from a specific room. User should be in the same room as he is trying to
   * retrieve this information.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @return
   * @throws Exception if room does not exists, if user is not connected, if user is not in the
   * same room as he is trying to retrieve data.
   */
  private List<Bag> retrieveUsersFromRoom(Class clazz, String uci, ChatData data) throws RoomNotFoundException {
    final List<Bag> responseBags = new LinkedList<>();
    final List<String> userNames = new LinkedList<>();

    if(!rooms.containsKey(data.getMsg())) {
      throw new RoomNotFoundException(data.getMsg(), "The " + data.getMsg() + " room was not found.");
    }

    //TODO: check other conditions please

    final Iterator<UserIdentifier> iterator = userIdentifiers.iterator();
    while(iterator.hasNext()) {
      final UserIdentifier user = iterator.next();
      if(data.getMsg().equals(user.getRoom())) {
        userNames.add(user.getName());
      }
    }

    responseBags.add(createDirectResponse(clazz, uci, data.getFrom(), data.getCmd(),
        ChatCommands.ResponseCode.OK, new Gson().toJson(userNames)));

    return responseBags;
  }

  /**
   * Creates a direct response from server. The response is for a specific command and can be of
   * success or error. A status message should be send as well.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param userTo a String representing the user recipient.
   * @param command a String representing the command.
   * @param status a ChatCommands.ResponseCode status.
   * @param message a String representing a status message.
   * @return a Bag response containing a response from server.
   */
  private Bag createDirectResponse(
      final Class clazz,
      final String uci,
      final String userTo,
      final String command,
      final String status,
      final String message) {

    final ChatData responseData = new ChatData("server", userTo, command +
        status, message == null ? "" : message);

    return new Bag(uci, responseData, clazz);
  }


  private List<Bag> createResponse(final Class clazz, final String uci, final ChatData data,
                                   final String status) {

    final List<Bag> response = new LinkedList<>();
    final ChatData responseData = new ChatData(data.getTo(), data.getFrom(), data.getCmd() + status, "" );

    switch (data.getCmd()) {
      case ChatCommands.CONNECT: {

        if (ChatCommands.ResponseCode.OK.equals(status)) {
          responseData.setMsg("ok");
        } else if (ChatCommands.ResponseCode.ERROR.equals(status)) {
          responseData.setMsg("error");
        }
        final Bag bag = new Bag(uci, responseData, clazz);
        response.add(bag);
        break;
      }
      case ChatCommands.CREATE_ROOM: {
        if (ChatCommands.ResponseCode.OK.equals(status)) {
          responseData.setMsg("room " + data.getMsg() + " successfully created.");
        } else if (ChatCommands.ResponseCode.ERROR.equals(status)) {
          responseData.setMsg("error while creating room " + data.getMsg());
        }
        final Bag bag = new Bag(uci, responseData, clazz);
        response.add(bag);
        break;
      }
      case ChatCommands.DISCONNECT: {





        //TODO
        if (ChatCommands.ResponseCode.OK.equals(status)) {
          responseData.setMsg("room " + data.getMsg() + " successfully created.");
        } else if (ChatCommands.ResponseCode.ERROR.equals(status)) {
          responseData.setMsg("error while creating room " + data.getMsg());
        }
        final Bag bag = new Bag(uci, responseData, clazz);
        response.add(bag);
        break;





      }
      case ChatCommands.JOIN_ROOM:
        if(ChatCommands.ResponseCode.OK.equals(status)) {

        } else if (ChatCommands.ResponseCode.ERROR.equals(status)) {

        }
        break;
      case ChatCommands.KEEP_ALIVE:
        if(ChatCommands.ResponseCode.OK.equals(status)) {

        } else if (ChatCommands.ResponseCode.ERROR.equals(status)) {

        }
        break;
      case ChatCommands.LEAVE_ROOM:
        if(ChatCommands.ResponseCode.OK.equals(status)) {

        } else if (ChatCommands.ResponseCode.ERROR.equals(status)) {

        }
        break;
      case ChatCommands.SEND_SUR:
        if(ChatCommands.ResponseCode.OK.equals(status)) {

        } else if (ChatCommands.ResponseCode.ERROR.equals(status)) {

        }
        break;
      case ChatCommands.SEND_BUR:
        if(ChatCommands.ResponseCode.OK.equals(status)) {

        } else if (ChatCommands.ResponseCode.ERROR.equals(status)) {

        }
        break;
      case ChatCommands.SEND_SGU:
        if(ChatCommands.ResponseCode.OK.equals(status)) {

        } else if (ChatCommands.ResponseCode.ERROR.equals(status)) {

        }
        break;
      case ChatCommands.SEND_BGU:
        if(ChatCommands.ResponseCode.OK.equals(status)) {

        } else if (ChatCommands.ResponseCode.ERROR.equals(status)) {

        }
        break;
      case ChatCommands.USERS_ROOM:
        //TODO
        break;
    }



    return response;

  }

}
