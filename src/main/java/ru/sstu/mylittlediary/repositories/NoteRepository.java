package ru.sstu.mylittlediary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sstu.mylittlediary.entities.Note;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByUserId(Long id);
}
