package ru.practicum.ewm.controller.privateController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.events.EventFullDto;
import ru.practicum.ewm.dto.events.NewEventDto;
import ru.practicum.ewm.dto.events.UpdateEventDto;
import ru.practicum.ewm.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/events")
public class PrivateEventsController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsByUserId(@PathVariable Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение списка событий пользовтеля id = {}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto eventDto) {
        log.info("Запрос на создание события пользователем id = {}", userId);
        return eventService.createEvent(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос на получение полной информации о событии по id = {} добавленном текущим пользователем id = {}",
                eventId, userId);
        return eventService.getEventByIdPublic(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByIdByUser(@PathVariable Long userId, @PathVariable Long eventId,
                                              @Valid @RequestBody UpdateEventDto eventDto) {
        log.info("Запрос на изменение события id = {}, добавленного текущим пользователем id = {}", eventId, userId);
        return eventService.updateEventByIdByUser(userId, eventId, eventDto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(@PathVariable Long userId,
                                                                       @PathVariable Long eventId) {
        log.info("Запрос на получение списка заявок на участие в событии id = {} пользователя id = {}", eventId, userId);
        return eventService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestsStatusByUserId(@PathVariable Long userId,
                                                                       @PathVariable Long eventId,
                                                                       @Valid @RequestBody
                                                                       EventRequestStatusUpdateRequest request) {
        log.info("Запрос на обновление статусов заявок на участии в событии id = {} пользователя id = {}", eventId,
                userId);
        return eventService.updateRequestsStatusByUserId(userId, eventId, request);
    }
}