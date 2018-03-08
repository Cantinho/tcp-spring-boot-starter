package br.com.cantinho.domain;

public class SimpleMessage extends BaseMessage {

  /**
   * A string representing SimpleMessage from.
   */
  private String from;

  /**
   * A string representing SimpleMessage to.
   */
  private String to;

  /**
   * A string representing SimpleMessage message.
   */
  private String msg;

  /**
   * Instantiates a default SimpleMessage only with default version.
   */
  public SimpleMessage() {
    super();
  }

  /**
   * Instantiates a new SimpleMessage with default version.
   * @param from a given string from.
   * @param to a given string to.
   * @param msg a given string message.
   */
  public SimpleMessage(String from, String to, String msg) {
    super();
    this.from = from;
    this.to = to;
    this.msg = msg;
  }

  /**
   * Instantiates a new SimpleMessage with a different version than default.
   * @param ver a given string version.
   * @param from a given string from.
   * @param to a given string to.
   * @param msg a given string message.
   */
  public SimpleMessage(String ver, String from, String to, String msg) {
    super(ver);
    this.from = from;
    this.to = to;
    this.msg = msg;
  }

  /**
   * Retrieves a string representing SimpleMessage from.
   * @return a string from.
   */
  public String getFrom() {
    return from;
  }

  /**
   * Defines a new value for string from.
   * @param from a given string from.
   */
  public void setFrom(final String from) {
    this.from = from;
  }

  /**
   * Retrieves a string representing SimpleMessage to.
   * @return a string to.
   */
  public String getTo() {
    return to;
  }

  /**
   * Defines a new value for string to.
   * @param to a given string to.
   */
  public void setTo(final String to) {
    this.to = to;
  }

  /**
   * Retrieves a string representing SimpleMessage message.
   * @return a string message.
   */
  public String getMsg() {
    return msg;
  }

  /**
   * Defines a new value for string message.
   * @param msg a given message.
   */
  public void setMsg(final String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "SimpleMessage{" +
        "ver='" + getVer() + '\'' +
        ", from='" + getFrom() + '\'' +
        ", to='" + getTo() + '\'' +
        ", msg='" + getMsg() + '\'' +
        '}';
  }
}
