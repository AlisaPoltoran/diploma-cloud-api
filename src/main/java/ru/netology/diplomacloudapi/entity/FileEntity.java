package ru.netology.diplomacloudapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Builder
@Table(name = "files", schema = "cloud")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "creation_date_time")
    private LocalDateTime date;

    @Column(nullable = false, name = "size_in_bytes")
    private long sizeInBytes;

    @JoinColumn()
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    @Column(name = "content_type")
    private String contentType;

}
