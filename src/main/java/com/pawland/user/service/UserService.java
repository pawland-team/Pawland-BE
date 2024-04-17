package com.pawland.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.pawland.global.exception.AlreadyExistsUserException;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void checkEmailDuplicate(String email) {
        Optional<User> foundUser = userRepository.findByEmail(email);
        if (foundUser.isPresent()) {
            throw new AlreadyExistsUserException();
        }
    }

    public void register(User user) {
        checkEmailDuplicate(user.getEmail());
        userRepository.save(user);
    }
}
