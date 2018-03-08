package br.com.cantinho.tcpspringbootstarter.starter;

import br.com.cantinho.tcpspringbootstarter.tcp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(TcpServerProperties.class)
@ConditionalOnProperty(prefix = "tcp.server", name = {"port", "autoStart"})
public class TcpServerAutoConfiguration {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TcpServerAutoConfiguration.class);

  @Value( "${tcp.server.secureEnabled}" )
  private boolean secureEnabled;

  /**
   * Creates a new TCP Server AutoStarterListener.
   *
   * @return
   */
  @Bean
  TcpServerAutoStarterApplicationListener tcpServerAutoStarterApplicationListener() {
    return new TcpServerAutoStarterApplicationListener();
  }

  /**
   * Creates a new TCP controller bean post processor.
   *
   * @return
   */
  @Bean
  TcpControllerBeanPostProcessor tcpControllerBeanPostProcessor() {
    return new TcpControllerBeanPostProcessor();
  }

  /**
   * Creates a TCP thread pool server.
   *
   * @return
   */
  @Bean
  TcpServer tcpServer() {
    LOGGER.info("tcpServer:" + secureEnabled);
    if(secureEnabled) {
      return new SecureTcpThreadPoolServer();
    } else {
      return new TcpThreadPoolServer();
    }

  }

}
