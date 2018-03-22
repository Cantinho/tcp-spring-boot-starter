package br.com.cantinho.tcpspringbootstarter.redis.model;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("ChatApplication")
public class ChatRoom implements Serializable {

    /**
     * Id.
     */
    private String name;

    /**
     * Onwer.
     */
    private String owner;



    public ChatRoom(final String name, final String owner) {
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
            "name='" + name + '\'' +
            ", owner='" + owner + '\'' +
            '}';
    }
}