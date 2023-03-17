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
    public User addUser(@RequestBody @Valid User user) {
            user  = userService.addUser(user);
            log.info("Создан пользователь: {}", user.toString());
            return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
            user = userService.updateUser(user);
            log.info("Пользователь обновлён: {}", user.toString());
            return user;
    }

    @GetMapping
    public List<User> returnAllUsers() {
        return userService.returnAllUsers();
    }

}
