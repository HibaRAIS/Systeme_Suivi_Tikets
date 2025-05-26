package com.example.systeme_suivi_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.systeme_suivi_ticket.model.Roles;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {

}
