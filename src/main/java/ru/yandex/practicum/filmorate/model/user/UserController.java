package ru.yandex.practicum.filmorate.model.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

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
