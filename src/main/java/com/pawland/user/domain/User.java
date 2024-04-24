package com.pawland.user.domain;

import com.pawland.global.domain.BaseTimeEntity;
import com.pawland.order.domain.Order;
import com.pawland.post.domain.Post;
import com.pawland.product.domain.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.pawland.user.domain.LoginType.NORMAL;

@Entity(name = "users")
@Getter
@Table
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

    private String introduce = "";

    @Enumerated(EnumType.STRING)
    private LoginType type = NORMAL;

    private String profileImage = "";

    @OneToMany(mappedBy = "buyer")
    private List<Order> orderList = new ArrayList<>();    // 주문 내역

    @OneToMany(mappedBy = "seller")
    private List<Product> productList = new ArrayList<>();    // 등록한 상품

    @OneToMany(mappedBy = "author")
    private List<Post> postList = new ArrayList<>();    // 내가 쓴 글

    @Builder
    public User(String email, String password, String introduce, LoginType type, String nickname, String profileImage) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.type = type == null ? this.type : type;
        this.profileImage = isBlank(profileImage) ? this.profileImage : profileImage;
        this.introduce = isBlank(introduce) ? this.introduce : introduce;
    }

    public void update(User user) {
        this.nickname = user.getNickname();
        this.profileImage = isBlank(user.getProfileImage()) ? profileImage : user.getProfileImage();
        this.introduce = isBlank(user.getIntroduce()) ? introduce : user.getIntroduce();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
