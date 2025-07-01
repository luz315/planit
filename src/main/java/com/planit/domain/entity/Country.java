package com.planit.domain.entity;

import com.planit.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "country")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Country extends BaseEntity {
    @Id
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    public static Country create(String code, String name) {
        return Country.builder()
                .code(code)
                .name(name)
                .build();
    }
}


