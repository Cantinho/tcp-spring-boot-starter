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
    LOGGER.debug("onConnect {}", uci);
    return uci;
  }

  public String onDisconnect(final TcpConnection tcpConnection) {
    final String uci = ClientUtils.generateUCI(tcpConnection);
    clients.remove(uci, tcpConnection);
    LOGGER.debug("onDisconnect {}", uci);
    return uci;
  }


  @Override
  public void send(final String uci, final Object... parameters) {
    LOGGER.debug("send {}", uci);
    final TcpConnection tcpConnection = clients.get(uci);
    if(null != tcpConnection) {
      try {
        tcpConnection.send(parameters[0]);
      } catch (Exception exc) {
        LOGGER.error("{}", exc.getMessage());
      }
    } else {
      LOGGER.error("tcp connection is null");
    }
  }

  @Override
  public void close(final String uci, final Object... parameters) {
    LOGGER.debug("close {}", uci);
    final TcpConnection tcpConnection = clients.get(uci);
    if(null != tcpConnection) {
      try {
        tcpConnection.close();
        LOGGER.error("close ok");
      } catch (Exception exc) {
        LOGGER.error("{}", exc.getMessage());
      }
    } else {
      LOGGER.error("tcp connection is null");
    }
  }


}
