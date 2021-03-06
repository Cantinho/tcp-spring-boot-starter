package br.com.cantinho.tcpspringbootstarter.redis.repo;

import br.com.cantinho.tcpspringbootstarter.redis.model.ChatRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends CrudRepository<ChatRoom, String> {
}