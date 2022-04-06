package ru.sstu.mylittlediary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sstu.mylittlediary.entities.Case;
import ru.sstu.mylittlediary.entities.User;
import ru.sstu.mylittlediary.repositories.CaseRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public List<Case> findAllByUser(User user) {
        return caseRepository.findAllByUserId(user.getId());
    }

    public List<Case> findAllByUserAndDate(User user, LocalDate date) {
        List<Case> allCases = findAllByUser(user).stream()
                .peek(c -> {
                    if (c.getDeadline().isBefore(LocalDate.now())) {
                        c.setIsExpired(true);
                    }
                    c.setDeadlineFormatted(c.getDeadline().format(formatter));
                })
                .collect(Collectors.toList());
        if (date == null) {
            date = LocalDate.now();
        }
        LocalDate finalDate = date;
        return allCases.stream()
                .filter(c -> c.getDeadline().equals(finalDate))
//                .sorted(Comparator.comparing(Case::getIsDone))
                .collect(Collectors.toList());

    }

    public Case saveCase(User user, String name, String description, LocalDate deadline) {
        Case newCase = Case.builder()
                .creationDate(LocalDate.now())
                .deadline(deadline)
                .name(name)
                .description(description)
                .isDone(false)
                .user(user)
                .build();
        return caseRepository.save(newCase);
    }

    public void updateCase(Long caseId, User user, String name, String description, LocalDate updDeadline) {
        Optional<Case> caseOptional = caseRepository.findById(caseId);
        if (!caseOptional.isPresent()) {
            System.out.println("Case with id " + caseId + " not found!");
            return;
        }
        Case caseForUpdate = caseOptional.get();
        caseForUpdate.setDeadline(updDeadline);
        caseForUpdate.setName(name);
        caseForUpdate.setDescription(description);
        caseRepository.save(caseForUpdate);
    }

    public void deleteCase(Long id, User user) {
        caseRepository.deleteById(id);
    }

    public void completeCase(Long id, User user) {
        Optional<Case> caseOptional = caseRepository.findById(id);
        if (!caseOptional.isPresent()) {
            System.out.println("Case with id " + " not present!");
            return;
        }
        Case updCase = caseOptional.get();
        boolean isDone = updCase.getIsDone();
        updCase.setIsDone(!isDone);
        caseRepository.save(updCase);
    }
}
