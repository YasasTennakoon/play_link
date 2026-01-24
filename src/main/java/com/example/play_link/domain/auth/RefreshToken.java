package com.example.play_link.domain.auth;

import com.example.play_link.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Document(collection = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @MongoId
    private String id;
    
    @Indexed(unique = true)
    private String token;
    
    @DBRef
    private User user;
    
    private Instant expiryDate;
    
    @CreatedDate
    private Instant createdAt;
}
