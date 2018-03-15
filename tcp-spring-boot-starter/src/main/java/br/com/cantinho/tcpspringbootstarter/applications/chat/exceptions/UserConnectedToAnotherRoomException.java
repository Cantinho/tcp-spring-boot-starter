package br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions;

public class UserConnectedToAnotherRoomException extends Exception {

  private String uci;
  private String user;
  private String ownedRoom;
  private String newRoom;

  public UserConnectedToAnotherRoomException(
      final String uci,
      final String user,
      final String ownedRoom,
      final String newRoom) {
    this.uci = uci;
    this.user = user;
    this.ownedRoom = ownedRoom;
    this.newRoom = newRoom;
  }

  public UserConnectedToAnotherRoomException(
      final String uci,
      final String user,
      final String ownedRoom,
      final String newRoom,
      final String message) {
    super(message);
    this.uci = uci;
    this.user = user;
    this.ownedRoom = ownedRoom;
    this.newRoom = newRoom;
  }
}
