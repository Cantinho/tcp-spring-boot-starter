package br.com.cantinho.tcpspringbootstarter.redis.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("ChatApplication")
public class ChatRoom implements Serializable {

    /**
     * Id.
     */
    @Id
    private String id;

    /**
     * Name.
     */
    @Indexed
    private String name;

    /**
     * Onwer.
     */
    @Indexed
    private String owner;

    public ChatRoom(final String id, final String name, final String owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", owner='" + owner + '\'' +
            '}';
    }
}