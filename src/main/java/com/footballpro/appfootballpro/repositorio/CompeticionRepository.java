package com.footballpro.appfootballpro.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.footballpro.appfootballpro.entidades.Competicion;

public interface CompeticionRepository extends JpaRepository<Competicion, Long> {
}
