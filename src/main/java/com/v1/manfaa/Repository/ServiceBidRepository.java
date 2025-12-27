package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.ServiceBid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceBidRepository extends JpaRepository<ServiceBid, Integer> {
    ServiceBid findServiceBidById(Integer id);
    List<ServiceBid> findServiceBidByServiceRequestId(Integer id);
}
