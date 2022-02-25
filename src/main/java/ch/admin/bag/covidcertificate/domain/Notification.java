package ch.admin.bag.covidcertificate.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    String messages;

    @Column(insertable = false)
    LocalDateTime creationDateTime;

    public Notification(String messages) {
        this.messages = messages;
    }
}
