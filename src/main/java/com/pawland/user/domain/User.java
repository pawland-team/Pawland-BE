package com.pawland.user.domain;

import com.pawland.order.domain.Order;
import com.pawland.post.domain.FavoritePost;
import com.pawland.post.domain.Post;
import com.pawland.product.domain.FavoriteItem;
import com.pawland.product.domain.Product;
import com.pawland.review.domain.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.pawland.user.domain.UserType.NORMAL;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 이메일
    private String email;
    // 비밀번호
    private String password;
    // 이름(닉네임)
    private String name;
    // 소개
    private String introduce;
    // 회원 유형(구글, 카카오, 일반)
    @Enumerated(EnumType.STRING)
    private UserType type = NORMAL;
    // 전화번호
    private String phoneNumber;
    // 이메일 인증 여부
    private boolean emailVerified = false;
    // 이미지
    private String image;
    // 남긴 리뷰
    @OneToMany(mappedBy = "user")
    private List<Review> reviewList = new ArrayList<>();
    // 주문 내역
    @OneToMany(mappedBy = "user")
    private List<Order> orderList = new ArrayList<>();
    // 등록한 상품
    @OneToMany(mappedBy = "user")
    private List<Product> productList = new ArrayList<>();
    // 찜한 상품
    @OneToMany(mappedBy = "user")
    private List<FavoriteItem> favoriteItemList = new ArrayList<>();
    // 내가 쓴 글
    @OneToMany(mappedBy = "user")
    private List<Post> postList = new ArrayList<>();
    // 추천 누른 글
    @OneToMany(mappedBy = "user")
    private List<FavoritePost> favoritePostList = new ArrayList<>();

    public User(String email, String password, String name, String introduce, UserType type, String phoneNumber, boolean emailVerified, String image) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.introduce = introduce;
        this.type = type;
        this.phoneNumber = phoneNumber;
        this.emailVerified = emailVerified;
        this.image = image;
    }
}
