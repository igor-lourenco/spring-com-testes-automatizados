package com.expert.testes.repositories;

import com.expert.testes.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"roles"},
        type = EntityGraph.EntityGraphType.FETCH // por padrão é FETCH(jakarta.persistence.fetchgraph)
    )
    User findByEmail(String email);
}
