package ru.practicum.stats.server.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.stats.dto.Constants.FOR_FORMATTER;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FOR_FORMATTER);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectlyMadeRequestException(final BadRequestException e) {
        log.info(e.getMessage());
        return new ApiError("BAD_REQUEST", "Incorrectly made request.", e.getMessage(),
                LocalDateTime.now().format(formatter));
    }
}
