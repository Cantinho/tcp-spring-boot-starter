package br.com.cantinho.tcpspringbootstarter;

import br.com.cantinho.tcpspringbootstarter.config.RedisConfig;
import br.com.cantinho.tcpspringbootstarter.redis.model.ChatMessage;
import br.com.cantinho.tcpspringbootstarter.redis.repo.ChatMessageRepository;
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
@ContextConfiguration(classes = RedisConfig.class)
public class StudentRepositoryIntegrationTest {

    @Autowired
    private ChatMessageRepository studentRepository;

    @Test
    public void whenSavingStudent_thenAvailableOnRetrieval() throws Exception {
        final ChatMessage student = new ChatMessage("Eng2015001", "John Doe");
        studentRepository.save(student);
        final ChatMessage retrievedStudent = studentRepository.findById(student.getId()).get();
        assertEquals(student.getId(), retrievedStudent.getId());
    }

    @Test
    public void whenUpdatingStudent_thenAvailableOnRetrieval() throws Exception {
        final ChatMessage student = new ChatMessage("Eng2015001", "John Doe");
        studentRepository.save(student);
        student.setOwner("Richard Watson");
        studentRepository.save(student);
        final ChatMessage retrievedStudent = studentRepository.findById(student.getId()).get();
        assertEquals(student.getOwner(), retrievedStudent.getOwner());
    }

    @Test
    public void whenSavingStudents_thenAllShouldAvailableOnRetrieval() throws Exception {
        final ChatMessage engStudent = new ChatMessage("Eng2015001", "John Doe");
        final ChatMessage medStudent = new ChatMessage("Med2015001", "Gareth Houston");
        studentRepository.save(engStudent);
        studentRepository.save(medStudent);
        List<ChatMessage> students = new ArrayList<>();
        studentRepository.findAll().forEach(students::add);
        assertEquals(students.size(), 2);
    }

    @Test
    public void whenDeletingStudent_thenNotAvailableOnRetrieval() throws Exception {
        final ChatMessage student = new ChatMessage("Eng2015001", "John Doe");
        studentRepository.save(student);
        studentRepository.deleteById(student.getId());
        final ChatMessage retrievedStudent = studentRepository.findById(student.getId()).orElse(null);
        assertNull(retrievedStudent);
    }
}