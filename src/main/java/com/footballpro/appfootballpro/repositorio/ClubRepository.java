package com.footballpro.appfootballpro.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.footballpro.appfootballpro.entidades.Club;

public interface ClubRepository extends JpaRepository<Club, Long> {
}
