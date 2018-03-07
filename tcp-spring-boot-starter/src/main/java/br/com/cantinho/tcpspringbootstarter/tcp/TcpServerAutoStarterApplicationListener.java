package br.com.cantinho.tcpspringbootstarter.tcp;

import br.com.cantinho.tcpspringbootstarter.ApplicationConfig;
import br.com.cantinho.tcpspringbootstarter.starter.TcpServerAutoConfiguration;
import br.com.cantinho.tcpspringbootstarter.starter.TcpServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import static br.com.cantinho.tcpspringbootstarter.ApplicationConfig.CURRENT_SERVER_IMPLEMENTATION;

/**
 * TCP server auto starter application listener.
 */
public class TcpServerAutoStarterApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TcpServerAutoConfiguration.class);

  /**
   * TCP server properties.
   */
  //@Autowired
  //private TcpServerProperties properties;

  @Value( "${tcp.server.secureEnabled}" )
  private boolean secureEnabled;

  @Value( "${tcp.server.port}" )
  private int defaultPort;

  @Value( "${tcp.server.securePort}" )
  private int securePort;

  /**
   * Actually, instantiating.
   */
  @Autowired
  @Qualifier(value = CURRENT_SERVER_IMPLEMENTATION)
  private TcpServer server;

  /**
   * Builds a TCP server auto starter application listener.
   */
  public TcpServerAutoStarterApplicationListener() {
  }

  /**
   * When application event occurs.
   *
   * @param contextRefreshedEvent
   */
  @Override
  public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
    LOGGER.info("onApplicationEvent");
    server.init(secureEnabled ? securePort : defaultPort);
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
