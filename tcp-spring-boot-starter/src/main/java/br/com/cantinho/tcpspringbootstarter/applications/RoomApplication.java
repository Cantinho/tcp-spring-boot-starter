package br.com.cantinho.tcpspringbootstarter.applications;

import br.com.cantinho.tcpspringbootstarter.assigners.converters.RoomData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomApplication implements Application {

  /**
   * A logger instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RoomApplication.class.getCanonicalName());


  @Override
  public Object process(Object... parameters) {
    LOGGER.debug("process");
    final RoomData request = (RoomData) parameters[0];
    final RoomData response = new RoomData();
    response.setFrom(request.getTo());
    response.setTo(request.getFrom());
    response.setMsg(request.getMsg());

    return response;
  }

  @Override
  public void onConnect(String uci) {
    LOGGER.debug("onConnect:{}", uci);
  }

  @Override
  public void onDisconnect(String uci) {
    LOGGER.debug("onDisconnect:{}", uci);
  }
}
