package com.footballpro.appfootballpro.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.footballpro.appfootballpro.entidades.Club;
import com.footballpro.appfootballpro.entidades.Competicion;
import com.footballpro.appfootballpro.repositorio.ClubRepository;
import com.footballpro.appfootballpro.repositorio.CompeticionRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/competiciones")
public class CompeticionController {

    @Autowired
    private CompeticionRepository competicionRepository;
    
    @Autowired
    private ClubRepository clubRepository;

    @GetMapping
    public String listarCompeticiones(Model model) {
        List<Competicion> competiciones = competicionRepository.findAll();
        
        // Calcular días entre fechas
        Map<Long, Long> diasPorCompeticion = new HashMap<>();
        Map<Long, Integer> clubesCountPorCompeticion = new HashMap<>();
        
        for (Competicion comp : competiciones) {
            if (comp.getFechaInicio() != null && comp.getFechaFin() != null) {
                long dias = ChronoUnit.DAYS.between(comp.getFechaInicio(), comp.getFechaFin());
                diasPorCompeticion.put(comp.getId(), dias);
            }
            // Contar clubes participantes
            clubesCountPorCompeticion.put(comp.getId(), comp.getClubes().size());
        }
        
        model.addAttribute("competiciones", competiciones);
        model.addAttribute("diasPorCompeticion", diasPorCompeticion);
        model.addAttribute("clubesCountPorCompeticion", clubesCountPorCompeticion);
        return "competiciones/listarCompeticiones";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Competicion competicion = new Competicion();
        competicion.setFechaInicio(LocalDate.now());
        competicion.setFechaFin(LocalDate.now().plusMonths(6));
        
        List<Club> clubes = clubRepository.findAll();
        
        model.addAttribute("competicion", competicion);
        model.addAttribute("todosClubes", clubes);
        return "competiciones/formCompeticiones";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Competicion competicion = competicionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competición no encontrada con id: " + id));
        
        List<Club> clubes = clubRepository.findAll();
        
        model.addAttribute("competicion", competicion);
        model.addAttribute("todosClubes", clubes);
        return "competiciones/formCompeticiones";
    }

    @PostMapping("/guardar")
    public String guardarCompeticion(@ModelAttribute Competicion competicion, 
                                   @RequestParam(required = false) List<Long> clubesIds,
                                   RedirectAttributes redirectAttributes) {
        
        if (competicion.getFechaFin().isBefore(competicion.getFechaInicio())) {
            redirectAttributes.addFlashAttribute("error", "La fecha de fin no puede ser anterior a la fecha de inicio");
            return "redirect:/competiciones/nuevo";
        }
        
        // Guardar la competición primero para obtener el ID si es nueva
        Competicion competicionGuardada = competicionRepository.save(competicion);
        
        // Manejar los clubes participantes
        if (clubesIds != null && !clubesIds.isEmpty()) {
            Set<Club> clubesSeleccionados = new HashSet<>(clubRepository.findAllById(clubesIds));
            competicionGuardada.setClubes(clubesSeleccionados);
            competicionRepository.save(competicionGuardada);
        } else {
            competicionGuardada.getClubes().clear();
            competicionRepository.save(competicionGuardada);
        }
        
        redirectAttributes.addFlashAttribute("success", "Competición guardada exitosamente");
        return "redirect:/competiciones";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCompeticion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            competicionRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Competición eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar la competición porque está asociada a clubes");
        }
        return "redirect:/competiciones";
    }
}