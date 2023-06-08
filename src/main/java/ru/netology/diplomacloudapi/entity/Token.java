package ru.netology.diplomacloudapi.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.netology.diplomacloudapi.entity.enums.TokenType;

@Entity
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@Builder
@Table(name = "tokens", schema = "cloud")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String token;
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type")
    private TokenType tokenType;
    private boolean expired;
    private boolean revoked;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
