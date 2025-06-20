package com.main.service;

import com.main.entity.User;
import com.main.repository.UserRepository;
import com.main.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    User user = null;

    @BeforeEach
    public void setup() {
        user = getUser();
    }

    @Test
    public void testAddUser() {
        when(userRepository.save(user)).thenReturn(user);
        // Act
        User result = userService.addUser(user);
        // Assert
        assertEquals(user, result);
        assertEquals("Jane Doe", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetAllUsers() {
        List<User> mockList = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(mockList);

        // Act
        List<User> result = userService.getAllUsers();
        // Assert
        assertEquals(mockList, result);
        verify(userRepository, times(1)).findAll();
    }

    private User getUser() {
        User user = new User();
        user.setAccountNumber(12345678L);
        user.setName("Jane Doe");
        user.setEmail("abc@gmail.com");
        user.setBalance(12500);
        return user;
    }
}