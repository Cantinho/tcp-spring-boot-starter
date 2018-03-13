package br.com.cantinho.tcpspringbootstarter.applications;

import br.com.cantinho.tcpspringbootstarter.assigners.converters.RoomData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RoomApplication implements Application {

  /**
   * A logger instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RoomApplication.class.getCanonicalName());

  private final List<UserIdentifier> userIdentifiers = Collections.synchronizedList(new ArrayList<>());

  @Override
  public Object process(Object... parameters) {
    LOGGER.debug("process");
    final String uci = (String) parameters[0];
    final RoomData request = (RoomData) parameters[1];

    boolean found = false;
    final ListIterator<UserIdentifier> listIterator = userIdentifiers.listIterator();
    while (listIterator.hasNext()) {
      final UserIdentifier userIdentifier = listIterator.next();
      if(userIdentifier.getName().equals(request.getFrom())) {
        listIterator.set(new UserIdentifier(uci, userIdentifier.getName()));
        found = true;
      }
    }
    if(!found) {
      userIdentifiers.add(new UserIdentifier(uci, request.getFrom()));
    }
    for(UserIdentifier id : userIdentifiers) {
      LOGGER.info("id: {}", id.toString());
    }

    final List<Bag> returnList = new ArrayList<>();
    for(final UserIdentifier userIdentifier : userIdentifiers) {
      final String currentUci = userIdentifier.getUci();
      final RoomData roomData = new RoomData(request.getFrom(), userIdentifier.getName(), request.getMsg());
      returnList.add(new Bag(currentUci, roomData));
    }

    return returnList;
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

    private String uci;
    private String name;

    public UserIdentifier() {
    }

    public UserIdentifier(String uci, String name) {
      this.uci = uci;
      this.name = name;
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

    @Override
    public String toString() {
      return "UserIdentifier{" +
          "uci='" + uci + '\'' +
          ", name='" + name + '\'' +
          '}';
    }
  }

  public class Bag {
    private String uci;
    private RoomData roomData;

    public Bag(String uci, RoomData roomData) {
      this.uci = uci;
      this.roomData = roomData;
    }

    public String getUci() {
      return uci;
    }

    public void setUci(String uci) {
      this.uci = uci;
    }

    public RoomData getRoomData() {
      return roomData;
    }

    public void setRoomData(RoomData roomData) {
      this.roomData = roomData;
    }
  }

}
