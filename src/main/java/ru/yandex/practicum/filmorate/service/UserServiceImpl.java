package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static int count;
    @Autowired
    private UserRepository repository;

    @Override
    public User addNewUser(User user) {
        user.setId(++count);
        user.validate();
        log.info("Создан пользователь: {}", user);
        return repository.save(user);
    }

    @Override
    public User updateIncomingUser(User user) {
        Optional<User> existingUser = repository.findById(user.getId());
        if (existingUser.isPresent()) {
            user.validate();
            repository.save(user);
            log.info("Пользователь обновлён: {}", user);
        } else {
            throw new UserNotFoundException("Пользователь с указанным id не найден");
        }
        return user;
    }

    @Override
    public List<User> returnAllUsers() {
        return repository.findAll();
    }

}
