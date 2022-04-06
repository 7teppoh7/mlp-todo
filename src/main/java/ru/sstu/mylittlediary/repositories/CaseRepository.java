package ru.sstu.mylittlediary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sstu.mylittlediary.entities.Case;

import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {

    List<Case> findAllByUserId(Long id);
}
