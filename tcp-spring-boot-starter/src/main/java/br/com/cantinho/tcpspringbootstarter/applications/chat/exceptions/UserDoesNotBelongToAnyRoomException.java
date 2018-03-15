package br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions;

public class UserDoesNotBelongToAnyRoomException extends Exception {

  private String uci;
  private String user;
  private String room;

  public UserDoesNotBelongToAnyRoomException(
      final String uci,
      final String user,
      final String room) {
    this.uci = uci;
    this.user = user;
    this.room = room;
  }

  public UserDoesNotBelongToAnyRoomException(
      final String uci,
      final String user,
      final String room,
      final String message) {
    super(message);
    this.uci = uci;
    this.user = user;
    this.room = room;
  }
}
