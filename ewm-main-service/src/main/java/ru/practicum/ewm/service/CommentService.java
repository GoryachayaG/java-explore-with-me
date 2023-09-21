package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.comments.CommentDto;
import ru.practicum.ewm.dto.comments.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateCommentById(Long userId, Long commentId, NewCommentDto newCommentDto);

    List<CommentDto> getCommentsByAuthor(Long userId, Long eventId,Integer from, Integer size);

    void deleteCommentByIdByAuthor(Long userId, Long commentId);

    List<CommentDto> getAllCommentsByAdmin(Long eventId, Integer from, Integer size);

    void deleteCommentByIdByAdmin(Long commentId);

    List<CommentDto> getAllCommentsOfEventByPublic(Long eventId, Integer from, Integer size);

    CommentDto getCommentByIdPublic(Long commentId);
}