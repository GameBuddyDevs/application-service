package com.back2261.applicationservice.infrastructure.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "community", schema = "schcomm")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Community implements Serializable {
    @Id
    private UUID communityId;

    private String name;
    private String description;
    private String communityAvatar;
    private String wallpaper;

    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "owner")
    private Gamer owner;
}
