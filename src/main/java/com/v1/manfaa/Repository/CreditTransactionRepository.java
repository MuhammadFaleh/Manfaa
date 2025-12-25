package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.CreditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Integer> {

    CreditTransaction findCreditTransactionById(Integer id);
}
