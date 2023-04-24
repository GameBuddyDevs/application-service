package com.back2261.applicationservice.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "avatars")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Avatars implements Serializable {
    @Id
    private UUID id;

    private String image;
    private Boolean isSpecial;
    private Integer price;
}
