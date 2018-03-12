package br.com.cantinho.tcpspringbootstarter.applications;

import br.com.cantinho.tcpspringbootstarter.assigners.converters.RoomData;

public class RoomApplication implements Application {

  @Override
  public Object process(Object data) {

    final RoomData request = (RoomData) data;
    final RoomData response = new RoomData();
    response.setFrom(request.getTo());
    response.setTo(request.getFrom());
    response.setMsg(request.getMsg());

    return response;
  }
}
