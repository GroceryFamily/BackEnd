package GroceryFamily.GroceryMom.repository;

import GroceryFamily.GroceryMom.repository.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    @Query(value = "SELECT * FROM product ORDER BY id ASC LIMIT :pageSize", nativeQuery = true)
    List<ProductEntity> list(@Param("pageSize") int pageSize);

    @Query(value = "SELECT * FROM product WHERE id >= :pageHeadId ORDER BY id ASC LIMIT :pageSize", nativeQuery = true)
    List<ProductEntity> list(@Param("pageHeadId") String pageHeadId, @Param("pageSize") int pageSize);
}