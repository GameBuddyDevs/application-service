package com.back2261.applicationservice.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "blocked_friends")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlockedFriend {

    @Id
    @Column(name = "blocked_user_id")
    private String blockedId;

    @ManyToOne
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;
}
