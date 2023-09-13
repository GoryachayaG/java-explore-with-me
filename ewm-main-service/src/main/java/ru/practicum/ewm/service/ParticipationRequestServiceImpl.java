package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository repository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    @Transactional
    @Override
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        ParticipationRequest request = new ParticipationRequest();
        User requester = getUser(userId);
        request.setRequester(requester);
        Event event = getEvent(eventId);
        request.setEvent(event);
        validateInitiatorOfEvent(event.getInitiator().getId(), userId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Для подачи заявок на участие статус события должен быть PUBLISHED");
        }
        Integer confirmedRequests = repository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() <= confirmedRequests && event.getParticipantLimit() != 0) {
            throw new ConflictException(("Лимит запросов на участие превышен"));
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        request.setCreated(LocalDateTime.now());
        ParticipationRequest savedRequest = repository.save(request);
        log.info("Создали запрос пользователя id = {} на участие в событии id = {}", userId, eventId);
        return ParticipationRequestMapper.toRequestDto(savedRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        getUser(userId);
        List<ParticipationRequest> requests = repository.findByRequesterId(userId);
        log.info("Получили список заявок на участие пользователя id = {} в чужих событиях", userId);
        return requests
                .stream()
                .map(ParticipationRequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelParticipationRequestStatus(Long userId, Long requestId) {
        getUser(userId);
        ParticipationRequest request = getRequest(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new ObjectNotFoundException("Пользователь с id = " + userId + "не подавал заявку на участие с " +
                    "номером " + requestId);
        }
        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest canceledRequest = repository.save(request);
        log.info("Пользователь id = {} отменил заявку id = {}", userId, requestId);
        return ParticipationRequestMapper.toRequestDto(canceledRequest);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь" +
                        "с id = " + userId + " не найден"));
    }

    private ParticipationRequest getRequest(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Request with id = " + requestId + " was not found"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие с id=" + eventId + " не найдено"));
    }

    private void validateInitiatorOfEvent(Long userId, Long initiatorId) {
        if (initiatorId.equals(userId)) {
            throw new ConflictException("Инициатор не может создать запрос на участие в собственном " +
                    "событии");
        }
    }
}