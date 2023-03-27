package com.back2261.applicationservice.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "friends")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    @Id
    @Column(name = "friend_id")
    private String friendId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Gamer gamer;
}
