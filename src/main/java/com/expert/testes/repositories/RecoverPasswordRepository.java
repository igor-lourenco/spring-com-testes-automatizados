package com.expert.testes.repositories;

import com.expert.testes.entities.RecoverPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoverPasswordRepository extends JpaRepository<RecoverPassword, Long> {
}
