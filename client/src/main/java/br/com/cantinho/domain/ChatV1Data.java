package br.com.cantinho.domain;

public class ChatV1Data extends Versionable {

  protected static final String VERSION = ChatV1Data.class.getSimpleName();

  private String from;
  private String to;
  private String cmd;
  private String msg;

  public ChatV1Data() {
    super(VERSION);
  }

  public ChatV1Data(String from, String to, String cmd, String msg) {
    super(VERSION);
    this.from = from;
    this.to = to;
    this.cmd = cmd;
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

  public String getCmd() {
    return cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "ChatV1Data{" +
        "from='" + from + '\'' +
        ", to='" + to + '\'' +
        ", cmd='" + cmd + '\'' +
        ", msg='" + msg + '\'' +
        '}';
  }
}
