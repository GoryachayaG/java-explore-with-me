package ru.practicum.ewm.controller.adminController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comments.CommentDto;
import ru.practicum.ewm.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequestMapping("/admin/comments")
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCommentsController {

    private final CommentService service;

    @GetMapping
    public List<CommentDto> getAllCommentsByAdmin(@RequestParam(required = false) Long eventId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение списка комментариев от администратора");
        return service.getAllCommentsByAdmin(eventId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByIdByAdmin(@PathVariable Long commentId) {
        log.info("Запрос на удаление комментария с id = {} от администратора", commentId);
        service.deleteCommentByIdByAdmin(commentId);
    }
}