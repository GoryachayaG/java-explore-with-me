package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Integer countAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventIdAndEventInitiatorId(Long eventId, Long userId);

    List<ParticipationRequest> findAllByEventIdAndEventInitiatorIdAndIdIn(Long eventId, Long userId,
                                                                          List<Long> requestsId);

    List<ParticipationRequest> findAllByStatusAndEventIdIn(RequestStatus status, List<Long> eventIds);
}