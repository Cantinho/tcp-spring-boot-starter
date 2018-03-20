package br.com.cantinho.tcpspringbootstarter.tcp;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * TCP server interface.
 */
public interface TcpServer {

  /**
   * Retrieves all connected connections count.
   *
   * @return
   */
  int getConnectionsCount();

  /**
   * Configures the server port.
   *
   * @param port
   */
  void setPort(final Integer port);

  /**
   * Inits server on port.
   *
   * @param port
   */
  void init(final Integer port);

  /**
   * Starts the server.
   */
  void start();

  /**
   * Stops the server.
   */
  void stop();

  /**
   * Retrieves all connected connections.
   *
   * @return
   */
  List<TcpConnection> getConnections();

  /**
   * Adds a TCP conenctions listener.
   *
   * @param listener
   */
  void addListener(final TcpConnection.Listener listener);
}
