package com.pawland.comment.service;

import com.pawland.comment.dto.request.CreateCommentRequest;
import com.pawland.comment.dto.request.UpdateCommentRequest;
import com.pawland.comment.dto.response.CommentResponse;
import com.pawland.comment.respository.CommentJpaRepository;
import com.pawland.post.domain.Post;
import com.pawland.post.dto.request.PostCreateRequest;
import com.pawland.post.dto.response.PostResponse;
import com.pawland.post.exception.PostException;
import com.pawland.post.repository.PostRepository;
import com.pawland.post.service.PostService;
import com.pawland.user.domain.LoginType;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentJpaRepository commentJpaRepository;

    @BeforeEach
    void setUp() {



    }

    @DisplayName("댓글 작성")
    @Test
    @Transactional
    void createComment() {
        //given
        User user = createUser();
        PostResponse post = createPost(user);

        //when
        CommentResponse comment = commentService.createComment(user.getId(),new CreateCommentRequest(post.getId(),"댓글입니다."));
        Post findPost = postRepository.findById(post.getId()).orElseThrow(PostException.NotFoundException::new);

        //then
        Assertions.assertEquals(user.getNickname(), comment.getAuthor().getNickname());
        Assertions.assertEquals(0,comment.getReplies().size());
        Assertions.assertEquals(1,findPost.getComments().size());
        Assertions.assertEquals("댓글입니다.",findPost.getComments().get(0).getContent());
    }

    @DisplayName("댓글 수정")
    @Test
    @Transactional
    void updateComment() {
        //given
        User user = createUser();
        PostResponse post = createPost(user);
        CommentResponse comment = commentService.createComment(user.getId(), new CreateCommentRequest(post.getId(), "댓글입니다"));


        //when
        CommentResponse commentResponse = commentService.updateComment(user.getId(), comment.getId(), new UpdateCommentRequest("수정되었습니다"));

        //then
        Assertions.assertEquals("수정되었습니다", commentResponse.getContent());
        Assertions.assertEquals(comment.getId(),commentResponse.getId());
        Assertions.assertEquals(comment.getAuthor(),commentResponse.getAuthor());
    }

    @DisplayName("댓글 삭제")
    @Test
    @Transactional
    void deleteComment() {
        //given
        User user = createUser();
        PostResponse post = createPost(user);
        CommentResponse comment = commentService.createComment(user.getId(), new CreateCommentRequest(post.getId(), "댓글입니다"));

        //when
        commentService.deleteComment(user.getId(), comment.getId());

        //then
        Assertions.assertThrows(NoSuchElementException.class, () -> commentJpaRepository.findById(comment.getId()).get());
    }

    @DisplayName("")

    private PostResponse createPost(User user) {
        return postService.uploadPost(user.getId(), new PostCreateRequest("테스트게시글", "테스트게시글입니다.", null, "서울"));
    }

    private User createUser() {
        User tester = User.builder()
                .email("test@test.com")
                .password("123123")
                .nickname("tester")
                .introduce("tester입니다.")
                .type(LoginType.GOOGLE)
                .build();

        userRepository.save(tester);

        return tester;
    }
}
