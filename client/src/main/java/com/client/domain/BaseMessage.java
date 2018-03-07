package com.client.domain;

public class BaseMessage {

  /**
   * A string representing the default message version.
   */
  private static final String DEFAULT_VERSION = "1.0";

  /**
   * A string version.
   */
  private String ver;

  /**
   * Instantiates a default BaseMessage.
   */
  public BaseMessage() {
    this.ver = DEFAULT_VERSION;
  }

  /**
   * Instantiates a BaseMessage with a given version.
   * @param ver a string version
   */
  public BaseMessage(final String ver) {
    this.ver = ver;
  }

  /**
   * Retrieves BaseMessage version.
   * @return a string version.
   */
  public String getVer() {
    return ver;
  }

  /**
   * Defines a new value for version.
   * @param ver a string new value for version.
   */
  public void setVer(final String ver) {
    this.ver = ver;
  }

  @Override
  public String toString() {
    return "BaseMessage{" +
        "ver='" + ver + '\'' +
        '}';
  }
}
