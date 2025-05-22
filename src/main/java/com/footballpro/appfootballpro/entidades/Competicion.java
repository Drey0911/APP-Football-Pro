package com.footballpro.appfootballpro.entidades;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "competiciones")
public class Competicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int montoPremio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @ManyToMany
    @JoinTable(
        name = "CLUBES_COMPETICIONES",
        joinColumns = @JoinColumn(name = "id_competicion"),
        inverseJoinColumns = @JoinColumn(name = "id_club")
    )
    private Set<Club> clubes = new HashSet<>();

    // Getter y Setter para clubes
    public Set<Club> getClubes() {
        return clubes;
    }

    public void setClubes(Set<Club> clubes) {
        this.clubes = clubes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getMontoPremio() {
        return montoPremio;
    }

    public void setMontoPremio(int montoPremio) {
        this.montoPremio = montoPremio;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}
