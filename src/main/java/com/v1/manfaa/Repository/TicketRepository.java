package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    Ticket findTicketById(Integer id);

}
