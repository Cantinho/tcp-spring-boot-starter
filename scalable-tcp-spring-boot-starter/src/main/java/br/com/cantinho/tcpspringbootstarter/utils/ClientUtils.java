package br.com.cantinho.tcpspringbootstarter.utils;

import br.com.cantinho.tcpspringbootstarter.tcp.TcpConnection;

public class ClientUtils {

  /**
   * Generates a unique connection identifier from a given TcpConnection.
   * @param connection a TcpConnection.
   * @return a string representing a unique connection identifier.
   */
  public static String generateUCI(final TcpConnection connection) {
    final String hostname = connection.getSocketAddress().getHostName();
    final String port = String.valueOf(connection.getSocketAddress().getPort());

    return hostname.concat(":").concat(port);
  }

}
