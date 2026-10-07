package com.officina.officina_be.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officina.officina_be.config.JwtService;
import com.officina.officina_be.core.utils.Constants;
import com.officina.officina_be.dto.request.ChangePasswordRequest;
import com.officina.officina_be.dto.request.LoginRequest;
import com.officina.officina_be.dto.request.UserRequest;
import com.officina.officina_be.dto.response.LoginResponse;
import com.officina.officina_be.dto.response.UserResponse;
import com.officina.officina_be.entity.User;
import com.officina.officina_be.entity.Vehicle;
import com.officina.officina_be.error.DuplicateResource;
import com.officina.officina_be.error.ResourceNotFound;
import com.officina.officina_be.error.Unauthorized;
import com.officina.officina_be.mapper.UserMapper;
import com.officina.officina_be.repository.UserRepository;
import com.officina.officina_be.repository.VehicleRepository;
import com.officina.officina_be.dto.response.PageResponse;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder pswEncoder;
    private final JwtService jwtService;
    private final VehicleRepository vehicleRepo;

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
    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepo.findByEmailIgnoreCaseAndDeletedFalse(request.email()).orElseThrow(() -> new Unauthorized("Credenziali errate"));

        if (!pswEncoder.matches(request.password(), user.getPassword())) {
            throw new Unauthorized("Credenziali non valide");
        }

        String token = jwtService.generateToken(user);
        UserResponse userResponse = userMapper.toResponse(user);

        return new LoginResponse(token, userResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByID(Long id) {
        User user = userRepo.findByIdAndDeletedFalse(id).orElseThrow(() -> new ResourceNotFound("Utente " + id + " non trovato"));

        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, Map<String, Object> updates) {
        User user = userRepo.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFound("Utente " + id + " non trovato"));

        if (updates.containsKey("firstName")) {
            user.setFirstName((String) updates.get("firstName"));
        }
        if (updates.containsKey("lastName")) {
            user.setLastName((String) updates.get("lastName"));
        }
        if (updates.containsKey("phoneNumber")) {
            user.setPhoneNumber((String) updates.get("phoneNumber"));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }

        User savedUser = userRepo.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepo.findByEmailIgnoreCaseAndDeletedFalse(email)
            .orElseThrow(() -> new ResourceNotFound("Utente non trovato"));
    }
    
    @Transactional 
    public User findById(Long id) {
        return userRepo.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFound("Utente non trovato"));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepo.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new ResourceNotFound("Utente non trovato") );

        List<Vehicle> vehicles = vehicleRepo.findByUser_IdAndDeletedFalse(userId);

        for(Vehicle veh: vehicles) {
            veh.setDeleted(true);
            veh.setUser(null); 
        }

        vehicleRepo.saveAll(vehicles);

        userRepo.delete(user);
    }

    @Transactional 
    public PageResponse<UserResponse> getAllUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").descending());

        Page<User> userPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            userPage = userRepo.searchUsers(keyword.trim(), pageable);
        } else {
            userPage = userRepo.findByDeletedFalse(pageable);
        }


        return userMapper.toPageResponse(userPage);
    }

    @Transactional 
    public void changePassword(ChangePasswordRequest request, Long userId) {
        User user = userRepo.findByIdAndDeletedFalse(userId).orElseThrow(() -> new ResourceNotFound("Utente " + userId + " non trovato"));

        if (!pswEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new Unauthorized("Credenziali non valide");
        }

        user.setPassword(pswEncoder.encode(request.newPassword()));
        userRepo.save(user);
    }
}
