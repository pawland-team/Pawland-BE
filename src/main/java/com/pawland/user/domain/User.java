package com.pawland.user.domain;

import com.pawland.global.domain.BaseTimeEntity;
import com.pawland.order.domain.Order;
import com.pawland.review.domain.OrderReview;
import com.pawland.post.domain.Post;
import com.pawland.post.domain.PostRecommend;
import com.pawland.product.domain.Product;
import com.pawland.product.domain.WishProduct;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pawland.user.domain.LoginType.NORMAL;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotNull
    private String introduce = "";

    private Double star;

    private int reviewCount;

    @Enumerated(EnumType.STRING)
    private LoginType type = NORMAL;

    @NotNull
    private String profileImage = "";

    @OneToMany(mappedBy = "buyer", orphanRemoval = true)
    private List<Order> orderList = new ArrayList<>();    // 주문 내역

    @OneToMany(mappedBy = "seller", orphanRemoval = true)
    private List<Product> productList = new ArrayList<>();    // 등록한 상품

    @OneToMany(mappedBy = "author", orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();    // 내가 쓴 글

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<PostRecommend> recommendPosts = new HashSet<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<WishProduct> wishProductSet = new HashSet<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<OrderReview> orderReviewSet = new HashSet<>();

    @Builder
    public User(Long id, String email, String password, String introduce, LoginType type, String nickname, String profileImage) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.type = type == null ? this.type : type;
        this.profileImage = isBlank(profileImage) ? this.profileImage : profileImage;
        this.introduce = isBlank(introduce) ? this.introduce : introduce;
        this.star = (double) 0;
    }

    public void updateProfile(User user) {
        this.nickname = user.getNickname();
        this.profileImage = isBlank(user.getProfileImage()) ? profileImage : user.getProfileImage();
        this.introduce = isBlank(user.getIntroduce()) ? introduce : user.getIntroduce();
    }

    public User updateOauth2Profile(User user) {
        this.profileImage = user.profileImage;
        return this;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public void addRecommend(PostRecommend postRecommend) {
        this.recommendPosts.add(postRecommend);
    }

    public void deleteRecommend(PostRecommend postRecommend) {
        this.recommendPosts.remove(postRecommend);
    }

    public void addWishProduct(WishProduct wishProduct) {
        this.wishProductSet.add(wishProduct);
        wishProduct.setUser(this);
    }

    public void deleteWishProduct(WishProduct wishProduct) {
        this.wishProductSet.remove(wishProduct);
        wishProduct.setUser(null);
    }

    public void addOrderReview(OrderReview orderReview) {
        orderReview.setUser(this);
        this.orderReviewSet.add(orderReview);
    }

    public void setStar(Double star) {
        this.star = star;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
