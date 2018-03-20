package br.com.cantinho.tcpspringbootstarter.applications.chat.domain;

import br.com.cantinho.tcpspringbootstarter.assigners.converters.ChatData;

public class Bag {
    private String uci;
    private ChatData chatData;
    private Class version;

    public Bag(final String uci, final ChatData chatData, final Class version) {
      this.uci = uci;
      this.chatData = chatData;
      this.version = version;
    }

    public String getUci() {
      return uci;
    }

    public void setUci(String uci) {
      this.uci = uci;
    }

    public ChatData getChatData() {
      return chatData;
    }

    public void setChatData(ChatData chatData) {
      this.chatData = chatData;
    }

    public Class getVersion() {
      return version;
    }

    public void setVersion(Class version) {
      this.version = version;
    }
  }