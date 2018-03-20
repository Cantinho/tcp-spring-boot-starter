package br.com.cantinho.tcpspringbootstarter.starter;

import br.com.cantinho.tcpspringbootstarter.clients.ClientHandler;
import br.com.cantinho.tcpspringbootstarter.data.DataHandler;
import br.com.cantinho.tcpspringbootstarter.data.DataHandlerException;
import br.com.cantinho.tcpspringbootstarter.filters.FilterHandler;
import br.com.cantinho.tcpspringbootstarter.tcp.TcpConnection;
import br.com.cantinho.tcpspringbootstarter.tcp.TcpController;

import br.com.cantinho.tcpspringbootstarter.utils.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Echo TCP controller.
 */
@TcpController
public class EchoTcpController {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(EchoTcpController.class);

  /**
   * Handler to filter connections.
   */
  final FilterHandler filterHandler;

  /**
   * Handler to manager client connections.
   */
  final ClientHandler clientHandler;

  /**
   * Handler to process incoming data.
   */
  final DataHandler dataHandler;

  /**
   * Build a Tcp Controller passing handlers as arguments.
   *
   * @param filterHandler
   * @param clientHandler
   * @param dataHandler
   */
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
   * Sends the message received from client.
   * Method must start with {@code receive}.
   *
   * @param connection
   * @param data
   */
  public void receiveData(final TcpConnection connection, final byte[] data) {
    final boolean passed = filterHandler.filter(connection, data);
    if(passed) {
      final String uci = ClientUtils.generateUCI(connection);
      try {
        dataHandler.onIncomingData(uci, data);
      } catch (final DataHandlerException dhe) {
        LOGGER.error("Fix data handler. {}", dhe);
      }
    }
  }

  /**
   * When it receives a connection.
   * Method must start with {@code connect}.
   *
   * @param connection
   */
  public void connect(final TcpConnection connection) {
    final boolean passed = filterHandler.filter(connection);
    if(passed) {
      final String uci = clientHandler.onConnect(connection);
      dataHandler.onConnect(uci);
    }

  }

  /**
   * When disconnect event occurs.
   * Method must start with {@code disconnect}.
   *
   * @param connection
   */
  public void disconnect(final TcpConnection connection) {
    final boolean passed = filterHandler.filter(connection);
    if(passed) {
      final String uci = clientHandler.onDisconnect(connection);
      dataHandler.onDisconnect(uci);
    }
  }

}
