package com.expert.testes.repositories;

import com.expert.testes.entities.RecoverPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface RecoverPasswordRepository extends JpaRepository<RecoverPassword, Long> {

    @Query("SELECT obj FROM RecoverPassword obj WHERE obj.token = :token AND obj.expiration > :now")
    List<RecoverPassword> searchValidTokens(String token, Instant now);

}
