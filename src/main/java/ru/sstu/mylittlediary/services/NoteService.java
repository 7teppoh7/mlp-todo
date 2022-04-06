package ru.sstu.mylittlediary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sstu.mylittlediary.entities.Case;
import ru.sstu.mylittlediary.entities.Note;
import ru.sstu.mylittlediary.entities.User;
import ru.sstu.mylittlediary.repositories.NoteRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    public List<Note> findAllByUser(User user) {
        return noteRepository.findAllByUserId(user.getId());
    }

    public Note saveNote(User user, String name, String description) {
        Note note = Note.builder()
                .creationDate(LocalDate.now())
                .name(name)
                .description(description)
                .user(user)
                .build();
        return noteRepository.save(note);
    }

    public void deleteNote(Long noteId, User user) {
        noteRepository.deleteById(noteId);
    }

    public void updateNote(Long id, User user, String name, String description) {
        Optional<Note> noteOptional = noteRepository.findById(id);
        if (!noteOptional.isPresent()) {
            System.out.println("Note with id " + id + " not found!");
            return;
        }
        Note noteForUpdate = noteOptional.get();
        noteForUpdate.setName(name);
        noteForUpdate.setDescription(description);
        noteRepository.save(noteForUpdate);
    }
}
