package com.expert.testes.repositories;

import com.expert.testes.entities.Product;
import com.expert.testes.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(nativeQuery = true, // Usa o value para buscar os registros da página, Usa o countQuery para saber o total de resultados
		value =
			" SELECT * FROM ( "
				+ " SELECT DISTINCT tb_product.id AS id, tb_product.name AS name "
			    + " FROM tb_product "
				+ " INNER JOIN tb_product_category ON tb_product_category.product_id = tb_product.id "
				+ " WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds) " // Se categoryIds for null ignora o filtro (traz produtos de qualquer categoria), Se tiver valores filtra produtos que pertencem a essas categorias
				+ " AND (LOWER(tb_product.name) LIKE LOWER(CONCAT('%',:name,'%')))"
			+ ") AS tb_result" , // subquery para permitir paginação e suportar o countQuery corretamente
        countQuery = """ 
			SELECT COUNT(*) FROM (
			SELECT DISTINCT tb_product.id, tb_product.name
			FROM tb_product
			INNER JOIN tb_product_category ON tb_product_category.product_id = tb_product.id
			WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
			AND (LOWER(tb_product.name) LIKE LOWER(CONCAT('%',:name,'%')))
			) AS tb_result
			""")
    Page<ProductProjection> searchProducts(List<Long> categoryIds, String name, Pageable pageable);

    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj.id IN :productIds")
    List<Product> searchProductsWithCategories(List<Long> productIds);
}
