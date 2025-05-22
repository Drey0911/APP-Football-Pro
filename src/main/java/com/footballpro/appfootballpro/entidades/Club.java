package com.footballpro.appfootballpro.entidades;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "clubes")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String pais;
    private String ciudad;
    private String estadio;
    private String fundador;
    
// Cambiamos estos campos para almacenar la imagen en la BD
    @Lob
    @Column(name = "escudo", columnDefinition = "LONGBLOB")
    private byte[] escudo;
    
    private String tipoImagen; // Tipo MIME de la imagen

    // Resto de las relaciones y métodos...
    
    // Modificamos los getters y setters para la imagen
    public byte[] getEscudo() {
        return escudo;
    }

    public void setEscudo(byte[] escudo) {
        this.escudo = escudo;
    }

    public String getTipoImagen() {
        return tipoImagen;
    }

    public void setTipoImagen(String tipoImagen) {
        this.tipoImagen = tipoImagen;
    }
    
    // Método para obtener la imagen como Base64
    public String getEscudoBase64() {
        if (escudo == null || escudo.length == 0) {
            return "";
        }
        return "data:" + tipoImagen + ";base64," + java.util.Base64.getEncoder().encodeToString(escudo);
    }// Tipo MIME de la imagen

    // Relacion Uno A Uno
    @OneToOne
    private Entrenador entrenador;

    // Relacion Uno A Muchos
    @OneToMany(mappedBy = "club")
    private Set<Jugador> jugadores = new HashSet<>();
    
    // Relacion Muchos A Uno
    @ManyToOne
    private Asociacion asociacion;

    // Relacion Muchos A Muchos
    @ManyToMany(mappedBy = "clubes")
    private Set<Competicion> competiciones = new HashSet<>();

    // Getters and Setters
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

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstadio() {
        return estadio;
    }

    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }

    public String getFundador() {
        return fundador;
    }

    public void setFundador(String fundador) {
        this.fundador = fundador;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }

    public Asociacion getAsociacion() {
        return asociacion;
    }

    public void setAsociacion(Asociacion asociacion) {
        this.asociacion = asociacion;
    }

    public Set<Competicion> getCompeticiones() {
        return competiciones;
    }

    public void setCompeticiones(Set<Competicion> competiciones) {
        this.competiciones = competiciones;
    }
}