package ru.practicum.stats.server.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id; // id записи

    @Column(name = "app", nullable = false)
    String app; // id сервиса для которого записывается информация

    @Column(name = "uri", nullable = false)
    String uri; // URI для которого был создан запрос

    @Column(name = "ip", nullable = false)
    String ip; // IP-адрес пользователя, который создал запрос

    @Column(name = "created", nullable = false)
    LocalDateTime timestamp; //  Дата и время, когда был создан запрос к эндпоинту
}