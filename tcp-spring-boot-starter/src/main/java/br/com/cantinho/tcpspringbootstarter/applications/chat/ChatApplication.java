package br.com.cantinho.tcpspringbootstarter.applications.chat;

import br.com.cantinho.tcpspringbootstarter.applications.Application;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.ChatData;
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
      return (new Date().getTime() - lastUpdatedAt) <= 30000;
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
   * @param data a ChatData room instance.
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
        break;
      case ChatCommands.SEND_BUR:
        break;
      case ChatCommands.SEND_SGU:
        break;
      case ChatCommands.SEND_BGU:
        break;
      case ChatCommands.USERS_ROOM:
        break;
    }
  }

  /**
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData room instance.
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
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData room instance.
   * @throws Exception
   */
  private void join(final Class clazz, final String uci, final ChatData data) throws Exception {
    final UserIdentifier updatedUserIdentifier = new UserIdentifier(clazz, uci, data.getFrom(),
        data.getMsg());

    if(!rooms.containsKey(data.getMsg())) {
      throw new Exception("room does not exist.");
    }

    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        listIterator.set(updatedUserIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new Exception("user not connected");
    }
  }

  /**
   * Removes user from room. If user has created the room, everybody is gonna leave the room.
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData room instance.
   * @throws Exception if user
   */
  private void leaveRoom(final Class clazz, final String uci, final ChatData data) throws
      Exception {

    if(!rooms.containsKey(data.getMsg())) {
      throw new Exception("room does not exist.");
    }

    for(final Map.Entry<String, String> entry : rooms.entrySet()) {
      if(entry.getValue().equals(data.getFrom())) {
        if(entry.getKey().equals(data.getMsg())) {
          // todo mundo remover exclusicv eu
          LOGGER.info("Everyone leaves the room because I'm owner.");
          // TODO returns complete response
          return;
        } else {
          throw new Exception("an inconsistency was found in room properties");
        }
      }
    }

    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        userIdentifier.setRoom("");
        listIterator.set(userIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new Exception("user not connected.");
    }
  }

  /**
   * Updates user last connection date.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData room instance.
   * @throws Exception if user is not connected.
   */
  private void keepAlive(final Class clazz, final String uci, final ChatData data) throws Exception {
    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        userIdentifier.keepAlive();
        listIterator.set(userIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new Exception("user not connected.");
    }
  }

  /**
   * Creates a room and sets himself as owner, if it does not exist.
   *
   * @param clazz a Class version instance.
   * @param uci a String Unique Connection Identifier.
   * @param data a ChatData room instance.
   * @throws Exception if room already exists, if user is already connected to another room, if
   * user is trying to create a room and is not connected.
   */
  private void createRoom(final Class clazz, final String uci, final ChatData data) throws Exception {

    if(rooms.containsKey(data.getMsg())) {
      throw new Exception("room already created.");
    }
    if(rooms.containsValue(data.getFrom())) {
      throw new Exception("user is already connected to another room.");
    }

    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(data.getFrom())) {
        userIdentifier.keepAlive();
        userIdentifier.setRoom(data.getMsg());
        listIterator.set(userIdentifier);
        found = true;
      }
    }
    if(!found) {
      throw new Exception("user not connected.");
    }

    rooms.put(data.getMsg(), data.getFrom());
  }


}
