package com.garden.treehouse.repos;

import com.garden.treehouse.model.Order;
import com.garden.treehouse.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long>{

    Optional<Order> findOrderByUserAndOrderStatus(User user, String status);
}
