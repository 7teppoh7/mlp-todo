package ru.sstu.mylittlediary.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.sstu.mylittlediary.dto.CaseDTO;
import ru.sstu.mylittlediary.entities.Case;
import ru.sstu.mylittlediary.entities.Note;
import ru.sstu.mylittlediary.entities.User;
import ru.sstu.mylittlediary.services.CaseService;
import ru.sstu.mylittlediary.services.NoteService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class ApiController {

    private final CaseService caseService;
    private final NoteService noteService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @GetMapping("/note")
    public List<Note> getNotes(@AuthenticationPrincipal User user) {
        return noteService.findAllByUser(user);
    }

    @GetMapping("/case")
    public List<Case> getCases(@AuthenticationPrincipal User user, @RequestParam(value = "date", required = false) String dateQuery) {
        LocalDate date = null;
        if (dateQuery != null) {
            date = LocalDate.parse(dateQuery, formatter);
        }
        return caseService.findAllByUserAndDate(user, date);
    }

    //case is also a note :D
    @PostMapping("/note")
    public Note createNote(@AuthenticationPrincipal User user, @RequestBody CaseDTO newNote) {
        return noteService.saveNote(user, newNote.getName(), newNote.getDescription());
    }

    @PostMapping("/case")
    public Case createCase(@AuthenticationPrincipal User user, @RequestBody CaseDTO newCase) {
        return caseService.saveCase(user, newCase.getName(), newCase.getDescription(), LocalDate.parse(newCase.getDeadline(), formatter));
    }

    @DeleteMapping("/case/{caseId}")
    public List<Case> deleteCase(@PathVariable String caseId, @AuthenticationPrincipal User user, @RequestParam(value = "date", required = false) String dateQuery) {
        Long id = Long.parseLong(caseId.split("-")[1]);
        caseService.deleteCase(id, user);
        LocalDate date = null;
        if (dateQuery != null) {
            date = LocalDate.parse(dateQuery, formatter);
        }
        return caseService.findAllByUserAndDate(user, date);
    }

    @DeleteMapping("/note/{noteId}")
    public List<Note> deleteNote(@PathVariable String noteId, @AuthenticationPrincipal User user) {
        Long id = Long.parseLong(noteId.split("-")[1]);
        noteService.deleteNote(id, user);
        return noteService.findAllByUser(user);
    }

    @PutMapping("/case/{caseId}")
    public void completeCase(@PathVariable String caseId, @AuthenticationPrincipal User user) {
        Long id = Long.parseLong(caseId.split("-")[1]);
        caseService.completeCase(id, user);
    }

    @PutMapping("/case")
    public void editCase(@AuthenticationPrincipal User user, @RequestBody CaseDTO updatedCase) {
        Long caseId = Long.parseLong(updatedCase.getId().split("-")[1]);
        caseService.updateCase(caseId, user, updatedCase.getName(), updatedCase.getDescription(), LocalDate.parse(updatedCase.getDeadline(), formatter));
    }

    @PutMapping("/note")
    public void editNote(@AuthenticationPrincipal User user, @RequestBody CaseDTO updatedNote) {
        Long id = Long.parseLong(updatedNote.getId().split("-")[1]);
        noteService.updateNote(id, user, updatedNote.getName(), updatedNote.getDescription());
    }

    @GetMapping("/case/todos.txt")
    public void downloadCases(@AuthenticationPrincipal User user, HttpServletResponse response) {
        try {
            List<Case> cases = caseService.findAllByUser(user);
            StringBuilder result = new StringBuilder();
            for (Case c : cases) {
                result.append("Название: ").append(c.getName()).append("\n");
                result.append("Описание: ").append(c.getDescription()).append("\n");
                result.append("Дата создания: ").append(c.getCreationDate()).append("\n");
                result.append("Дата выполнения: ").append(c.getDeadline()).append("\n\n");
            }
            InputStream is = new ByteArrayInputStream(result.toString().getBytes(StandardCharsets.UTF_8));
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("Error during downloading cases");
        }
    }

    @GetMapping("/note/notes.txt")
    public void downloadNotes(@AuthenticationPrincipal User user, HttpServletResponse response) {
        try {
            List<Note> notes = noteService.findAllByUser(user);
            StringBuilder result = new StringBuilder();
            for (Note note : notes) {
                result.append("Название: ").append(note.getName()).append("\n");
                result.append("Описание: ").append(note.getDescription()).append("\n");
                result.append("Дата создания: ").append(note.getCreationDate()).append("\n\n");
            }
            InputStream is = new ByteArrayInputStream(result.toString().getBytes(StandardCharsets.UTF_8));
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("Error during downloading notes");
        }
    }
}
