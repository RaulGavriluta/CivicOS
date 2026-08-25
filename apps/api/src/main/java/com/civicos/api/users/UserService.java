package com.civicos.api.users;

import com.civicos.api.users.dto.ChangePasswordRequest;
import com.civicos.api.users.dto.CreateUserRequest;
import com.civicos.api.users.dto.UpdateUserRequest;
import com.civicos.api.users.dto.UserResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public User createUser(CreateUserRequest request){
        if(userRepository.findByEmail(request.email()).isPresent()){
            throw new UserAlreadyExistsException(request.email());
        }

        LocalDateTime now = LocalDateTime.now();

        User user = new User();

        user.setId(UUID.randomUUID());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return userRepository.save(user);
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Transactional
    public User updateUser(UUID id, UpdateUserRequest request){
        User user = getUserById(id);

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());

        return userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(UUID id){
        User user = getUserById(id);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request){
        User user = getUserById(id);

        if(!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())){
            throw new InvalidPasswordException("Current password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
