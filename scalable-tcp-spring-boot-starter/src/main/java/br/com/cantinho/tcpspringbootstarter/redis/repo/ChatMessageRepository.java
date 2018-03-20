package br.com.cantinho.tcpspringbootstarter.redis.repo;

import br.com.cantinho.tcpspringbootstarter.redis.model.ChatMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends CrudRepository<ChatMessage, String> {}