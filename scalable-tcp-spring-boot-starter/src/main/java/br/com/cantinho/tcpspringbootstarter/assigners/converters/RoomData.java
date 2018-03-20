package br.com.cantinho.tcpspringbootstarter.assigners.converters;

public class RoomData {

  private String from;
  private String to;
  private String msg;

  public RoomData() {
  }

  public RoomData(String from, String to, String msg) {
    this.from = from;
    this.to = to;
    this.msg = msg;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}