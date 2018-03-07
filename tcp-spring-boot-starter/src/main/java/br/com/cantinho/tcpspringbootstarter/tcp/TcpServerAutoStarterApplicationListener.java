package br.com.cantinho.tcpspringbootstarter.tcp;

import br.com.cantinho.tcpspringbootstarter.starter.TcpServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * TCP server auto starter application listener.
 */
public class TcpServerAutoStarterApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

  /**
   * TCP server properties.
   */
  @Autowired
  private TcpServerProperties properties;

  /**
   * Actually, instantiating.
   */
  @Autowired
  @Qualifier(value = "TcpThreadPoolServer")
  private TcpServer server;

  /**
   * Builds a TCP server auto starter application listener.
   */
  public TcpServerAutoStarterApplicationListener() {
    server = new TcpThreadPoolServer();
  }

  /**
   * When application event occurs.
   *
   * @param contextRefreshedEvent
   */
  @Override
  public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
    ((TcpThreadPoolServer) server).init(properties.getPort());
    server.start();
  }

  /**
   * Retrieves a TCP server.
   *
   * @return
   */
  public TcpServer getServer() {
    return server;
  }
}
