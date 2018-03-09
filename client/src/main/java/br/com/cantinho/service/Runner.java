package br.com.cantinho.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Runner {

  @Autowired
  public Runner(final Server server, final Client client){
    // starts a server
    //server.start();
    try {
      /** wait a while until server gets ready */
      Thread.sleep(500);
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    // starts a client
    client.start();
  }
}
