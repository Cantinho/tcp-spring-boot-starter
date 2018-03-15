package br.com.cantinho.tcpspringbootstarter.applications.chat.exceptions;

public class DistinctRoomException extends Exception {

  private String room;
  private String otherRoom;

  public DistinctRoomException(final String room, final String otherRoom) {
    this.room = room;
    this.otherRoom = otherRoom;
  }

  public DistinctRoomException(final String room, final String otherRoom, final String message) {
    super(message);
    this.room = room;
    this.otherRoom = otherRoom;
  }

  public String getRoom() {
    return room;
  }

  public String getOtherRoom() {
    return otherRoom;
  }
}
