package com.example.systeme_suivi_ticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TechnicianController {

    @GetMapping("/technician/dashboard")
    public String showTechnicianDashboard() {
        return "technicien/Dashboard";
    }

}
