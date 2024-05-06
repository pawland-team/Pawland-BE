package com.pawland.post.service;

import com.pawland.post.domain.Post;
import com.pawland.post.dto.request.PostCreateRequest;
import com.pawland.post.dto.request.PostSearchRequest;
import com.pawland.post.dto.response.PostResponse;
import com.pawland.post.repository.PostJpaRepository;
import com.pawland.post.repository.PostRepository;
import com.pawland.user.domain.User;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostJpaRepository postJpaRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostResponse uploadPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(UserException.NotFoundUser::new);

        Post post = postJpaRepository.save(request.toPostWith(user));

        return PostResponse.of(post);
    }

    @Transactional
    public Page<PostResponse> getPosts(PostSearchRequest postSearchRequest) {
        Pageable pageable = PageRequest.of(postSearchRequest.getPage() - 1, 6);
        Page<Post> posts = postRepository.getPostsBySearch(postSearchRequest, pageable);

        return posts.map(PostResponse::of);
    }
}
