package ru.practicum.stats.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id записи

    @Column(name = "app", nullable = false)
    private String app; // id сервиса для которого записывается информация

    @Column(name = "uri", nullable = false)
    private String uri; // URI для которого был создан запрос

    @Column(name = "ip", nullable = false)
    private String ip; // IP-адрес пользователя, который создал запрос

    @Column(name = "created", nullable = false)
    private LocalDateTime timestamp; //  Дата и время, когда был создан запрос к эндпоинту
}