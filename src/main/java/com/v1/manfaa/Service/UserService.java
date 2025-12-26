package com.v1.manfaa.Service;

import com.v1.manfaa.Api.ApiException;
import com.v1.manfaa.DTO.In.UserDTOIn;
import com.v1.manfaa.DTO.Out.UserDTOOut;
import com.v1.manfaa.Model.User;
import com.v1.manfaa.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDTOOut> convertToDtoOut(List<User> users) {
        return users.stream()
                .map(user -> new UserDTOOut(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getPhone_Number(),
                        user.getRole()
                ))
                .toList();
    }

    public List<UserDTOOut> getAllUsers(){
        return convertToDtoOut(userRepository.findAll());

    }

    public void addUser(UserDTOIn userDtoIn){
        String hash = new BCryptPasswordEncoder().encode(userDtoIn.getPassword());
        User user = new User(null, userDtoIn.getUsername(), hash, userDtoIn.getEmail(), userDtoIn.getFullName(), userDtoIn.getPhone_Number(),
                "STARTUP",null);

        userRepository.save(user);


    }

    public void updateUser(Integer userId , UserDTOIn userDtoIn){
       User old =  userRepository.findUserById(userId);
       String hash = new BCryptPasswordEncoder().encode(userDtoIn.getPassword());

       if(old == null){
           throw new ApiException("User not found");
       }
       old.setUsername(userDtoIn.getUsername());
       old.setRole("STARTUP");
       old.setPhone_Number(userDtoIn.getPhone_Number());
       old.setPassword(hash);
       old.setFullName(userDtoIn.getFullName());
       old.setEmail(userDtoIn.getEmail());
       userRepository.save(old);

    }

    public void deleteUser(Integer userId){
        User user = userRepository.findUserById(userId);

        if(user == null){
            throw new ApiException("User not found");
        }
        userRepository.delete(user);
    }



}
