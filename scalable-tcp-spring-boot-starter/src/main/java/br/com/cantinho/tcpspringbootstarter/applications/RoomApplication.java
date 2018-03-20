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
    final Class clazz = (Class) parameters[1];
    final RoomData request = (RoomData) parameters[2];

    boolean found = false;
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
          final RoomData roomData = new RoomData(request.getFrom(), userIdentifier.getName(), request.getMsg());
          returnList.add(new Bag(currentUci, roomData, userIdentifier.getVersion()));
        }
      }
    } else {
      for(final UserIdentifier userIdentifier : userIdentifiers) {
        final String currentUci = userIdentifier.getUci();
        final RoomData roomData = new RoomData(request.getFrom(), userIdentifier.getName(), request.getMsg());
        returnList.add(new Bag(currentUci, roomData, userIdentifier.getVersion()));
      }
    }


    return returnList;
  }

  @Override
  public Object onConnect(String uci) {
    LOGGER.debug("onConnect:{}", uci);
    return new Object();
  }

  @Override
  public Object onDisconnect(final String uci) {
    final Iterator<UserIdentifier> iterator = userIdentifiers.iterator();
    while(iterator.hasNext()) {
      final UserIdentifier userIdentifier = iterator.next();
      if(userIdentifier.getUci().equals(uci)) {
        iterator.remove();
      }
    }
    LOGGER.debug("onDisconnect:{}", uci);
    return new Object();
  }

  private class UserIdentifier{

    private String uci;
    private String name;
    private Class version;

    public UserIdentifier() {
    }

    private UserIdentifier(final String uci, final String name, final Class version) {
      this.uci = uci;
      this.name = name;
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

    public Class getVersion() {
      return version;
    }

    public void setVersion(Class version) {
      this.version = version;
    }

    @Override
    public String toString() {
      return "UserIdentifier{" +
          "uci='" + uci + '\'' +
          ", name='" + name + '\'' +
          ", version=" + version +
          '}';
    }
  }

  public class Bag {
    private String uci;
    private RoomData roomData;
    private Class version;

    public Bag(final String uci, final RoomData roomData, final Class version) {
      this.uci = uci;
      this.roomData = roomData;
      this.version = version;
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

    public Class getVersion() {
      return version;
    }

    public void setVersion(Class version) {
      this.version = version;
    }
  }

}
