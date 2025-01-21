package com.example.shoppingmall.entitiy;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // 공통 매핑 정보가 필요할 때 사용
@EntityListeners(AuditingEntityListener.class)
// 엔티티가 삽입, 삭제 수정, 조회할 때 전/후로 작업 이벤트 처리를 해준다
@Getter
public abstract class BaseTimeEntity {

    @CreatedDate// 엔티티가 생성 될 때 자동으로 현재 날짜 시간 저장
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 수정될 때 자동으로 현재 날짜 시간 저장
    private LocalDateTime updatedAt;

}
