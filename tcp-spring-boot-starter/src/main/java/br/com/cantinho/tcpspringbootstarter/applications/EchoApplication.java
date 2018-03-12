package br.com.cantinho.tcpspringbootstarter.applications;

import br.com.cantinho.tcpspringbootstarter.assigners.converters.EchoData;

public class EchoApplication implements Application {

  @Override
  public Object process(Object data) {

    final EchoData request = (EchoData) data;
    final EchoData response = new EchoData();
    response.setSource(request.getDestination());
    response.setDestination(request.getSource());
    response.setPayload(request.getPayload());

    return response;
  }
}
