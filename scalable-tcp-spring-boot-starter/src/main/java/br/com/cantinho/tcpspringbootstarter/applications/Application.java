package br.com.cantinho.tcpspringbootstarter.applications;

public interface Application {

  Object process(final Object... parameters);

  Object onConnect(final String uci);

  Object onDisconnect(final String uci);

  void setListener(final ApplicationListener listener);
}
