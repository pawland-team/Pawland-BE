package com.pawland.post.service;

import com.pawland.post.domain.Post;
import com.pawland.post.domain.PostRecommend;
import com.pawland.post.dto.request.PostCreateRequest;
import com.pawland.post.dto.request.PostSearchRequest;
import com.pawland.post.dto.request.UpdatePostRequest;
import com.pawland.post.dto.response.PostResponse;
import com.pawland.post.exception.PostException;
import com.pawland.post.repository.PostJpaRepository;
import com.pawland.post.repository.PostRecommendJpaRepository;
import com.pawland.post.repository.PostRecommendRepository;
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

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostJpaRepository postJpaRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostRecommendJpaRepository postRecommendJpaRepository;
    private final PostRecommendRepository postRecommendRepository;

    @Transactional
    public PostResponse uploadPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserException.NotFoundUser::new);

        Post post = postJpaRepository.save(request.toPostWith(user));

        return PostResponse.of(post, user);
    }

    @Transactional
    public Page<PostResponse> getPosts(Long userId, PostSearchRequest postSearchRequest) {
        Pageable pageable = PageRequest.of(postSearchRequest.getPage() - 1, postSearchRequest.getSize());
        Page<Post> posts = postRepository.getPostsBySearch(postSearchRequest, pageable);

        return posts.map(p -> PostResponse.of(p, getUserById(userId)));
    }

    public Page<PostResponse> getMyPosts(Long userId, PostSearchRequest postSearchRequest) {
        Pageable pageable = PageRequest.of(postSearchRequest.getPage() - 1, postSearchRequest.getSize());
        Page<Post> myPosts = postRepository.getMyPosts(userId, pageable, postSearchRequest);

        return myPosts.map(post -> PostResponse.of(post, getUserById(userId)));
    }

    @Transactional
    public boolean recommend(Long userId, Long postId) {

        if (AlreadyRecommendPost(userId, postId)) {
            throw new IllegalStateException("이미 추천한 게시글 입니다.");
        }

        User userById = getUserById(userId);
        Post postById = getPostById(postId);

        PostRecommend postRecommend = new PostRecommend(postById, userById);
        postRecommendJpaRepository.save(postRecommend);

        return true;
    }

    @Transactional
    public boolean cancelRecommend(Long userId, Long postId) {
        PostRecommend postRecommendByUserIdAndPostId = postRecommendRepository.getPostRecommendByUserIdAndPostId(userId, postId);

        postRecommendByUserIdAndPostId.getPost().deleteRecommend(postRecommendByUserIdAndPostId);
        postRecommendByUserIdAndPostId.getUser().deleteRecommend(postRecommendByUserIdAndPostId);

        postRecommendJpaRepository.delete(postRecommendByUserIdAndPostId);

        return true;
    }

    @Transactional
    public PostResponse getOnePostById(Long userId, Long postId) {
        Post post = getPostById(postId);
        post.upView();
        return PostResponse.of(post, getUserById(userId));
    }

    @Transactional
    public PostResponse updatePost(Long userId, Long postId, UpdatePostRequest updatePostRequest) {
        Post post = getPostById(postId);

        if (!Objects.equals(post.getAuthor().getId(), userId)) {
            throw new UserException.AccessDeniedException();
        }

        post.updatePost(updatePostRequest);

        return PostResponse.of(post,getUserById(userId));
    }

    @Transactional
    public boolean deletePost(Long userId, Long postId) {
        Post post = getPostById(postId);
        if (!Objects.equals(post.getAuthor().getId(), userId)) {
            throw new UserException.AccessDeniedException();
        }

        postJpaRepository.delete(post);

        return true;
    }

    private boolean AlreadyRecommendPost(Long userId, Long postId) {
        Post postById = getPostById(postId);
        return postById.getRecommends().stream().map(PostRecommend::getUser).anyMatch(user -> user.getId().equals(userId));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserException.NotFoundUser::new);
    }

    private Post getPostById(Long postId) {
        return postJpaRepository.findById(postId).orElseThrow(PostException.NotFoundException::new);
    }
}
