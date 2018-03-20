package br.com.cantinho.tcpspringbootstarter.redis.repo;

import br.com.cantinho.tcpspringbootstarter.redis.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student, String> {}