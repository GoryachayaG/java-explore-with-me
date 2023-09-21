package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.comments.CommentDto;
import ru.practicum.ewm.dto.comments.NewCommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .publishedOn(comment.getPublishedOn())
                .eventId(comment.getEvent().getId())
                .build();
    }

    public static Comment toComment(NewCommentDto dto, User author, Event event) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setPublishedOn(LocalDateTime.now());
        return comment;
    }
}
