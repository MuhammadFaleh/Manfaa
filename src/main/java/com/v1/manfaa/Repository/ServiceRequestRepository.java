package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Integer> {
    ServiceRequest findServiceRequestById(Integer id);

}
