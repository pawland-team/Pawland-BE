package com.pawland.user.dto.response;

import com.pawland.user.domain.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;

    private UserResponse(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }

    public static UserResponse of(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
    }


}
