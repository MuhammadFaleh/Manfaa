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
import com.v1.manfaa.Api.ApiResponse;
import com.v1.manfaa.DTO.In.TicketDTOIn;
import com.v1.manfaa.DTO.In.TicketResolveDTOIn;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/get-all") // admin
    public ResponseEntity<?> getAllTickets(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(ticketService.getAllTickets());
    }

    @PostMapping("/add-contract/{contractId}") // user
    public ResponseEntity<?> addTicketContract(@PathVariable Integer contractId,
                                               @Valid @RequestBody TicketDTOIn dto,
                                               @AuthenticationPrincipal User user) {
        ticketService.addTicketContract(user.getId(), contractId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Contract ticket created successfully"));
    }

    @PostMapping("/add-suggestion") // user
    public ResponseEntity<?> addTicketSuggestion(@Valid @RequestBody TicketDTOIn dto,
                                                 @AuthenticationPrincipal User user) {
        ticketService.addTicketSuggestion(user.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Suggestion ticket created successfully"));
    }

    @PostMapping("/add-subscription") // user
    public ResponseEntity<?> addTicketSubscription(@Valid @RequestBody TicketDTOIn dto,
                                                   @AuthenticationPrincipal User user) {
        ticketService.addTicketSubscription(user.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Subscription ticket created successfully"));
    }

    @PostMapping("/add-platform") // user
    public ResponseEntity<?> addTicketPlatform(@Valid @RequestBody TicketDTOIn dto,
                                               @AuthenticationPrincipal User user) {
        ticketService.addTicketPlatform(user.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Platform ticket created successfully"));
    }

    @PutMapping("/update/{ticketId}") // user
    public ResponseEntity<?> updateTicket(@PathVariable Integer ticketId,
                                          @Valid @RequestBody TicketDTOIn dto,
                                          @AuthenticationPrincipal User user) {
        ticketService.updateTicket(ticketId, user.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket updated successfully"));
    }

    @DeleteMapping("/delete/{ticketId}") // user
    public ResponseEntity<?> deleteTicket(@PathVariable Integer ticketId,
                                          @AuthenticationPrincipal User user) {
        ticketService.deleteTicket(ticketId, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Ticket deleted successfully"));
    }

    @PutMapping("/resolve") // admin
    public ResponseEntity<?> resolveTicket(@Valid @RequestBody TicketResolveDTOIn dto,
                                           @AuthenticationPrincipal User user) {
        ticketService.resolveTicket(user.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket resolved successfully"));
    }

    @PutMapping("/reject") // admin
    public ResponseEntity<?> rejectTicket(@Valid @RequestBody TicketResolveDTOIn dto,
                                          @AuthenticationPrincipal User user) {
        ticketService.rejectTicket(user.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Ticket rejected successfully"));
    }

    @GetMapping("/my-tickets") // user
    public ResponseEntity<?> showMyTickets(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(ticketService.showMyTickets(user.getId()));
    }

    @GetMapping("/my-tickets/status/{status}") // user
    public ResponseEntity<?> getMyTicketsByStatus(@PathVariable String status,
                                                  @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(ticketService.getMyTicketsByStatus(user.getId(), status));
    }

    @GetMapping("/subscriber/{isSubscriber}") // admin
    public ResponseEntity<?> getTicketsBySubscriberStatus(@PathVariable Boolean isSubscriber,
                                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(ticketService.getTicketsBySubscriberStatus(isSubscriber));
    }

    @GetMapping("/subscriber/{isSubscriber}/category/{category}") // admin
    public ResponseEntity<?> getTicketsByCategoryAndSubscriberStatus(@PathVariable Boolean isSubscriber,
                                                                     @PathVariable String category,
                                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(ticketService.getTicketsByCategoryAndSubscriberStatus(category, isSubscriber));
    }

    @GetMapping("/subscriber/{isSubscriber}/priority/{priority}") // admin
    public ResponseEntity<?> getTicketsByPriorityAndSubscriberStatus(@PathVariable Boolean isSubscriber,
                                                                     @PathVariable String priority,
                                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(ticketService.getTicketsByPriorityAndSubscriberStatus(priority, isSubscriber));
    }

    @GetMapping("/subscriber/{isSubscriber}/category/{category}/priority/{priority}") // admin
    public ResponseEntity<?> getTicketsByCategoryAndPriorityAndSubscriberStatus(@PathVariable Boolean isSubscriber,
                                                                                @PathVariable String category,
                                                                                @PathVariable String priority,
                                                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(ticketService.getTicketsByCategoryAndPriorityAndSubscriberStatus(category, priority, isSubscriber));
    }
}