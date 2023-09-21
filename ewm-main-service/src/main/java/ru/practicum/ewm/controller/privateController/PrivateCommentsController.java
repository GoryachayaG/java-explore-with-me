package ru.practicum.ewm.controller.privateController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comments.CommentDto;
import ru.practicum.ewm.dto.comments.NewCommentDto;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequestMapping("/users/{userId}/comments")
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateCommentsController {

    private final CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId, @RequestParam Long eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Запрос на создание комментария от пользователя с id = {} на событие с id = {}", userId, eventId);
        return service.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentById(@PathVariable Long userId, @PathVariable Long commentId,
                                        @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Запрос на обновление комментария с id = {} от пользователя с id = {}", commentId, userId);
        return service.updateCommentById(userId, commentId, newCommentDto);
    }

    @GetMapping
    public List<CommentDto> getCommentsByAuthor(@PathVariable Long userId,
                                                @RequestParam(required = false) Long eventId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение комментариев пользователя с id = {}", userId);
        return service.getCommentsByAuthor(userId, eventId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByIdByAuthor(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Запрос на удаление комментария с id = {} автором с id = {}", commentId, userId);
        service.deleteCommentByIdByAuthor(userId, commentId);
    }
}
