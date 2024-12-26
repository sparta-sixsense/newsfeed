package com.sixsense.newsfeed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // @Getter, @Setter, @ToString, @EqualsAndHashCode 포함
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 포함하는 생성자 추가
@Builder // 빌더 패턴 지원
public class ProfileUpdateRequestDto {
    private String name;
    private Integer age;
    private String address;
    private String password;
}
