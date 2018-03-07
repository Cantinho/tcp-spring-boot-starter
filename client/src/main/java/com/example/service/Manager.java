package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Manager {

  @Autowired
  public Manager(final Server server, final Client client){
    server.start();
    try {
      Thread.sleep(1000);
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    client.start();
  }
}
