package com.footballpro.appfootballpro.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.footballpro.appfootballpro.entidades.Jugador;

import java.util.List;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    List<Jugador> findByClubId(Long clubId);
    Long countByClubId(Long clubId);
}