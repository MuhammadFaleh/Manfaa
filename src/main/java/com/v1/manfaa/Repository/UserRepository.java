package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserById(Integer id);
    User findUserByUsername(String username);


    boolean existsByEmail(@NotBlank(message = "email is required") @Email(message = "email must be valid") String email);
}
