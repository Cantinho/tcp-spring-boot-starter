package br.com.cantinho.tcpspringbootstarter.starter;

import br.com.cantinho.tcpspringbootstarter.clients.ClientHandler;
import br.com.cantinho.tcpspringbootstarter.converters.DataHandler;
import br.com.cantinho.tcpspringbootstarter.filters.FilterHandler;
import br.com.cantinho.tcpspringbootstarter.tcp.TcpConnection;
import br.com.cantinho.tcpspringbootstarter.tcp.TcpController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Echo TCP controller.
 */
@TcpController
public class EchoTcpController {

  private static final Logger LOGGER = LoggerFactory.getLogger(EchoTcpController.class);

  final FilterHandler filterHandler;
  final ClientHandler clientHandler;
  final DataHandler dataHandler;

  @Autowired
  public EchoTcpController(
      final FilterHandler filterHandler,
      final ClientHandler clientHandler,
      final DataHandler dataHandler
  ) {
    this.filterHandler = filterHandler;
    this.clientHandler = clientHandler;
    this.dataHandler = dataHandler;
  }


  /**
   * Send the message received from client.
   *
   * @param connection
   * @param data
   */
  public void receiveData(final TcpConnection connection, final byte[] data) {
    final String answer = new String(data);
    LOGGER.info("receiveData: " + answer);
    connection.send(answer.getBytes());
  }

  /**
   * When receive a connection.
   *
   * @param connection
   */
  public void connect(final TcpConnection connection) {
    LOGGER.info("New connection \t");
    LOGGER.info("New connection::hostname " + connection.getSocketAddress().getHostName());
    LOGGER.info("New connection::address " + connection.getSocketAddress().getHostName());
    LOGGER.info("New connection::port " + connection.getSocketAddress().getPort());
    LOGGER.info("New connection::add.canonicalHostName " + connection.getSocketAddress()
        .getAddress().getCanonicalHostName());
    LOGGER.info("New connection::add.hostAddress " + connection.getSocketAddress()
        .getAddress().getHostAddress());
    LOGGER.info("New connection::add.hostName " + connection.getSocketAddress()
        .getAddress().getHostName());
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
