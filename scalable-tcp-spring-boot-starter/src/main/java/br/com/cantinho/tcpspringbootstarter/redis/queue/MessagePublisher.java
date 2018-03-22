package br.com.cantinho.tcpspringbootstarter.redis.queue;

public interface MessagePublisher {

    void publish(final String message);
}