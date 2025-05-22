package com.footballpro.appfootballpro.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.footballpro.appfootballpro.entidades.Entrenador;

import java.util.List;

public interface EntrenadorRepository extends JpaRepository<Entrenador, Long> {
    List<Entrenador> findByClubIsNull();
}