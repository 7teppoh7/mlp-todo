package ru.sstu.mylittlediary.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sstu.mylittlediary.dto.UserDTO;
import ru.sstu.mylittlediary.entities.Role;
import ru.sstu.mylittlediary.entities.User;
import ru.sstu.mylittlediary.services.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
    }

    @GetMapping
    public String redirectToMain() {
        return "redirect:/notes";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userDto", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String addUser(@ModelAttribute("userDto") @Valid UserDTO userDto, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "register";
        }
        User user = new User(userDto);
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email занят");
            return "register";
        }
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setUserRoles(roles);
        userService.save(user);
        return "redirect:/login";
    }
}
