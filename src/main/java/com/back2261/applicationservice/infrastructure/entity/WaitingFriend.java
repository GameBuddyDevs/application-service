package com.back2261.applicationservice.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "waiting_friends")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WaitingFriend {

    @Id
    @Column(name = "requested_id")
    private String waitingFriendId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Gamer gamer;
}
