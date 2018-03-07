package com.client.utils;

import com.client.domain.SimpleMessage;
import com.google.gson.Gson;

import java.net.Socket;

public class Utils {

  /**
   * Creates a SimpleMessage with socket information given a socket and a string message.
   *
   * @param socket a socket.
   * @param message a message.
   * @return a SimpleMessage.
   */
  public static SimpleMessage createMessage(final Socket socket, final String message){
    SimpleMessage messageMapper = new SimpleMessage();
    messageMapper.setFrom(socket.getLocalSocketAddress().toString());
    messageMapper.setTo(socket.getRemoteSocketAddress().toString());
    messageMapper.setMsg(message);
    return messageMapper;
  }

  /**
   * Returns an array of bytes from a SimpleMessage.
   *
   * @param message a SimpleMessage.
   * @return an array of bytes.
   */
  public static byte[] getBytesFromMessage(final SimpleMessage message) {
    Gson gson = new Gson();
    String messageStr = gson.toJson(message);
    return messageStr.getBytes();
  }

  /**
   * Returns a SimpleMessage from an array of bytes.
   *
   * @param messageBytes an array of bytes.
   * @return a SimpleMessage.
   */
  public static SimpleMessage getMessageFromBytes(final byte[] messageBytes) {
    Gson gson = new Gson();
    String messageStr = new String(messageBytes);
    return gson.fromJson(messageStr, SimpleMessage.class);
  }

  /**
   * Exchanges sender to receiver.
   *
   * @param mapper a SimpleMessage.
   * @return the given SimpleMessage with exchanged from/to.
   */
  public static SimpleMessage exchangeFromTo(final SimpleMessage mapper) {
    String temp = mapper.getFrom();
    mapper.setFrom(mapper.getTo());
    mapper.setTo(temp);
    return mapper;
  }

}
