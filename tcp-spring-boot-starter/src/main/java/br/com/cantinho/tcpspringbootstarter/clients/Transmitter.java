package br.com.cantinho.tcpspringbootstarter.clients;

/**
 * Capable of send message.
 */
public interface Transmitter {
  void send(final String uci, final Object... parameters);
}
