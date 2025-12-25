package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.ServiceBid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceBidRepository extends JpaRepository<ServiceBid, Integer> {
    ServiceBid findServiceBidById(Integer id);
}
