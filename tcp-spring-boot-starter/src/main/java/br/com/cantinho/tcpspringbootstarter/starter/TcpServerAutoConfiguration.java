package br.com.cantinho.tcpspringbootstarter.starter;

import br.com.cantinho.tcpspringbootstarter.assigners.*;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.IConverter;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.RoomV1DataConverter;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.V1DataConverter;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.V2DataConverter;
import br.com.cantinho.tcpspringbootstarter.clients.BasicClientHandler;
import br.com.cantinho.tcpspringbootstarter.clients.ClientHandler;
import br.com.cantinho.tcpspringbootstarter.data.BasicDataHandler;
import br.com.cantinho.tcpspringbootstarter.data.DataHandler;
import br.com.cantinho.tcpspringbootstarter.data.DataHandlerException;
import br.com.cantinho.tcpspringbootstarter.filters.BasicFilterHandler;
import br.com.cantinho.tcpspringbootstarter.filters.ExcludeComputeInternalHostNameFilter;
import br.com.cantinho.tcpspringbootstarter.filters.Filter;
import br.com.cantinho.tcpspringbootstarter.filters.FilterHandler;
import br.com.cantinho.tcpspringbootstarter.tcp.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

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

  @Bean
  FilterHandler filterHandler() {
    LOGGER.info("filterHandler: " + BasicFilterHandler.class.getCanonicalName());
    final List<Filter> filters = new ArrayList<>();
    filters.add(new ExcludeComputeInternalHostNameFilter());
    return new BasicFilterHandler(filters);
  }

  @Bean
  @Scope("singleton")
  ClientHandler clientHandler() {
    LOGGER.info("clientHandler: " + BasicClientHandler.class.getCanonicalName());
    return new BasicClientHandler();
  }


  @Bean
  DataHandler dataHandler() throws DataHandlerException, AssignableException {
    LOGGER.info("dataHandler: " + BasicDataHandler.class.getCanonicalName());

    final List<IConverter> echoConverters = new ArrayList<>();
    echoConverters.add(new V1DataConverter());
    echoConverters.add(new V2DataConverter());

    if(echoConverters.isEmpty()) {
      throw new RuntimeException("Converters doesn't exist.");
    }

    final List<IConverter> roomConverters = new ArrayList<>();
    roomConverters.add(new RoomV1DataConverter());

    if(echoConverters.isEmpty()) {
      throw new RuntimeException("Converters doesn't exist.");
    }

    final List<Assignable> assignables = new ArrayList<>();
    assignables.add(new EchoAssignable(echoConverters, clientHandler()));
    assignables.add(new RoomAssignable(roomConverters, clientHandler()));

    if(assignables.isEmpty()) {
      throw new RuntimeException("Assignable doesn't exist.");
    }
    LOGGER.info("dataHandler::assignables: " + assignables);
    return new BasicDataHandler(assignables);
  }

}
