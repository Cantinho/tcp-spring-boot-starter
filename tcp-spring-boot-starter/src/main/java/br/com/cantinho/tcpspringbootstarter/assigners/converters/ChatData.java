package br.com.cantinho.tcpspringbootstarter.assigners.converters;

public class ChatData {

  private String from;
  private String to;
  private String cmd;
  private String msg;

  public ChatData() {
  }

  public ChatData(final ChatData data) {
    this.from = data.getFrom();
    this.to = data.getTo();
    this.cmd = data.getCmd();
    this.msg = data.getMsg();
  }

  public ChatData(String from, String to, String cmd, String msg) {
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
    return "ChatData{" +
        "from='" + from + '\'' +
        ", to='" + to + '\'' +
        ", cmd='" + cmd + '\'' +
        ", msg='" + msg + '\'' +
        '}';
  }
}