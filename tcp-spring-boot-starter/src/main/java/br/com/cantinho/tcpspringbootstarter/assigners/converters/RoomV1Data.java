package br.com.cantinho.tcpspringbootstarter.assigners.converters;

public class RoomV1Data extends Versionable {

  protected static final String VERSION = RoomV1Data.class.getSimpleName();

  private String from;

  private String to;

  private String msg;

  public RoomV1Data(){}

  public RoomV1Data(final String from, final String to, final String msg) {
    super(VERSION);
    this.from = from;
    this.to = to;
    this.msg = msg;
  }

  @Override
  public String getVer() {
    return VERSION;
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

  @Override
  public String toString() {
    return "RoomV1Data{" +
        "from='" + from + '\'' +
        ", to='" + to + '\'' +
        ", msg='" + msg + '\'' +
        '}';
  }
}
