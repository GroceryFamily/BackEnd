package GroceryFamily.GroceryMom.repository;

import GroceryFamily.GroceryMom.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, String> {}