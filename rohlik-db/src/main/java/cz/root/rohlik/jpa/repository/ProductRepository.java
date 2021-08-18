package cz.root.rohlik.jpa.repository;

import cz.root.rohlik.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Modifying
    @Query(value = "delete from PRODUCT_TABLE a where a.name = :productName", nativeQuery = true)
    void deleteProductByName(@Param("productName") String productName);

    @Query(value = "select * from PRODUCT_TABLE a where a.name = :productName", nativeQuery = true)
    Product findProductByName(@Param("productName") String productName);
}
