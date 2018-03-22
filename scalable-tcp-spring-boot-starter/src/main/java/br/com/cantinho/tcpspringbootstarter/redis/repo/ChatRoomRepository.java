package br.com.cantinho.tcpspringbootstarter.redis.repo;

import br.com.cantinho.tcpspringbootstarter.redis.model.ChatRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends CrudRepository<ChatRoom, String> {

  List<ChatRoom> findByName(final String name);

  List<ChatRoom> removeByName(final String name);
}