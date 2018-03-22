package br.com.cantinho.tcpspringbootstarter.applications;

import br.com.cantinho.tcpspringbootstarter.assigners.converters.EchoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoApplication implements Application {

  /**
   * A logger instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(EchoApplication.class.getCanonicalName());

  private ApplicationListener listener;

  @Override
  public Object process(Object... parameters) {
    LOGGER.debug("process");
    final EchoData request = (EchoData) parameters[0];
    final EchoData response = new EchoData();
    response.setSource(request.getDestination());
    response.setDestination(request.getSource());
    response.setPayload(request.getPayload());

    return response;
  }

  @Override
  public Object onConnect(String uci) {
    LOGGER.debug("onConnect:{}", uci);
    return new Object();
  }

  @Override
  public Object onDisconnect(String uci) {
    LOGGER.debug("onDisconnect:{}", uci);
    return new Object();
  }

  @Override
  public void setListener(ApplicationListener listener) {
    this.listener = listener;
  }
}
