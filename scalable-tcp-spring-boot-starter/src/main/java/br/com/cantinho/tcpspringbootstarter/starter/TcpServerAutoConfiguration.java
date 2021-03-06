package br.com.cantinho.tcpspringbootstarter.starter;

import br.com.cantinho.tcpspringbootstarter.applications.chat.ChatApplication;
import br.com.cantinho.tcpspringbootstarter.applications.EchoApplication;
import br.com.cantinho.tcpspringbootstarter.applications.RoomApplication;
import br.com.cantinho.tcpspringbootstarter.assigners.*;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.ChatV1DataConverter;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.IConverter;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.RoomV1DataConverter;
import br.com.cantinho.tcpspringbootstarter.assigners.converters.RoomV2DataConverter;
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
import br.com.cantinho.tcpspringbootstarter.redis.queue.MessagePublisher;
import br.com.cantinho.tcpspringbootstarter.redis.queue.RedisMessagePublisher;
import br.com.cantinho.tcpspringbootstarter.redis.queue.RedisMessageSubscriber;
import br.com.cantinho.tcpspringbootstarter.redis.repo.ChatRoomRepository;
import br.com.cantinho.tcpspringbootstarter.tcp.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(TcpServerProperties.class)
@EnableRedisRepositories(basePackages = "br.com.cantinho.tcpspringbootstarter.redis.repo")
@ConditionalOnProperty(prefix = "tcp.server", name = {"port", "autoStart"})
public class TcpServerAutoConfiguration {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TcpServerAutoConfiguration.class);

  @Value( "${tcp.server.secureEnabled}" )
  private boolean secureEnabled;

  /**
   * Redis host address.
   */
  @Value("${redis.host}")
  private transient String redisHost;

  /**
   * Redis port address.
   */
  @Value("${redis.port}")
  private transient Integer redisPort;

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

    final List<IConverter> roomConverters = new ArrayList<>();
    roomConverters.add(new RoomV1DataConverter());
    roomConverters.add(new RoomV2DataConverter());

    final List<IConverter> chatConverters = new ArrayList<>();
    chatConverters.add(new ChatV1DataConverter());

    if(echoConverters.isEmpty() && roomConverters.isEmpty() && chatConverters.isEmpty()) {
      throw new RuntimeException("Converters doesn't exist.");
    }

    final List<Assignable> assignables = new ArrayList<>();
    assignables.add(new EchoAssignable(echoConverters, clientHandler(), new EchoApplication()));
    assignables.add(new RoomAssignable(roomConverters, clientHandler(), new RoomApplication()));
    assignables.add(new ChatAssignable(chatConverters, clientHandler(), chatApplication()));

    if(assignables.isEmpty()) {
      throw new RuntimeException("Assignable doesn't exist.");
    }
    LOGGER.info("dataHandler::assignables: " + assignables);
    return new BasicDataHandler(assignables);
  }

  @Autowired
  private ChatRoomRepository chatRoomRepository;

  ChatRoomRepository chatMessageRepository() {
    return chatRoomRepository;
  }

  /**
   * Creates a RedisConnectionFactory configured object to
   * connect to a Redis instance.
   *
   * @return a RedisConnectionFactory instance.
   */
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(200);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);

    final JedisConnectionFactory connectionFactory = new JedisConnectionFactory(poolConfig);
    connectionFactory.setUsePool(true);
    connectionFactory.setHostName(redisHost);
    connectionFactory.setPort(redisPort);

    return connectionFactory;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
    template.setConnectionFactory(redisConnectionFactory());
    template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
    return template;
  }

  @Bean
  @Scope("singleton")
  ChatApplication chatApplication() {
    return new ChatApplication(redisPublisher(), chatMessageRepository());
  }

  @Bean
  MessageListenerAdapter messageListener() {
    return new MessageListenerAdapter(chatApplication());
  }

  @Bean
  RedisMessageListenerContainer redisContainer() {
    final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(redisConnectionFactory());
    container.addMessageListener(messageListener(), topic());
    return container;
  }

  @Bean
  @Scope("singleton")
  public MessagePublisher redisPublisher() {
    return new RedisMessagePublisher(redisTemplate(), topic());
  }

  @Bean
  ChannelTopic topic() {
    return new ChannelTopic("pubsub:chat");
  }

}
