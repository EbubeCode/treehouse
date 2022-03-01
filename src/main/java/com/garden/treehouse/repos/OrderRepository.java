package com.garden.treehouse.repos;

import com.garden.treehouse.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long>{

}
