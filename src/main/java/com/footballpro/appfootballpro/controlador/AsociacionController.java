package com.footballpro.appfootballpro.controlador;

import com.footballpro.appfootballpro.entidades.Asociacion;
import com.footballpro.appfootballpro.repositorio.AsociacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/asociaciones")
public class AsociacionController {

    @Autowired
    private AsociacionRepository asociacionRepository;

    @GetMapping
    public String listarAsociaciones(Model model) {
        List<Asociacion> asociaciones = asociacionRepository.findAll();
        model.addAttribute("asociaciones", asociaciones);
        return "asociaciones/listarAsociaciones";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("asociacion", new Asociacion());
        return "asociaciones/formAsociaciones";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Asociacion asociacion = asociacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de asociación inválido:" + id));
        model.addAttribute("asociacion", asociacion);
        return "asociaciones/formAsociaciones";
    }

    @PostMapping("/guardar")
    public String guardarAsociacion(@ModelAttribute Asociacion asociacion,RedirectAttributes redirectAttributes) {
        asociacionRepository.save(asociacion);
        redirectAttributes.addFlashAttribute("success", "Asociacion guardada exitosamente");
        return "redirect:/asociaciones";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarAsociacion(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        Asociacion asociacion = asociacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de asociación inválido:" + id));
        asociacionRepository.delete(asociacion);
        redirectAttributes.addFlashAttribute("success", "Asociacion eliminada exitosamente");
        return "redirect:/asociaciones";
    }
}