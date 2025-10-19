package com.knugpt.knugpt.domain.chat.entity;

import com.knugpt.knugpt.domain.user.entity.User;
import com.knugpt.knugpt.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 20, nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn
    private User user;


    @Builder
    public ChatRoom(String title, User user) {
        this.title = title;
        this.user = user;
    }


    public void updateTitle(String title) {
        this.title = title;
    }
}
