package br.com.cantinho.tcpspringbootstarter.clients;

import br.com.cantinho.tcpspringbootstarter.tcp.TcpConnection;

public class BasicClientHandler extends ClientHandler implements Transmitter {

  @Override
  public String onConnect(final TcpConnection tcpConnection) {
    return super.onConnect(tcpConnection);
  }

  @Override
  public String onDisconnect(final TcpConnection tcpConnection) {
    return super.onDisconnect(tcpConnection);
  }

  @Override
  public void send(final String uci, final Object... parameters) {
    super.send(uci, parameters);
  }

  @Override
  public void close(final String uci, final Object... parameters) {
    super.close(uci, parameters);
  }

}
