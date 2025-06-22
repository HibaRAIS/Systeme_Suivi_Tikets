package com.example.systeme_suivi_ticket.service;

import com.example.systeme_suivi_ticket.dto.TicketStatusChartData;
import com.example.systeme_suivi_ticket.model.Ticket;
import com.example.systeme_suivi_ticket.model.User;
import com.example.systeme_suivi_ticket.repository.TicketRepository;
import com.example.systeme_suivi_ticket.repository.TicketStatusRepository;
import com.example.systeme_suivi_ticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    @Autowired
    private TicketStatusRepository ticketStatusRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private UserRepository userRepository;

    public DashboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public long getTotalTicketCount(Long userId) {
        return ticketRepository.countByCreatedById(userId);
    }

    public List<Ticket> getAllTicketsByUserId(Long userId) {
        return ticketRepository.findAllByCreatedById(userId);
    }

    public List<Ticket> getRecentTicketsForUser(Long userId) {
        return ticketRepository.findTop5ByCreatedByIdOrderByCreatedAtDesc(userId);
    }

    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            return null;
        }
    }

    public List<TicketStatusChartData> getTicketStatusChartData(String username) {
        List<Object[]> result = ticketStatusRepository.getStatusStatistics(username);
        List<TicketStatusChartData> chartData = new ArrayList<>();

        for (Object[] row : result) {
            String statusName = (String) row[0];
            Long ticketCount = (Long) row[1];
            chartData.add(new TicketStatusChartData(statusName, ticketCount));
        }

        return chartData;
    }
}

