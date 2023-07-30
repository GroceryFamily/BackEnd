package GroceryFamily.GroceryElders.repo;

import GroceryFamily.GroceryElders.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {}