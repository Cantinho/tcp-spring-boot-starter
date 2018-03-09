package br.com.cantinho.tcpspringbootstarter.clients;

import br.com.cantinho.tcpspringbootstarter.tcp.TcpConnection;

import java.util.HashMap;
import java.util.Map;

public class ClientHandler {

  /**
   * Unique Connection Identifier, TCP connection
   */
  private Map<String, TcpConnection> clients = new HashMap<>();

  public void onConnect(final TcpConnection tcpConnection) {
    final String hostname = tcpConnection.getSocketAddress().getHostName();
    //TODO
  }

  public void onDisconnect(final TcpConnection tcpConnection) {

  }

  public void send(final String uuid, final Object... parameters) {

  }

}
