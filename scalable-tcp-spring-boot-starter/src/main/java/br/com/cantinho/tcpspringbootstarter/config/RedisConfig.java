package br.com.cantinho.tcpspringbootstarter.config;

import br.com.cantinho.tcpspringbootstarter.redis.queue.MessagePublisher;
import br.com.cantinho.tcpspringbootstarter.redis.queue.RedisMessagePublisher;
import br.com.cantinho.tcpspringbootstarter.redis.queue.RedisMessageSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
@EnableRedisRepositories(basePackages = "br.com.cantinho.tcpspringbootstarter.redis.repo")
public class RedisConfig {


}