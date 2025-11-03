package com.furniture.repository;

import com.furniture.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findBySellerId(Long id);

    @Query("""
    SELECT DISTINCT p
    FROM Product p
    LEFT JOIN p.categories c
    WHERE 
        (:query IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')))
        OR
        (:query IS NULL OR LOWER(c.categoryId) LIKE LOWER(CONCAT('%', :query, '%')))
        OR
        (:query IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))
""")
    List<Product> searchProduct(@Param("query") String query);

}
