package br.com.cantinho.tcpspringbootstarter.clients;

import br.com.cantinho.tcpspringbootstarter.tcp.TcpConnection;

public class BasicClientHandler extends ClientHandler implements Transmitter {

  @Override
  public String onConnect(final TcpConnection tcpConnection) {
    return super.onConnect(tcpConnection);
  }

  @Override
  public void onDisconnect(final TcpConnection tcpConnection) {
    super.onDisconnect(tcpConnection);
  }

  @Override
  public void send(final String uci, final Object... parameters) {
    super.send(uci, parameters);
  }

}
