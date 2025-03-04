package com.srivath.order.repositories;

import com.srivath.order.models.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {

}
