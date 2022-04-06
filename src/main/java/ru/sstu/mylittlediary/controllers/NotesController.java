package ru.sstu.mylittlediary.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sstu.mylittlediary.entities.User;
import ru.sstu.mylittlediary.services.CaseService;
import ru.sstu.mylittlediary.services.NoteService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NotesController {

    private final NoteService noteService;
    private final CaseService caseService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    @GetMapping
    public String getNotes(Model model, @AuthenticationPrincipal User user, @RequestParam(value = "date", required = false) String dateQuery) {
        LocalDate date = null;
        if (dateQuery != null) {
            date = LocalDate.parse(dateQuery, formatter);
        }
        model.addAttribute("username", user.getEmail());
        model.addAttribute("notes", noteService.findAllByUser(user));
        model.addAttribute("cases", caseService.findAllByUserAndDate(user, date));
        return "mlp";
    }

    @PostMapping("/createNote")
    public String createNote(Model model, @AuthenticationPrincipal User user, String name, String description) {
        noteService.saveNote(user, name, description);
        return "redirect:/notes";
    }

    @PostMapping("/createCase")
    public String createCase(Model model, @AuthenticationPrincipal User user, String name, String description, String deadline) {
        caseService.saveCase(user, name, description, LocalDate.parse(deadline, formatter));
        return "redirect:/notes";
    }

    @PostMapping("/editCase")
    public String editCase(Model model, @AuthenticationPrincipal User user, String id, String name, String description, String deadline) {
        Long caseId = Long.parseLong(id.split("-")[1]);
        caseService.updateCase(caseId, user, name, description, LocalDate.parse(deadline, formatter));
        return "redirect:/notes";
    }

    @PostMapping("/editNote")
    public String editNote(Model model, @AuthenticationPrincipal User user, Long id, String name, String description) {
        noteService.updateNote(id, user, name, description);
        return "redirect:/notes";
    }
}
