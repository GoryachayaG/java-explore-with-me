package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.StatsDto;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.ewm.dto.events.*;
import ru.practicum.ewm.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.model.enums.*;
import ru.practicum.ewm.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final LocationRepository locationRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final ParticipationRequestRepository requestRepository;

    private final StatsClient client;

    public static final LocalDateTime START = LocalDateTime.of(2000, 1, 1, 0, 0);

    private final LocalDateTime now = LocalDateTime.now();

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        List<EventFullDto> result = getEventFullDtosWithViews(events);
        log.info("Получен список событий, созданных пользователем id = {}", userId);
        return result;
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        Event event = EventMapper.toEvent(newEventDto);
        locationRepository.save(event.getLocation());
        Long catId = newEventDto.getCategory();
        Category category = getCategory(catId);
        event.setCategory(category);
        User initiator = getUser(userId);
        event.setInitiator(initiator);
        event.setCreatedOn(now);
        event.setState(EventState.PENDING);
        Event saved = eventRepository.save(event);
        log.info("Добавлено событие: {}", saved);
        return EventMapper.toEventFullDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByIdPublic(Long userId, Long eventId) {
        getUser(userId);
        Event event = getEvent(eventId);
        log.info("Получили событие id = {} в ответ на запрос пользователя id = {}", eventId, userId);
        return getEventFullDto(eventId, event);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByIdByUser(Long userId,
                                              Long eventId,
                                              UpdateEventDto updateEventDto) {
        Event event = getEvent(eventId);
        validateInitiator(userId, event.getInitiator().getId());
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Пользователь id = " + userId + "не может редактировать " +
                    "опубликованное событие id = " + eventId);
        }
        checkParams(event, updateEventDto);
        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        locationRepository.save(event.getLocation());
        log.info("Обновили событие id = {}, добавленное текущим пользователем id = {}", eventId, userId);
        return getEventFullDto(eventId, event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAllEventsByAdmin(List<Long> users, List<EventState> states,
                                                  List<Long> categories, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, page);
        List<EventFullDto> result = getEventFullDtosWithViews(events);
        log.info("Получили полную информацию обо всех событиях подходящих под переданные условия в ответ на запрос от " +
                "администратора");
        return result;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto) {
        Event event = getEvent(eventId);
        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new ConflictException("Нельзя опубликовать событие id = " + eventId + "поскольку " +
                            "оно уже опубликовано");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(now);
            } else {
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new ConflictException("Событие id = " + eventId + "нельзя отклонить тк оно не в " +
                            "статусе PENDING");
                }
                event.setState(EventState.CANCELED);
            }
        }
        if (event.getPublishedOn() != null && event.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new ConflictException("Можно изменить событие только если дата его начала не ранее чем " +
                    "через час от внесения изменений");
        }
        checkParams(event, updateEventDto);
        locationRepository.save(event.getLocation());
        log.info("Событие id = {} обновлено администратором", eventId);
        return getEventFullDto(eventId, event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, EventSort sort, Integer from,
                                               Integer size, String uri, String ip) {
        if (rangeStart == null) {
            rangeStart = now;
        }
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("RangeStart не может быть позже чем rangeEnd");
        }
        sendStats(uri, ip);
        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                page);
        List<EventShortDto> eventsDto = getEventShortDtos(events, sort);
        log.info("Получили список событий по заданным параметрам в ответ на публичный запрос");
        return eventsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByIdPublic(Long eventId, String uri, String ip) {
        Event event = getEvent(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ObjectNotFoundException("Событие id = " + eventId + " еще не опубликовано");
        }
        sendStats(uri, ip);
        log.info("Получили событие по id = {} в ответ на публичный запрос", eventId);
        return getEventFullDto(eventId, event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId) {
        getUser(userId);
        getEvent(eventId);
        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId);
        log.info("Получен список заявок на участие в событии id = {}", eventId);
        return requests
                .stream()
                .map(ParticipationRequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestsStatusByUserId(Long userId, Long eventId,
                                                                       EventRequestStatusUpdateRequest requestDto) {
        getUser(userId);
        Event event = getEvent(eventId);
        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        int freePlaces = event.getParticipantLimit() - confirmedRequests;
        alreadyFullParticipationLimit(requestDto.getStatus(), freePlaces);
        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndEventInitiatorIdAndIdIn(eventId,
                userId, requestDto.getRequestIds());
        setStatus(requests, requestDto.getStatus(), freePlaces);
        log.info("Обновили статусы заявок на участии в событии id = {} пользователя id = {}", eventId, userId);
        List<ParticipationRequestDto> requestsDto = requests
                .stream()
                .map(ParticipationRequestMapper::toRequestDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> confirmedRequestsDto = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequestsDto = new ArrayList<>();
        for (ParticipationRequestDto dto : requestsDto) {
            if (dto.getStatus().equals(RequestStatus.CONFIRMED)) {
                confirmedRequestsDto.add(dto);
            } else {
                rejectedRequestsDto.add(dto);
            }
        }
        EventRequestStatusUpdateResult response = new EventRequestStatusUpdateResult();
        response.setConfirmedRequests(confirmedRequestsDto);
        response.setRejectedRequests(rejectedRequestsDto);
        log.info("Обновили статусы заявок на участии в событии id = {} пользователя id = {}", eventId, userId);
        return response;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь" +
                        "с id = " + userId + " не найден"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id=" + eventId + " не найдено"));
    }

    private Category getCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Category with id=" + catId + " was not found"));
    }

    private void checkParams(Event event, UpdateEventDto updateEventDto) {
        if (updateEventDto.getAnnotation() != null && !updateEventDto.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            Long catId = updateEventDto.getCategory();
            Category category = getCategory(catId);
            event.setCategory(category);
        }
        if (updateEventDto.getDescription() != null && !updateEventDto.getDescription().isBlank()) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null) {
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(updateEventDto.getLocation()));
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null && !updateEventDto.getTitle().isBlank()) {
            event.setTitle(updateEventDto.getTitle());
        }
    }

    private List<EventFullDto> getEventFullDtosWithViews(List<Event> events) {
        List<EventFullDto> eventsDto = events
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
        List<Long> eventsId = new ArrayList<>();
        for (EventFullDto dto : eventsDto) {
            eventsId.add(dto.getId());
        }
        Map<Long, Integer> requests = getConfirmedRequests(eventsId);
        Map<Long, Long> views = getViews(eventsId);
        eventsDto.forEach(eventFullDto -> {
            eventFullDto.setConfirmedRequests(requests.getOrDefault(eventFullDto.getId(), 0));
            eventFullDto.setViews(views.getOrDefault(eventFullDto.getId(), 0L));
        });
        return eventsDto;
    }

    private List<EventShortDto> getEventShortDtos(List<Event> events, EventSort sort) {
        List<EventShortDto> eventsDto = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        List<Long> eventsId = new ArrayList<>();
        for (EventShortDto dto : eventsDto) {
            eventsId.add(dto.getId());
        }
        Map<Long, Long> views = getViews(eventsId);
        Map<Long, Integer> requests = getConfirmedRequests(eventsId);
        eventsDto.forEach(eventShortDto -> {
            eventShortDto.setConfirmedRequests(requests.getOrDefault(eventShortDto.getId(), 0));
            eventShortDto.setViews(views.getOrDefault(eventShortDto.getId(), 0L));
        });
        if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            eventsDto.sort(Comparator.comparing(EventShortDto::getEventDate));
        }
        if (sort != null && sort.equals(EventSort.VIEWS)) {
            eventsDto.sort(Comparator.comparing(EventShortDto::getViews));
        }
        return eventsDto;
    }

    private Map<Long, Integer> getConfirmedRequests(List<Long> eventsId) {
        List<ParticipationRequest> confirmedRequests = requestRepository.findAllByStatusAndEventIdIn(
                RequestStatus.CONFIRMED, eventsId);
        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
    }

    private EventFullDto getEventFullDto(Long eventId, Event event) {
        EventFullDto eventDto = EventMapper.toEventFullDto(event);
        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        eventDto.setConfirmedRequests(confirmedRequests);
        Map<Long, Long> views = getViews(List.of(eventDto.getId()));
        eventDto.setViews(views.getOrDefault(eventDto.getId(), 0L));
        return eventDto;
    }

    private void validateInitiator(Long userId, Long initiatorId) {
        if (!initiatorId.equals(userId)) {
            throw new ConflictException("Пользователь id = " + userId + "не является инициатором события");
        }
    }

    private void sendStats(String uri, String ip) {
        HitDto hit = HitDto.builder()
                .app("ewm-main-service")
                .uri(uri)
                .ip(ip)
                .timestamp(now)
                .build();
        client.createHit(hit);
        log.info("Информация о просмотре событий передана на сервис статистики");
    }

    private Map<Long, Long> getViews(List<Long> eventsId) {
        List<String> uris = new ArrayList<>();
        for (Long eventId : eventsId) {
            String uri = "/events/" + eventId;
            uris.add(uri);
        }
        List<StatsDto> result = client.getStats(START, LocalDateTime.now(), uris, true);
        log.info("Отправлен запрос на получение статистики просмиотров");
        Map<Long, Long> views = new HashMap<>();
        for (StatsDto dto : result) {
            String uri = dto.getUri();
            String[] split = uri.split("/");
            String id = split[2];
            Long eventId = Long.parseLong(id);
            views.put(eventId, dto.getHits());
        }
        return views;
    }

    private void alreadyFullParticipationLimit(RequestStatus status, int freePlaces) {
        if (status.equals(RequestStatus.CONFIRMED) && freePlaces <= 0) {
            throw new ConflictException("Лимит запросов на участие в событии достигнут");
        }
    }

    private void setStatus(List<ParticipationRequest> requests, RequestStatus status, int freePlaces) {
        if (status.equals(RequestStatus.CONFIRMED)) {
            for (ParticipationRequest request : requests) {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new ConflictException("Request's status has to be PENDING");
                }
                if (freePlaces > 0) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    freePlaces--;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                }
            }
        } else if (status.equals(RequestStatus.REJECTED)) {
            for (ParticipationRequest request : requests) {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new ConflictException("Request's status has to be PENDING");
                }
                request.setStatus(RequestStatus.REJECTED);
            }
        } else {
            throw new ConflictException("Нужно либо одобрить - CONFIRMED, либо отклонить - REJECTED заявку");
        }
    }
}