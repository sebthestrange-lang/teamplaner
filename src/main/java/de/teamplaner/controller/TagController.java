package de.teamplaner.controller;

import de.teamplaner.model.Tag;
import de.teamplaner.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("tags", tagService.alleTags());
        return "tags/liste";
    }

    @GetMapping("/neu")
    public String neuFormular(Model model) {
        model.addAttribute("tag", new Tag());
        model.addAttribute("aktion", "Neuer Tag");
        return "tags/formular";
    }

    @PostMapping
    public String erstellen(@Valid @ModelAttribute("tag") Tag tag,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("aktion", "Neuer Tag");
            return "tags/formular";
        }
        tagService.speichern(tag);
        redirectAttributes.addFlashAttribute("erfolgsMeldung", "Tag wurde erstellt.");
        return "redirect:/tags";
    }

    @GetMapping("/{id}/bearbeiten")
    public String bearbeitenFormular(@PathVariable Long id, Model model) {
        model.addAttribute("tag", tagService.findByIdOrThrow(id));
        model.addAttribute("aktion", "Tag bearbeiten");
        return "tags/formular";
    }

    @PostMapping("/{id}")
    public String aktualisieren(@PathVariable Long id,
                                @Valid @ModelAttribute("tag") Tag formDaten,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("aktion", "Tag bearbeiten");
            return "tags/formular";
        }
        Tag tag = tagService.findByIdOrThrow(id);
        tag.setName(formDaten.getName());
        tag.setFarbe(formDaten.getFarbe());
        tagService.speichern(tag);
        redirectAttributes.addFlashAttribute("erfolgsMeldung", "Tag wurde aktualisiert.");
        return "redirect:/tags";
    }

    @PostMapping("/{id}/loeschen")
    public String loeschen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tagService.loeschen(id);
        redirectAttributes.addFlashAttribute("erfolgsMeldung", "Tag wurde gelöscht.");
        return "redirect:/tags";
    }
}
