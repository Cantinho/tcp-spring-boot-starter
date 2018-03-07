package br.com.cantinho.tcpspringbootstarter.starter;

import br.com.cantinho.tcpspringbootstarter.tcp.TcpConnection;
import br.com.cantinho.tcpspringbootstarter.tcp.TcpController;
import br.com.cantinho.tcpspringbootstarter.tcp.TcpThreadPoolServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Echo TCP controller.
 */
@TcpController
public class EchoTcpController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TcpThreadPoolServer.class);

  /**
   * Send the message received from client.
   *
   * @param connection
   * @param data
   */
  public void receiveData(final TcpConnection connection, byte[] data) {
    final String answer = new String(data);
    LOGGER.info("New data: " + answer);
    connection.send(answer.getBytes());
  }

  /**
   * When receive a connection.
   *
   * @param connection
   */
  public void connect(final TcpConnection connection) {
    LOGGER.info("New connection " + connection.getSocketAddress().getHostName());
  }

  /**
   * When disconnect event occurs.
   *
   * @param connection
   */
  public void disconnect(final TcpConnection connection) {
    LOGGER.info("Disconnect " + connection.getSocketAddress().getHostName());
  }

}
