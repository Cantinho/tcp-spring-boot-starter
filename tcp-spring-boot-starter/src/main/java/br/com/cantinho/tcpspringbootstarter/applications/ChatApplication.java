package br.com.cantinho.tcpspringbootstarter.applications;

import br.com.cantinho.tcpspringbootstarter.assigners.converters.ChatData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ChatApplication implements Application {

  /**
   * A logger instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ChatApplication.class.getCanonicalName());

  private final List<UserIdentifier> userIdentifiers = Collections.synchronizedList(new ArrayList<>());

  @Override
  public Object process(Object... parameters) {
    LOGGER.debug("process");
    final String uci = (String) parameters[0];
    final Class clazz = (Class) parameters[1];
    final ChatData request = (ChatData) parameters[2];

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

}
