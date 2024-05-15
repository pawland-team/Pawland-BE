package com.pawland.comment.service;

import com.pawland.comment.domain.Comment;
import com.pawland.comment.domain.RecommendComment;
import com.pawland.comment.dto.request.CreateCommentRequest;
import com.pawland.comment.dto.request.UpdateCommentRequest;
import com.pawland.comment.dto.response.CommentResponse;
import com.pawland.comment.exception.CommentException;
import com.pawland.comment.respository.CommentJpaRepository;
import com.pawland.post.domain.Post;
import com.pawland.post.exception.PostException;
import com.pawland.post.repository.PostJpaRepository;
import com.pawland.user.domain.User;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentJpaRepository commentJpaRepository;
    private final UserRepository userRepository;
    private final PostJpaRepository postJpaRepository;

    @Transactional
    public CommentResponse createComment(Long userId, Long postId, CreateCommentRequest createCommentRequest) {
        User author = getUserById(userId);
        Post post = getPostById(postId);

        Comment comment = Comment.builder()
                .author(author)
                .post(post)
                .content(createCommentRequest.getContent())
                .build();

        commentJpaRepository.save(comment);

        post.addComment(comment);

        return CommentResponse.of(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        User userById = getUserById(userId);
        Comment commentById = getCommentById(commentId);

        if (!userById.getId().equals(commentById.getAuthor().getId())) {
            throw new CommentException.AccessDeniedException();
        }

        commentById.update(updateCommentRequest);

        return CommentResponse.of(commentById);
    }

    @Transactional
    public boolean deleteComment(Long userId, Long commentId) {
        User userById = getUserById(userId);
        Comment commentById = getCommentById(commentId);

        if (!userById.getId().equals(commentById.getAuthor().getId())) {
            throw new CommentException.AccessDeniedException();
        }

        commentById.getPost().getComments().remove(commentById);
        commentJpaRepository.delete(commentById);

        return true;
    }

    @Transactional
    public CommentResponse recommendComment(Long userId, Long commentId) {
        User userById = getUserById(userId);
        Comment commentById = getCommentById(commentId);

        commentById.recommendComment(new RecommendComment(commentById, userById));

        return CommentResponse.of(commentById);
    }

    @Transactional
    public CommentResponse createCommentComment(Long userId, Long commentId, CreateCommentRequest createCommentRequest) {
        User userById = getUserById(userId);
        Comment comment = new Comment(null, userById, createCommentRequest.getContent());
        Comment commentById = getCommentById(commentId);

        commentJpaRepository.save(comment);

        commentById.addReply(comment);

        return CommentResponse.of(comment);
    }

    @Transactional
    public boolean removeCommentComment(Long userId, Long commentId) {
        User userById = getUserById(userId);
        Comment commentById = getCommentById(commentId);
        if (!userById.getId().equals(commentById.getAuthor().getId())) {
            throw new CommentException.AccessDeniedException();
        }

        commentJpaRepository.delete(commentById);

        return true;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserException.NotFoundUser::new);
    }

    private Post getPostById(Long postId) {
        return postJpaRepository.findById(postId).orElseThrow(PostException.NotFoundException::new);
    }

    private Comment getCommentById(Long commentId) {
        return commentJpaRepository.findById(commentId).orElseThrow(CommentException.NotFoundComment::new);
    }


}
