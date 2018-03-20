package br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions;

public class UserNotConnectedException extends Exception {

  private String uci;
  private String user;

  public UserNotConnectedException(final String uci, final String user) {
    this.uci = uci;
    this.user = user;
  }

  public UserNotConnectedException(final String uci, final String user, final String message) {
    super(message);
    this.uci = uci;
    this.user = user;
  }

  public String getUci() {
    return uci;
  }

  public String getUser() {
    return user;
  }
}
