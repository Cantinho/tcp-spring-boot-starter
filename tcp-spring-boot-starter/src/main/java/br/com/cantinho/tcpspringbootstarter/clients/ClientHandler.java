package br.com.cantinho.tcpspringbootstarter.clients;

import br.com.cantinho.tcpspringbootstarter.tcp.TcpConnection;
import br.com.cantinho.tcpspringbootstarter.utils.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class ClientHandler implements Transmitter {

  /**
   * A logger instance.
   */
  private final static Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class
      .getCanonicalName());
  /**
   * Unique Connection Identifier, TCP connection
   */
  private Map<String, TcpConnection> clients = new HashMap<>();

  public String onConnect(final TcpConnection tcpConnection) {
    final String uci = ClientUtils.generateUCI(tcpConnection);
    clients.put(uci, tcpConnection);
    return uci;
  }

  public void onDisconnect(final TcpConnection tcpConnection) {
    final String uci = ClientUtils.generateUCI(tcpConnection);
    clients.remove(uci, tcpConnection);
  }

  @Override
  public void send(final String uci, final Object... parameters) {
    final TcpConnection tcpConnection = clients.get(uci);
    if(null != tcpConnection) {
      try {
        tcpConnection.send(parameters);
      } catch (Exception exc) {
        LOGGER.error("{}", exc.getMessage());
      }
    }
  }

}
