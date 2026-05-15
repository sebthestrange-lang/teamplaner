package de.teamplaner.controller;

import de.teamplaner.service.AufgabeService;
import de.teamplaner.service.MitarbeiterService;
import de.teamplaner.service.ProjektService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/suche")
@RequiredArgsConstructor
public class SucheController {

    private final AufgabeService aufgabeService;
    private final ProjektService projektService;
    private final MitarbeiterService mitarbeiterService;

    @GetMapping
    public String suche(@RequestParam(required = false, defaultValue = "") String q, Model model) {
        model.addAttribute("q", q);
        if (!q.isBlank()) {
            model.addAttribute("aufgaben", aufgabeService.suchen(q));
            model.addAttribute("projekte", projektService.suchen(q));
            model.addAttribute("mitarbeiter", mitarbeiterService.suchen(q));
        }
        return "suche/ergebnisse";
    }
}
