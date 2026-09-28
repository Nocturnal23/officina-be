package com.officina.officina_be.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.officina.officina_be.dto.request.UserRequest;
import com.officina.officina_be.dto.response.PageResponse;
import com.officina.officina_be.dto.response.UserResponse;
import com.officina.officina_be.entity.User;

@Component
public class UserMapper {
    //Da RequestDTO a enttità DB.
    public User toEntity(UserRequest request) {
        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(request.password());
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPhoneNumber(request.phoneNumber().trim());
        return user;
    }

    // Da entita DB a ResponseDTO.
    public UserResponse toResponse(User entity) {
        return new UserResponse(
            entity.getId(),
            entity.getEmail(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getPhoneNumber(),
            entity.getRole()
        );
    }


    public PageResponse<UserResponse> toPageResponse(Page<User> page) {
        List<UserResponse> content = page.getContent()
            .stream()
            .map(this::toResponse)
            .toList();

        return new PageResponse<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}
