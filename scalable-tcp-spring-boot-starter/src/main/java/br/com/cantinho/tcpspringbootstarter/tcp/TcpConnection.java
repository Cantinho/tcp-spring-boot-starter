package br.com.cantinho.tcpspringbootstarter.tcp;

import java.net.InetSocketAddress;

/**
 * TCP connection.
 */
public interface TcpConnection {

  /**
   * Retrieves a socket address.
   *
   * @return
   */
  InetSocketAddress getSocketAddress();

  /**
   * Sends a object to client.
   *
   * @param objectToSend
   */
  void send(final Object objectToSend);

  /**
   * Adds a listener.
   *
   * @param listener
   */
  void addListener(final Listener listener);

  /**
   * Starts the connection handler.
   * TODO fix this.
   */
  void start();

  /**
   * Closes the connection handler.
   */
  void close();

  /**
   * TCP connection listener.
   */
  interface Listener {

    /**
     * Occurs when server receive a message through the connection.
     *
     * @param connection
     * @param message
     */
    void onMessageReceived(final TcpConnection connection, final Object message);

    /**
     * Occurs when a client connects to the server.
     *
     * @param connection
     */
    void onClientConnected(final TcpConnection connection);

    /**
     * Occurs when a client disconnects from the server.
     *
     * @param connection
     */
    void onClientDisconnected(final TcpConnection connection);
  }
}
