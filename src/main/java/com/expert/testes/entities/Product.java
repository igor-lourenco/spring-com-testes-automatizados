package com.expert.testes.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_product")
@Builder
public class Product {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Lob // Especifica que uma propriedade ou campo persistente deve ser persistido como um objeto grande em um tipo de objeto grande compatível com o banco de dados
    private String description;

    private BigDecimal price;
    private String imgUrl;

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


    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY, // por padrão usa o Fetch.LAZY
        cascade = { CascadeType.PERSIST  // ao persistir produto, também irá salvar as categorias em cascata
            , CascadeType.MERGE} ) // ao atualizar produto, também irá salvar as categorias em cascata
    @JoinTable(name = "tb_product_category",
//        foreignKey = @ForeignKey(name = "fk_product_category_product"), // exemplo de configurar o nome da contraint diretamente no @JoinTable em vez do @JoinColumn
//        inverseForeignKey = @ForeignKey(name = "fk_product_category_category") // exemplo de configurar o nome da contraint diretamente no @JoinTable em vez do @JoinColumn

        joinColumns = @JoinColumn(name = "product_id", // coluna que referencia o id dessa entidade Produto (owner)
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_category_product") // nome da constraint de chave estrangeira
        ),
        inverseJoinColumns = @JoinColumn(name = "category_id", // coluna que referencia o id da entidade Categoria (não owner)
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_category_category") // nome da constraint de chave estrangeira
        )
    )
    @Builder.Default // Para o @Builder respeitar o valor default e evitar o NullPointerException
    private Set<Category> categories = new HashSet<>();
}

