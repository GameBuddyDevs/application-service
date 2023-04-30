package com.back2261.applicationservice.infrastructure.entity;

import jakarta.persistence.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "gamer", schema = "schauth")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Gamer implements UserDetails {
    @Id
    private String userId;

    @Column(name = "username", unique = true)
    private String gamerUsername;

    @Column(unique = true, nullable = false)
    private String email;

    private Integer age;
    private String country;
    private UUID avatar;

    @UpdateTimestamp
    private Date lastModifiedDate;

    private String pwd;
    private String gender;
    private Boolean isBlocked = false;
    private String fcmToken;
    private Integer coin;

    @ManyToMany
    @JoinTable(
            name = "gamer_keywords_join",
            schema = "schauth",
            joinColumns = @JoinColumn(name = "gamer_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    private Set<Keywords> keywords;

    @ManyToMany
    @JoinTable(
            name = "gamer_games_join",
            schema = "schauth",
            joinColumns = @JoinColumn(name = "gamer_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id"))
    private Set<Games> likedgames;

    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private Set<Gamer> friends;

    @ManyToMany
    @JoinTable(
            name = "waiting_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "requested_id"))
    private Set<Gamer> waitingFriends;

    @ManyToMany
    @JoinTable(
            name = "blocked_friends",
            joinColumns = @JoinColumn(name = "gamer_id"),
            inverseJoinColumns = @JoinColumn(name = "blocked_user_id"))
    private Set<Gamer> blockedFriends;

    @ManyToMany
    @JoinTable(
            name = "gamer_earned_achievements",
            joinColumns = @JoinColumn(name = "gamer_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id"))
    private Set<Achievements> gamerEarnedAchievements;

    @ManyToMany
    @JoinTable(
            name = "gamer_collected_achievements",
            joinColumns = @JoinColumn(name = "gamer_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id"))
    private Set<Achievements> gamerCollectedAchievements;

    @ManyToMany
    @JoinTable(
            name = "bought_avatars",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "avatar_id"))
    private Set<Avatars> boughtAvatars;

    @ManyToMany
    @JoinTable(
            name = "community_members_join",
            schema = "schcomm",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "community_id"))
    private Set<Community> joinedCommunities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
