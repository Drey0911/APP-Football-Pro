package com.footballpro.appfootballpro.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.footballpro.appfootballpro.entidades.Club;
import com.footballpro.appfootballpro.entidades.Jugador;
import com.footballpro.appfootballpro.repositorio.ClubRepository;
import com.footballpro.appfootballpro.repositorio.JugadorRepository;

import java.util.List;

@Controller
@RequestMapping("/jugadores")
public class JugadorController {

    @Autowired
    private JugadorRepository jugadorRepository;
    
    @Autowired
    private ClubRepository clubRepository;

    @GetMapping
    public String listarJugadores(Model model) {
        List<Jugador> jugadores = jugadorRepository.findAll();
        model.addAttribute("jugadores", jugadores);
        return "jugadores/listarJugadores";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Jugador jugador = new Jugador();
        List<Club> clubs = clubRepository.findAll();
        
        model.addAttribute("jugador", jugador);
        model.addAttribute("clubs", clubs);
        return "jugadores/formJugadores";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Jugador jugador = jugadorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado con ID: " + id));
        List<Club> clubs = clubRepository.findAll();
        
        model.addAttribute("jugador", jugador);
        model.addAttribute("clubs", clubs);
        return "jugadores/formJugadores";
    }

    @PostMapping("/guardar")
    public String guardarJugador(@ModelAttribute Jugador jugador, 
                               @RequestParam(required = false) Long clubId,
                               RedirectAttributes redirectAttributes) {
        if (clubId != null) {
            Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("Club no encontrado con ID: " + clubId));
            jugador.setClub(club);
        } else {
            jugador.setClub(null); // Agente Libre
        }
        
        jugadorRepository.save(jugador);
        redirectAttributes.addFlashAttribute("success", "Jugador guardado exitosamente");
        return "redirect:/jugadores";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarJugador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        jugadorRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Jugador eliminado exitosamente");
        return "redirect:/jugadores";
    }
}