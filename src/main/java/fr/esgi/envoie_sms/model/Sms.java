package fr.esgi.envoie_sms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Data
@Table(name = "sms_history")
public class Sms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(length = 1000)
    private String apiResponse;

    @Column(nullable = false)
    private boolean success;

    @PrePersist
    public void prePersist() {
        sentAt = LocalDateTime.now();
    }
}