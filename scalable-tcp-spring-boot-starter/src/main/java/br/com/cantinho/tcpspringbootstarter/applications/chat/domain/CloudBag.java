package br.com.cantinho.tcpspringbootstarter.applications.chat.domain;

import br.com.cantinho.tcpspringbootstarter.assigners.converters.ChatData;

/**
 * Cloud Bad.
 */
public class CloudBag {

    private String id;
    private String version;
    private String event;
    private ChatData chatData;

  public CloudBag(final String id, final String version, final String event, final ChatData chatData) {
    this.id = id;
    this.version = version;
    this.event = event;
    this.chatData = chatData;
  }

  public String getId() {
    return id;
  }

  public String getVersion() {
    return version;
  }

  public String getEvent() {
    return event;
  }

  public ChatData getChatData() {
    return chatData;
  }

  @Override
  public String toString() {
    return "CloudBag{" +
        "id='" + id + '\'' +
        ", version='" + version + '\'' +
        ", event='" + event + '\'' +
        ", chatData=" + chatData +
        '}';
  }
}