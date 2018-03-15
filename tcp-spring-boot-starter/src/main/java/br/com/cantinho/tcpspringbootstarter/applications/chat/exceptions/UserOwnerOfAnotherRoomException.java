package br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions;

public class UserOwnerOfAnotherRoomException extends Exception {

  private String uci;
  private String user;
  private String currentRoom;
  private String newRoom;

  public UserOwnerOfAnotherRoomException(
      final String uci,
      final String user,
      final String currentRoom,
      final String newRoom) {
    this.uci = uci;
    this.user = user;
    this.currentRoom = currentRoom;
    this.newRoom = newRoom;
  }

  public UserOwnerOfAnotherRoomException(
      final String uci,
      final String user,
      final String currentRoom,
      final String newRoom,
      final String message) {
    super(message);
    this.uci = uci;
    this.user = user;
    this.currentRoom = currentRoom;
    this.newRoom = newRoom;
  }
}
