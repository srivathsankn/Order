package com.srivath.order.repositories;

import com.srivath.order.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Additional query methods can be defined here if needed

}
