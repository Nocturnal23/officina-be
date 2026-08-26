package com.officina.officina_be.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officina.officina_be.core.utils.Constants;
import com.officina.officina_be.dto.request.LoginRequest;
import com.officina.officina_be.dto.request.UserRequest;
import com.officina.officina_be.dto.response.UserResponse;
import com.officina.officina_be.entity.User;
import com.officina.officina_be.error.DuplicateResource;
import com.officina.officina_be.error.ResourceNotFound;
import com.officina.officina_be.error.Unauthorized;
import com.officina.officina_be.mapper.UserMapper;
import com.officina.officina_be.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder pswEncoder;

    @Transactional
    public UserResponse registerUser(UserRequest request) {
        if(userRepo.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResource("L'indirizzo email è già presente nel sistema.");
        }

        User user = userMapper.toEntity(request);
        user.setRole(Constants.Role.USER);
        user.setPassword(pswEncoder.encode(request.password()));
        User savedUser = userRepo.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse loginUser(LoginRequest request) {
        User user = userRepo.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new Unauthorized("Credenziali errate"));

        if (!pswEncoder.matches(request.password(), user.getPassword())) {
            throw new Unauthorized("Credenziali non valide");
        }

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByID(Long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFound("Utente " + id + " non trovato"));

        return userMapper.toResponse(user);
    }
}
