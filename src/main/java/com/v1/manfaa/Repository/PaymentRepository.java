package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByPaymentId(String paymentId);
    Payment findFirstByCompanyProfileIdOrderByCreatedAtDesc(Integer id);
}