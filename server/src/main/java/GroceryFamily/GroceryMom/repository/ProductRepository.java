package GroceryFamily.GroceryMom.repository;

import GroceryFamily.GroceryMom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, String> {
    @Query(value = "SELECT p FROM Product p")
    Page<Product> list(Pageable pageable);
}