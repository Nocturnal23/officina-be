package com.officina.officina_be.services;

import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officina.officina_be.config.JwtService;
import com.officina.officina_be.core.utils.Constants;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder pswEncoder;
    private final JwtService jwtService;
    private final VehicleRepository vehicleRepo;
    private final VehicleService vehicleService;

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

        User savedUser = userRepo.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepo.findByEmailIgnoreCaseAndDeletedFalse(email)
            .orElseThrow(() -> new ResourceNotFound("Utente non trovato"));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepo.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new ResourceNotFound("Utente non trovato") );

        List<Vehicle> vehicles = vehicleRepo.findByUser_IdAndDeletedFalse(userId);

        for(Vehicle veh: vehicles) {
            vehicleService.deleteVehicle(veh.getId(), userId);
        }
            
        user.setDeleted(true);
        userRepo.save(user);
    }
}
