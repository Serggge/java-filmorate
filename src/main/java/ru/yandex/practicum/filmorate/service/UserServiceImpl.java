package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static int count;
    private final UserRepository repository;

    @Override
    public User create(User user) {
        user.setId(++count);
        validate(user);
        log.info("Создан пользователь: {}", user);
        return repository.save(user);
    }

    @Override
    public User update(User user) {
        Optional<User> existingUser = repository.findById(user.getId());
        if (existingUser.isPresent()) {
            validate(user);
            repository.save(user);
            log.info("Пользователь обновлён: {}", user);
        } else {
            throw new UserNotFoundException(
                    String.format("Пользователь с id=%d не найден", user.getId())
            );
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    private static void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
