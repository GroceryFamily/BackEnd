package GroceryFamily.GroceryMom.repository;

import GroceryFamily.GroceryMom.repository.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<PriceEntity, String> {}