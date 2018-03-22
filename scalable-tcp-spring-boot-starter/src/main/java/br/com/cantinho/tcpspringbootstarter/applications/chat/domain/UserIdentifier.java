package br.com.cantinho.tcpspringbootstarter.applications.chat.domain;

import java.util.Date;

public class UserIdentifier {

  private static final long ALIVE_TIME_IN_MILLIS = 30000;
  private Class version;
  private String uci;
  private String name;
  private String room;
  private Long lastUpdatedAt;

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

  @Override
  public String toString() {
    return "UserIdentifier{" +
        "version=" + version +
        ", uci='" + uci + '\'' +
        ", name='" + name + '\'' +
        ", room='" + room + '\'' +
        ", lastUpdatedAt=" + lastUpdatedAt +
        '}';
  }
}
