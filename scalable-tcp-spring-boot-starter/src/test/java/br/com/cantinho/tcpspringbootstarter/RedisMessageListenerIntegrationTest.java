package br.com.cantinho.tcpspringbootstarter;

import br.com.cantinho.tcpspringbootstarter.redis.queue.MessagePublisher;
import br.com.cantinho.tcpspringbootstarter.redis.queue.RedisMessagePublisher;
import br.com.cantinho.tcpspringbootstarter.redis.queue.RedisMessageSubscriber;
import br.com.cantinho.tcpspringbootstarter.starter.TcpServerAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TcpServerAutoConfiguration.class)
public class RedisMessageListenerIntegrationTest {

//    @Autowired
//    private MessagePublisher redisMessagePublisher;

    @Test
    public void testOnMessage() throws Exception {
//        String message = "Chat Message " + UUID.randomUUID();
//        redisMessagePublisher.publish(message);
//        Thread.sleep(100);
//        assertTrue(RedisMessageSubscriber.messageList.get(0).contains(message));
    }
}