package com.expert.testes.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_user")
@Builder
public class User {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.EAGER, // por padrão usa o Fetch.LAZY
        cascade = { CascadeType.PERSIST  // ao persistir produto, também irá salvar as categorias em cascata
            , CascadeType.MERGE} ) // ao atualizar produto, também irá salvar as categorias em cascata
    @JoinTable(name = "tb_user_role",
//        foreignKey = @ForeignKey(name = "fk_product_category_product"), // exemplo de configurar o nome da contraint diretamente no @JoinTable em vez do @JoinColumn
//        inverseForeignKey = @ForeignKey(name = "fk_product_category_category") // exemplo de configurar o nome da contraint diretamente no @JoinTable em vez do @JoinColumn

        joinColumns = @JoinColumn(name = "user_id", // coluna que referencia o id dessa entidade Produto (owner)
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_role_user") // nome da constraint de chave estrangeira
        ),
        inverseJoinColumns = @JoinColumn(name = "role_id", // coluna que referencia o id da entidade Categoria (não owner)
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_role_role") // nome da constraint de chave estrangeira
        )
    )
    @Builder.Default // Para o @Builder respeitar o valor default e evitar o NullPointerException
    private Set<Role> roles = new HashSet<>();

}
