package com.pawland.user.dto;

import com.pawland.user.domain.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String name;

    private UserResponse(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public static UserResponse of(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }


}
