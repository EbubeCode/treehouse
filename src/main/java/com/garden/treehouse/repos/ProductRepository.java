package com.garden.treehouse.repos;


import com.garden.treehouse.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<List<Product>> findByCategory(String category);

    Optional<List<Product>> findProductByNameContaining(String name);

}