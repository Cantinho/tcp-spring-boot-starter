package br.com.cantinho.tcpspringbootstarter.redis.model;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("ChatApplication")
public class ChatRoom implements Serializable {

    /**
     * Id.
     */
    private String id;

    /**
     * Onwer.
     */
    private String owner;



    public ChatRoom(final String id, final String owner) {
        this.id = id;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
            "id='" + id + '\'' +
            ", owner='" + owner + '\'' +
            '}';
    }
}