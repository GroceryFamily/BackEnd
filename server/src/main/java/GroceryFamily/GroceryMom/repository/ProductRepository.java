package GroceryFamily.GroceryMom.repository;

import GroceryFamily.GroceryMom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    @Query(value = "SELECT * FROM product ORDER BY id ASC LIMIT :pageSize", nativeQuery = true)
    List<Product> list(@Param("pageSize") int pageSize);

    @Query(value = "SELECT * FROM product WHERE id > :lastId ORDER BY id ASC LIMIT :pageSize", nativeQuery = true)
    List<Product> list(@Param("lastId") String lastId, @Param("pageSize") int pageSize);
}