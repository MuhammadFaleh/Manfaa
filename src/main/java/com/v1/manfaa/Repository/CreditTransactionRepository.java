package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.CreditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Integer> {

    CreditTransaction findCreditTransactionById(Integer id);

    List<CreditTransaction> findByPayingCompanyIdOrPaidCompanyId(Integer payingCompanyId, Integer paidCompanyId);
}
