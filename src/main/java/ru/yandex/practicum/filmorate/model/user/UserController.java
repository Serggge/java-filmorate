package ru.yandex.practicum.filmorate.model.user;

import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@RequestBody @Valid User user) {
        return userService.add(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        return userService.add(user);
    }

    @GetMapping
    public List<User> list() {
        return userService.list();
    }

}
