package ru.sstu.mylittlediary.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sstu.mylittlediary.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
