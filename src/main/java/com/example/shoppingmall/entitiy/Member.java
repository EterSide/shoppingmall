package com.example.shoppingmall.entitiy;

import com.example.shoppingmall.entitiy.status.MemberStatus;
import com.example.shoppingmall.entitiy.status.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter  // @Data 제거
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "members")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.ACTIVE;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<Order>();
//
//    @OneToMany(mappedBy = "member")
//    private List<IssuedCoupon> issuedCoupons = new ArrayList<>();

}
