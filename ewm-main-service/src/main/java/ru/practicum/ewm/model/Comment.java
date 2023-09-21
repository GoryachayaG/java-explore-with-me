package ru.practicum.ewm.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "comments")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "text", nullable = false)
    String text;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @Column(name = "published_on", nullable = false)
    LocalDateTime publishedOn;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    Event event;
}
