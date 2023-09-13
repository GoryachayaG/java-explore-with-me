package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.practicum.stats.dto.Constants.FOR_FORMATTER;

@Service
public class StatsClient {

    @Value("${STATS_SERVER_URL}")
    private String serverUrl;

    private final RestTemplate restTemplate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FOR_FORMATTER);

    @Autowired
    public StatsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createHit(HitDto hitDto) {
        restTemplate.postForLocation(serverUrl.concat("/hit"), hitDto);
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "unique", unique));
        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", String.join(",", uris));
        }
        StatsDto[] response = restTemplate.getForObject(
                serverUrl.concat("/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                StatsDto[].class, parameters);
        return Objects.isNull(response) ? List.of() : List.of(response);
    }
}