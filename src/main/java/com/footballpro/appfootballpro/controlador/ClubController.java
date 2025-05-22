package com.footballpro.appfootballpro.controlador;

import com.footballpro.appfootballpro.entidades.*;
import com.footballpro.appfootballpro.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/clubes")
public class ClubController {

    @Autowired
    private ClubRepository clubRepository;
    
    @Autowired
    private EntrenadorRepository entrenadorRepository;
    
    @Autowired
    private AsociacionRepository asociacionRepository;
    
    @Autowired
    private JugadorRepository jugadorRepository;
    
    @Autowired
    private CompeticionRepository competicionRepository;
    

    @GetMapping("")
    public String listarClubes(Model model) {
        List<Club> clubes = clubRepository.findAll();
        
        // Obtener conteo de jugadores por club
        Map<Long, Long> jugadoresCountPorClub = new HashMap<>();
        Map<Long, List<Jugador>> jugadoresPorClub = new HashMap<>();
        
        for (Club club : clubes) {
            Long count = jugadorRepository.countByClubId(club.getId());
            jugadoresCountPorClub.put(club.getId(), count);
            
            if (count > 0) {
                List<Jugador> jugadores = jugadorRepository.findByClubId(club.getId());
                jugadoresPorClub.put(club.getId(), jugadores);
            }
        }
        
        model.addAttribute("clubes", clubes);
        model.addAttribute("jugadoresCountPorClub", jugadoresCountPorClub);
        model.addAttribute("jugadoresPorClub", jugadoresPorClub);
        return "clubes/listarClubes";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Club club = new Club();
        
        // Obtener solo entrenadores sin club
        List<Entrenador> entrenadoresDisponibles = entrenadorRepository.findByClubIsNull();
        List<Asociacion> asociaciones = asociacionRepository.findAll();
        
        model.addAttribute("club", club);
        model.addAttribute("entrenadores", entrenadoresDisponibles);
        model.addAttribute("asociaciones", asociaciones);
        return "clubes/formClubes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Club no encontrado con ID: " + id));
        
        // Obtener todos los entrenadores sin club + el entrenador actual del club (si tiene)
        List<Entrenador> entrenadoresDisponibles = entrenadorRepository.findByClubIsNull();
        if (club.getEntrenador() != null) {
            entrenadoresDisponibles.add(club.getEntrenador());
        }
        
        List<Asociacion> asociaciones = asociacionRepository.findAll();
        
        model.addAttribute("club", club);
        model.addAttribute("entrenadores", entrenadoresDisponibles);
        model.addAttribute("asociaciones", asociaciones);
        return "clubes/formClubes";
    }

    @PostMapping("/guardar")
    public String guardarClub(
            @ModelAttribute("club") Club club,
            BindingResult result,
            @RequestParam("file") MultipartFile file,
            @RequestParam("asociacionId") Long asociacionId,
            @RequestParam(value = "entrenadorId", required = false) Long entrenadorId,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error en los datos del formulario");
            return "redirect:/clubes/nuevo";
        }
        
        try {
            // Manejar la asociación
            Asociacion asociacion = asociacionRepository.findById(asociacionId)
                    .orElseThrow(() -> new IllegalArgumentException("Asociación no encontrada"));
            club.setAsociacion(asociacion);
            
            // Manejar el entrenador
            if (entrenadorId != null) {
                Entrenador entrenador = entrenadorRepository.findById(entrenadorId)
                        .orElseThrow(() -> new IllegalArgumentException("Entrenador no encontrado"));
                
                if (entrenador.getClub() != null && entrenador.getClub().getId() != club.getId()) {
                    Club clubAnterior = entrenador.getClub();
                    clubAnterior.setEntrenador(null);
                    clubRepository.save(clubAnterior);
                }
                
                club.setEntrenador(entrenador);
                entrenador.setClub(club);
            } else {
                if (club.getEntrenador() != null) {
                    Entrenador entrenadorActual = club.getEntrenador();
                    entrenadorActual.setClub(null);
                    entrenadorRepository.save(entrenadorActual);
                    club.setEntrenador(null);
                }
            }
            
            // Manejar la imagen 
            if (!file.isEmpty()) {
                club.setEscudo(file.getBytes());
                club.setTipoImagen(file.getContentType());
            } else if (club.getId() != null) {
                // Si no se sube nueva imagen pero estamos editando, mantener la existente
                Club clubExistente = clubRepository.findById(club.getId()).orElse(null);
                if (clubExistente != null) {
                    club.setEscudo(clubExistente.getEscudo());
                    club.setTipoImagen(clubExistente.getTipoImagen());
                }
            }
            
            clubRepository.save(club);
            
            if (entrenadorId != null) {
                Entrenador entrenador = entrenadorRepository.findById(entrenadorId).orElse(null);
                if (entrenador != null) {
                    entrenador.setClub(club);
                    entrenadorRepository.save(entrenador);
                }
            }
            
            redirectAttributes.addFlashAttribute("success", club.getId() == null ? 
                    "Club creado exitosamente" : "Club actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el club: " + e.getMessage());
            return "redirect:/clubes/nuevo";
        }
        
        return "redirect:/clubes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarClub(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Club club = clubRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Club no encontrado con ID: " + id));
            
            // Liberar al entrenador asociado
            if (club.getEntrenador() != null) {
                Entrenador entrenador = club.getEntrenador();
                entrenador.setClub(null);
                entrenadorRepository.save(entrenador);
            }
            
            // Eliminar relaciones con jugadores
            List<Jugador> jugadores = jugadorRepository.findByClubId(club.getId());
            for (Jugador jugador : jugadores) {
                jugador.setClub(null);
                jugadorRepository.save(jugador);
            }
            
            // Eliminar relaciones con competiciones
            Set<Competicion> competiciones = club.getCompeticiones();
            for (Competicion competicion : competiciones) {
                competicion.getClubes().remove(club);
                competicionRepository.save(competicion);
            }
            
            clubRepository.delete(club);
            redirectAttributes.addFlashAttribute("success", "Club eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el club: " + e.getMessage());
        }
        
        return "redirect:/clubes";
    }
}