package com.garden.treehouse.events;

import com.garden.treehouse.model.Order;
import com.garden.treehouse.model.User;

public record OrderCreated(User user, Order order) {
}
