package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    Ticket findTicketById(Integer id);
    List<Ticket> findByCompanyProfileIsSubscriber(Boolean isSubscriber);
    List<Ticket> findByCategoryAndCompanyProfileIsSubscriber(String category, Boolean isSubscriber);
    List<Ticket> findByPriorityAndCompanyProfileIsSubscriber(String priority, Boolean isSubscriber);
    List<Ticket> findByCategoryAndPriorityAndCompanyProfileIsSubscriber(String category, String priority, Boolean isSubscriber);
    List<Ticket> findByStatusAndCompanyProfileId(String status, Integer id);
    List<Ticket> findByCompanyProfileId(Integer id);
}
