package com.example.systeme_suivi_ticket.controller;

import com.example.systeme_suivi_ticket.dto.ChartDataDTO;
import com.example.systeme_suivi_ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardApiController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/tickets-overview")
    public ResponseEntity<?> getTicketsOverviewChartData(@RequestParam(defaultValue = "month") String period) {
        ChartDataDTO newTicketsData = ticketService.getTicketStatsByPeriod(period, "created");
        // Pour "resolved", nous allons retourner les mêmes données que "new" pour la simplicité de l'exemple pour 'week' et 'year'
        ChartDataDTO resolvedTicketsData = "month".equals(period) ? ticketService.getTicketStatsByPeriod(period, "resolved") : newTicketsData;

        Map<String, Object> response = Map.of(
                "labels", newTicketsData.getLabels(),
                "newData", newTicketsData.getData(),
                "resolvedData", resolvedTicketsData.getData()
        );

        return ResponseEntity.ok(response);
    }
}

