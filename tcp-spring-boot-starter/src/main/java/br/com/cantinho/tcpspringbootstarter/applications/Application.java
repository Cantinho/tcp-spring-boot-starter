package br.com.cantinho.tcpspringbootstarter.applications;

public interface Application {

  Object process(final Object... parameters);

  void onConnect(final String uci);

  void onDisconnect(final String uci);
}
