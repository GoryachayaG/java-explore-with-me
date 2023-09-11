package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.dto.Constants.FOR_FORMATTER;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    //Сохранение инфо о том, что на uri конкретного сервиса был отправлен запрос пользователем
    public void createHit(@Valid @RequestBody HitDto hitDto) {
        log.info("Запрос на сохранение статистики");
        service.createHit(hitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatsDto> getStats(@RequestParam @DateTimeFormat(pattern = FOR_FORMATTER) LocalDateTime start,
                                   @RequestParam @DateTimeFormat(pattern = FOR_FORMATTER) LocalDateTime end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос на получение статистики");
        return service.getStats(start, end, uris, unique);
    }
}
