package GroceryFamily.GroceryMom.repository;

import GroceryFamily.GroceryMom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {}