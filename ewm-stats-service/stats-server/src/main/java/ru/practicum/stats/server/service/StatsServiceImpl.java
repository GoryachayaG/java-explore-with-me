package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.server.mapper.HitMapper;
import ru.practicum.stats.server.model.Hit;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    @Transactional
    public void createHit(HitDto hitDto) {
        Hit hitForSave = HitMapper.toHit(hitDto);
        repository.save(hitForSave);
        log.info("Сохранили инфо");
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            if (uris != null) {
                log.info("Получили статистику по заданным uri и ip");
                return repository.getAllByTimestampAndUriUnique(start,end, uris);
            } else {
                log.info("Получили статистику по ip");
                return repository.getAllByTimestampUnique(start, end);
            }
        } else {
            if (uris != null) {
                log.info("Получили общую статистику по заданным uri");
                return repository.getAllByTimestampAndUri(start, end, uris);
            } else {
                log.info("Получили общую статистику");
                return repository.getAllByTimestamp(start, end);
            }
        }
    }
}