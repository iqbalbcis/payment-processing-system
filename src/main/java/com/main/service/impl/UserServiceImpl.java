package com.main.service.impl;

import com.main.entity.User;
import com.main.repository.UserRepository;
import com.main.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User addUser(User user) {
        user.setAccountNumber(generateUniqueAccountNumber());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private long generateUniqueAccountNumber() {
        long accountNumber;
        do {
            accountNumber = ThreadLocalRandom.current().nextLong(71023487L,
                    99999999L);
        } while (userRepository.findById(accountNumber).isPresent());
        return accountNumber;
    }
}
