package ru.sstu.mylittlediary.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Table(name = "case_table")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String description;
    private LocalDate creationDate;
    private LocalDate deadline;
    private Boolean isDone = false;
    @Transient
    private Boolean isExpired = false;
    @Transient
    private String deadlineFormatted;
    @ManyToOne
    @ToString.Exclude
    private User user;

}
