package com.main.controller;

import com.main.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String BASE_URL_FOR_CREATE_USER = "/api/user/create-user";
    private static final String BASE_URL_FOR_FIND_ALL_USER = "/api/user/users";

    @Autowired
    private MockMvc mockMvc;

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        mysql.start();
    }

    @AfterAll
    static void afterAll() {
        mysql.stop();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAddUser() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(getUser());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL_FOR_CREATE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.balance").value(12500));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testFindAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL_FOR_FIND_ALL_USER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
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
