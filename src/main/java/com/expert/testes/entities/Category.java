package com.expert.testes.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_category")
public class Category {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp // serve para preencher automaticamente um campo com a data e hora de criação da entidade, no momento em que ela é persistida pela primeira vez no banco de dados.
    @Column(name = "created_at", length = 6,
        nullable = false,
        updatable = false // para não atualizar no banco de dados após criado
    )
    private LocalDateTime createdAt;


    @Setter(AccessLevel.NONE)
    @UpdateTimestamp // serve para atualizar automaticamente um campo com a data/hora da última modificação da entidade, sempre que um UPDATE acontece no banco.
    @Column(name = "updated_at",
        insertable = false // para não ser criado no banco de dados, ou seja, salvar como null
    )
    private LocalDateTime updatedAt;
}

