package com.pawland.post.service;

import com.pawland.post.domain.Post;
import com.pawland.post.dto.request.PostCreateRequest;
import com.pawland.post.dto.response.PostResponse;
import com.pawland.post.repository.PostRepository;
import com.pawland.user.domain.User;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse uploadPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(UserException.NotFoundUser::new);

        Post post = postRepository.save(request.toPostWith(user));

        return PostResponse.of(post);
    }


}
