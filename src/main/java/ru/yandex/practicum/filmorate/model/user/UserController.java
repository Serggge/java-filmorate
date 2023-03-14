package ru.yandex.practicum.filmorate.model.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@RequestBody @Valid User user) {
        try {
            return userService.add(user);
        } finally {
            log.info("Создан пользователь: {}", user.toString());
        }
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        try {
            return userService.update(user);
        } finally {
            log.info("Пользователь обновлён: {}", user.toString());
        }
    }

    @GetMapping
    public List<User> list() {
        return userService.list();
    }

}
