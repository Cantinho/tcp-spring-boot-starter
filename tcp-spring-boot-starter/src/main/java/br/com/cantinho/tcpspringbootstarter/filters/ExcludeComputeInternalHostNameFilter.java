package br.com.cantinho.tcpspringbootstarter.filters;

import br.com.cantinho.tcpspringbootstarter.starter.EchoTcpController;
import br.com.cantinho.tcpspringbootstarter.tcp.TcpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExcludeComputeInternalHostNameFilter extends Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(EchoTcpController.class);

  public boolean filter(final Object... parameters) {
    if(parameters == null || parameters.length <= 0) {
      return false;
    }
    final TcpConnection tcpConnection = (TcpConnection) parameters[0];
    final String hostName = tcpConnection.getSocketAddress().getHostName();
    if(hostName != null && !hostName.isEmpty() && hostName.endsWith(".compute.internal")) {
      LOGGER.trace("Internal Amazon instance trying to establish connection to server.");
      return false;
    }
    return true;
  }
}
