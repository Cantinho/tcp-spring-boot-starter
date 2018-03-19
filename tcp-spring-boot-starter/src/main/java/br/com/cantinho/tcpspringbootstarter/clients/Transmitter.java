package br.com.cantinho.tcpspringbootstarter.clients;

/**
 * Capable of send message.
 */
public interface Transmitter {

  /**
   * Sends data to client.
   *
   * @param uci
   * @param parameters
   */
  void send(final String uci, final Object... parameters);

  /**
   * Closes communication to client.
   *
   * @param uci
   * @param parameters
   */
  void close(final String uci, final Object... parameters);
}
