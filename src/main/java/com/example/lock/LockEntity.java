package com.example.lock;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int count;
    private String test;

    @Version
    private Long version;

    public LockEntity(int count, String test) {
        this.count = count;
        this.test = test;
    }

    public void updateCount(int incrementValue) {
        this.count += incrementValue;
    }
}
