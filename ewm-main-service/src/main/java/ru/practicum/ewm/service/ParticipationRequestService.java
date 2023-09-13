package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    ParticipationRequestDto cancelParticipationRequestStatus(Long userId, Long requestId);
}