package br.com.cantinho.tcpspringbootstarter.applications.chat;

import br.com.cantinho.tcpspringbootstarter.applications.Application;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

    processChatData(clazz, uci, request);

    //FIXME: analyze before removing
    /*boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(request.getFrom())) {
        listIterator.set(new UserIdentifier(uci, userIdentifier.getName(), clazz));
        found = true;
      }
    }
    if(!found) {
      userIdentifiers.add(new UserIdentifier(uci, request.getFrom(), clazz));
    }
    for(UserIdentifier id : userIdentifiers) {
      LOGGER.info("id: {}", id.toString());
    }

    final List<Bag> returnList = new ArrayList<>();
    if(null != request.getTo() && !request.getTo().isEmpty()) {
      for(final UserIdentifier userIdentifier : userIdentifiers) {
        if(userIdentifier.getName().equals(request.getTo())) {
          final String currentUci = userIdentifier.getUci();
          final ChatData chatData = new ChatData(request.getFrom(), userIdentifier.getName(),
              request.getCmd(), request.getMsg());
          returnList.add(new Bag(currentUci, chatData, userIdentifier.getVersion()));
        }
      }
    } else {
      for(final UserIdentifier userIdentifier : userIdentifiers) {
        final String currentUci = userIdentifier.getUci();
        final ChatData chatData = new ChatData(request.getFrom(), userIdentifier.getName(),
            request.getCmd(), request.getMsg());
        returnList.add(new Bag(currentUci, chatData, userIdentifier.getVersion()));
      }
    }*/

    return null;
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

  private class UserIdentifier{

    private static final long ALIVE_TIME_IN_MILLIS = 30000;
    private Class version;
    private String uci;
    private String name;
    private String room;
    private Long lastUpdatedAt;

    /**
     * Returns true if user has made any interaction with server in the last 30 seconds.
     * @return true if user is active, false otherwise.
     */
    public boolean isActive(){
      return (new Date().getTime() - lastUpdatedAt) <= ALIVE_TIME_IN_MILLIS;
    }

    /**
     * Updates last time user hast made any interaction with server.
     */
    public void keepAlive(){
      this.lastUpdatedAt = new Date().getTime();
    }

    public UserIdentifier(Class version, String uci, String name) {
      this.version = version;
      this.uci = uci;
      this.name = name;
      this.room = "";
      this.lastUpdatedAt = new Date().getTime();
    }

    public UserIdentifier(Class version, String uci, String name, String room) {
      this.version = version;
      this.uci = uci;
      this.name = name;
      this.room = room;
      this.lastUpdatedAt = new Date().getTime();
    }

    public Class getVersion() {
      return version;
    }

    public void setVersion(Class version) {
      this.version = version;
    }

    public String getUci() {
      return uci;
    }

    public void setUci(String uci) {
      this.uci = uci;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getRoom() {
      return room;
    }

    public void setRoom(String room) {
      this.room = room;
    }
  }

  public class Bag {
    private String uci;
    private ChatData chatData;
    private Class version;

    public Bag(final String uci, final ChatData chatData, final Class version) {
      this.uci = uci;
      this.chatData = chatData;
      this.version = version;
    }

    public String getUci() {
      return uci;
    }

    public void setUci(String uci) {
      this.uci = uci;
    }

    public ChatData getChatData() {
      return chatData;
    }

    public void setChatData(ChatData chatData) {
      this.chatData = chatData;
    }

    public Class getVersion() {
      return version;
    }

    public void setVersion(Class version) {
      this.version = version;
    }
  }

  private void processChatData(final Class clazz, final String uci, final ChatData request){

  }

  /**
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception
   */
  private void processCommand(final Class clazz, final String uci, final ChatData data)
      throws Exception {
    switch (data.getCmd()) {
      case ChatCommands.CONNECT:
        connect(clazz, uci, data);
        break;
      case ChatCommands.CREATE_ROOM:
        createRoom(clazz, uci, data);
        break;
      case ChatCommands.DISCONNECT:
        disconnect(clazz, uci, data);
        break;
      case ChatCommands.JOIN_ROOM:
        join(clazz, uci, data);
        break;
      case ChatCommands.KEEP_ALIVE:
        keepAlive(clazz, uci, data);
        break;
      case ChatCommands.LEAVE_ROOM:
        leaveRoom(clazz, uci, data);
        break;
      case ChatCommands.SEND_SUR:
        sendMessageToSpecificUserInRoom(clazz, uci, data);
        break;
      case ChatCommands.SEND_BUR:
        sendMessageToAllUsersInRoom(clazz, uci, data);
        break;
      case ChatCommands.SEND_SGU:
        sendMessageToSpecificUser(clazz, uci, data);
        break;
      case ChatCommands.SEND_BGU:
        sendMessageToAllUsers(clazz, uci, data);
        break;
      case ChatCommands.USERS_ROOM:
        break;
    }
  }

  /**
   * Connects an user to the server. If user is already connected, updates it's information.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   */
  private void connect(final Class clazz, final String uci, final ChatData data) {
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
  private void disconnect(final Class clazz, final String uci, final ChatData data) {
    try {
      leaveRoom(clazz, uci, data);
    } catch (RoomNotFoundException e) {
      e.printStackTrace();
    } catch (UserConnectedToAnotherRoomException e) {
      e.printStackTrace();
    } catch (UserNotConnectedException e) {
      e.printStackTrace();
    } catch (UserDoesNotBelongToAnyRoomException e) {
      e.printStackTrace();
    }
    // TODO: notify user and disconnect it.
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
  private void join(final Class clazz, final String uci, final ChatData data)
      throws RoomNotFoundException, UserConnectedToAnotherRoomException, UserNotConnectedException {
    final UserIdentifier updatedUserIdentifier = new UserIdentifier(clazz, uci, data.getFrom(),
        data.getMsg());

    if(!rooms.containsKey(data.getMsg())) {
      throw new RoomNotFoundException(data.getMsg());
    }

    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(!StringUtils.isBlank(userIdentifier.getRoom())) {
        throw new UserConnectedToAnotherRoomException(uci, data.getFrom(),
            userIdentifier.getRoom(), data.getMsg());
      }
      if(userIdentifier.getName().equals(data.getFrom())) {
        listIterator.set(updatedUserIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new UserNotConnectedException(uci, data.getFrom());
    }
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
  private void leaveRoom(final Class clazz, final String uci, final ChatData data)
      throws RoomNotFoundException, UserConnectedToAnotherRoomException, UserNotConnectedException,
      UserDoesNotBelongToAnyRoomException {

    if(!rooms.containsKey(data.getMsg())) {
      throw new RoomNotFoundException(data.getMsg());
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
              //TODO: notify user that room owner has disconnected from room
            }
          }

          rooms.remove(room);
          LOGGER.info("Everyone leaves the room because I'm owner.");
          return;
        } else {
          // I'm owner of a room and I'm trying to leave another room. I'm can only be in one
          // room at a time.
          throw new IllegalStateException("An inconsistency was found in room properties");
        }
      }
    }

    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        if(StringUtils.isBlank(userIdentifier.getRoom())) {
          throw new UserDoesNotBelongToAnyRoomException(uci, data.getFrom(), data.getMsg());
        }
        if(!data.getMsg().equals(userIdentifier.getRoom())) {
          throw new UserConnectedToAnotherRoomException(uci, data.getFrom(),
              userIdentifier.getRoom(), data.getMsg());
        }
        userIdentifier.setRoom("");
        listIterator.set(userIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new UserNotConnectedException(uci, data.getFrom());
    }
  }

  /**
   * Updates user last connection date.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception if user is not connected.
   */
  private void keepAlive(final Class clazz, final String uci, final ChatData data)
      throws UserNotConnectedException {
    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        if(!userIdentifier.isActive()) {
          // TODO: notify user and disconnect
          return;
        }
        userIdentifier.keepAlive();
        listIterator.set(userIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new UserNotConnectedException(uci, data.getFrom());
    }
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
  private void createRoom(final Class clazz, final String uci, final ChatData data)
      throws RoomAlreadyExistsException, UserOwnerOfAnotherRoomException,
      UserConnectedToAnotherRoomException, UserNotConnectedException {

    if(rooms.containsKey(data.getMsg())) {
      throw new RoomAlreadyExistsException(data.getMsg());
    }

    for(Map.Entry<String, String> entry : rooms.entrySet()) {
      if(entry.getValue().equals(data.getFrom())) {
        throw new UserOwnerOfAnotherRoomException(uci, data.getFrom(), entry.getKey(), data.getMsg());
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
              data.getMsg());
        }
        userIdentifier.keepAlive();
        userIdentifier.setRoom(data.getMsg());
        listIterator.set(userIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new UserNotConnectedException(uci, data.getFrom());
    }

    rooms.put(data.getMsg(), data.getFrom());
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
  private void sendMessageToSpecificUserInRoom(final Class clazz, final String uci, final ChatData data) {

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
  private void sendMessageToAllUsersInRoom(final Class clazz, final String uci, final ChatData data) {

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
  private void sendMessageToSpecificUser(final Class clazz, final String uci, final ChatData data) {

  }

  /**
   * Sends a message to all users. The user should be connected to the server.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData data instance.
   * @throws Exception if sender is not connected to the server.
   */
  private void sendMessageToAllUsers(final Class clazz, final String uci, final ChatData data) {

  }

}
