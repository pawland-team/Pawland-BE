package com.pawland.user.service;

import com.pawland.user.dto.request.UserInfoUpdateRequest;
import com.pawland.user.dto.response.UserInfoResponse;
import com.pawland.user.dto.response.UserInfoUpdateResponse;
import com.pawland.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public void checkNicknameDuplicate(String nickname) {
        Optional<User> foundUser = userRepository.findByNickname(nickname);
        if (foundUser.isPresent()) {
            throw new UserException.AlreadyExistsNickname();
        }
    }

    public void checkEmailDuplicate(String email) {
        Optional<User> foundUser = userRepository.findByEmail(email);
        if (foundUser.isPresent()) {
            throw new UserException.AlreadyExistsEmail();
        }
    }

    @Transactional
    public void register(User user) {
        checkNicknameDuplicate(user.getNickname());
        checkEmailDuplicate(user.getEmail());
        userRepository.save(user);
    }

    public UserInfoResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserException.NotFoundUser::new);
        // TODO: 평점 조회 로직 구현 필요
        return new UserInfoResponse(user, 3.5);
    }

    @Transactional
    public UserInfoUpdateResponse updateUser(String email, UserInfoUpdateRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserException.NotFoundUser::new);

        if (isNicknameChanged(request.getNickname(), user)) {
            checkNicknameDuplicate(request.getNickname());
        }
        user.updateProfile(request.toUser());
        // TODO: 평점 조회 로직 구현 필요
        return new UserInfoUpdateResponse(user, 3.5);
    }

    private static boolean isNicknameChanged(String requestNickname, User user) {
        return !requestNickname.equals(user.getNickname());
    }
}
