package com.AccountService.UnitTest;

import com.AccountService.entity.UserEntity;
import com.AccountService.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {


    @Autowired
    UserRepository userRepository;
    String correctEmail;
    String incorrectEmail;

    @BeforeEach
    void addUser() {
        userRepository.save(new UserEntity("Kuba", "Kaiser", "kuba@acme.com", "123","ROLE_USER"));
        correctEmail = "kuba@acme.com";
        incorrectEmail = "jakub@acme.com";
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldExistUser() {
        assertTrue(userRepository.existsByEmailIgnoreCase(correctEmail));
    }

    @Test
    void shouldNotExistUser() {
        assertFalse(userRepository.existsByEmailIgnoreCase(incorrectEmail));
    }

    @Test
    void shouldFindUser() {
        assertNotNull(userRepository.findByEmailIgnoreCase(correctEmail));
    }

    @Test
    void shouldNotFindUser() {
        assertNull(userRepository.findByEmailIgnoreCase(incorrectEmail));
    }

     @Test
    void shouldReturnCorrectEmail() {
         UserEntity user = userRepository.findByEmailIgnoreCase(correctEmail);
         assertEquals("kuba@acme.com",user.getEmail());
     }

}
