package com.v1.manfaa.Controller;

import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.TicketDTOIn;
import com.v1.manfaa.DTO.Out.TicketDTOOut;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/get") // admin
    public ResponseEntity<List<TicketDTOOut>> getAllTickets(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(ticketService.getAllTickets(user.getId()));
    }

    @PostMapping("/add/{companyId}/{contractId}")
    public ResponseEntity<?> addTicket(@PathVariable Integer companyId, @PathVariable Integer contractId,
                                       @RequestBody @Valid TicketDTOIn ticketDTOIn) {
        ticketService.addTicket(companyId, contractId, ticketDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket added"));
    }

    @PutMapping("/update/{companyId}/{ticketId}")
    public ResponseEntity<?> updateTicket(@PathVariable Integer companyId, @PathVariable Integer ticketId,
                                          @RequestBody @Valid TicketDTOIn ticketDTOIn) {
        ticketService.updateTicket(ticketId, companyId, ticketDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket updated"));
    }

    @DeleteMapping("/delete/{companyId}/{ticketId}")
    public ResponseEntity<?> deleteTicket(@PathVariable Integer companyId, @PathVariable Integer ticketId) {
        ticketService.deleteTicket(ticketId, companyId);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket deleted"));
    }
}
