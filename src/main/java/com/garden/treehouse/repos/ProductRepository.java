package com.garden.treehouse.repos;


import com.garden.treehouse.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<List<Product>> findByCategoryAndActive(String category, boolean active);
    Optional<List<Product>> findAllByActive(boolean active);

    Optional<List<Product>> findTop4ByActiveOrderByIdDesc(boolean active);
    Optional<List<Product>> findProductsByNameContainingAndActive(String name, boolean active);
    Optional<List<Product>> findTop4ByCategoryAndActiveOrderByIdDesc(String name, boolean active);

}
