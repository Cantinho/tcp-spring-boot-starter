package br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions;

public class RoomNotFoundException extends Exception {

  private String room;

  public RoomNotFoundException(final String room) {
    this.room = room;
  }

  public RoomNotFoundException(final String room, final String message) {
    super(message);
    this.room = room;
  }

  public String getRoom() {
    return room;
  }
}
