package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.stats.dto.StatsDto(h.app, h.uri, count(h.ip)) " +
            "from Hit as h where h.timestamp between ?1 and ?2 " +
            "and h.uri in ?3 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<StatsDto> getAllByTimestampAndUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.stats.dto.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit as h where h.timestamp between ?1 and ?2 " +
            "and h.uri in ?3 " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<StatsDto> getAllByTimestampAndUriUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.stats.dto.StatsDto(h.app, h.uri, count(h.ip)) " +
            "from Hit as h where h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<StatsDto> getAllByTimestamp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stats.dto.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit as h where h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<StatsDto> getAllByTimestampUnique(LocalDateTime start, LocalDateTime end);
}
