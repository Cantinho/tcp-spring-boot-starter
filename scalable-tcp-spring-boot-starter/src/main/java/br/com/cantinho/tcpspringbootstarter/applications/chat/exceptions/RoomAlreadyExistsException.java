package br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions;

public class RoomAlreadyExistsException extends Exception {

  private String room;

  public RoomAlreadyExistsException(final String room) {
    this.room = room;
  }

  public RoomAlreadyExistsException(final String room, final String message) {
    super(message);
    this.room = room;
  }

  public String getRoom() {
    return room;
  }

}
