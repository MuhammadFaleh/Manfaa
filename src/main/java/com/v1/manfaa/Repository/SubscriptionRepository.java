package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    Subscription findSubscriptionById(Integer id);

    Subscription findSubscriptionByCompanyProfileIdAndIsActive(Integer companyProfileId, Boolean isActive);
}
