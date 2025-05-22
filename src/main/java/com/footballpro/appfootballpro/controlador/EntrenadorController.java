package com.footballpro.appfootballpro.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.footballpro.appfootballpro.entidades.Entrenador;
import com.footballpro.appfootballpro.repositorio.EntrenadorRepository;

import java.util.List;

@Controller
@RequestMapping("/entrenadores")
public class EntrenadorController {

    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @GetMapping
    public String listarEntrenadores(Model model) {
        List<Entrenador> entrenadores = entrenadorRepository.findAll();
        model.addAttribute("entrenadores", entrenadores);
        return "entrenadores/listarEntrenadores";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("entrenador", new Entrenador());
        return "entrenadores/formEntrenadores";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Entrenador entrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de entrenador inválido:" + id));
        model.addAttribute("entrenador", entrenador);
        return "entrenadores/formEntrenadores";
    }

    @PostMapping("/guardar")
    public String guardarEntrenador(@ModelAttribute Entrenador entrenador, RedirectAttributes redirectAttributes) {
        entrenadorRepository.save(entrenador);
         redirectAttributes.addFlashAttribute("success", "Entrenador guardado exitosamente");
        return "redirect:/entrenadores";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEntrenador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Entrenador entrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de entrenador inválido:" + id));
        entrenadorRepository.delete(entrenador);
        redirectAttributes.addFlashAttribute("success", "Entrenador eliminado exitosamente");
        return "redirect:/entrenadores";
    }
}