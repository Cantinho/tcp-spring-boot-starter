package br.com.cantinho.tcpspringbootstarter;

import br.com.cantinho.tcpspringbootstarter.redis.model.ChatRoom;
import br.com.cantinho.tcpspringbootstarter.redis.repo.ChatRoomRepository;
import br.com.cantinho.tcpspringbootstarter.starter.TcpServerAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TcpServerAutoConfiguration.class)
public class ChatRoomRepositoryIntegrationTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    public void whenSavingStudent_thenAvailableOnRetrieval() throws Exception {
        final ChatRoom chatRoom = new ChatRoom("Room001", "John Doe");
        chatRoomRepository.save(chatRoom);
        final ChatRoom retrievedChatRoom = chatRoomRepository.findById(chatRoom.getId()).get();
        assertEquals(chatRoom.getId(), retrievedChatRoom.getId());
    }

    @Test
    public void whenUpdatingStudent_thenAvailableOnRetrieval() throws Exception {
        final ChatRoom chatRoom = new ChatRoom("Room001", "John Doe");
        chatRoomRepository.save(chatRoom);
        chatRoom.setOwner("Richard Watson");
        chatRoomRepository.save(chatRoom);
        final ChatRoom retrievedChatRoom = chatRoomRepository.findById(chatRoom.getId()).get();
        assertEquals(chatRoom.getOwner(), retrievedChatRoom.getOwner());
    }

    @Test
    public void whenSavingStudents_thenAllShouldAvailableOnRetrieval() throws Exception {
        final ChatRoom engRoom = new ChatRoom("Room001", "John Doe");
        final ChatRoom medRoom = new ChatRoom("Room002", "Gareth Houston");
        chatRoomRepository.save(engRoom);
        chatRoomRepository.save(medRoom);
        List<ChatRoom> messages = new ArrayList<>();
        chatRoomRepository.findAll().forEach(messages::add);
        assertEquals(messages.size(), 2);
    }

    @Test
    public void whenDeletingStudent_thenNotAvailableOnRetrieval() throws Exception {
        final ChatRoom message = new ChatRoom("Room001", "John Doe");
        chatRoomRepository.save(message);
        chatRoomRepository.deleteById(message.getId());
        final ChatRoom retrievedMessage = chatRoomRepository.findById(message.getId()).orElse(null);
        assertNull(retrievedMessage);
    }
}