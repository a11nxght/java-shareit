package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CommentMapperTest {
    @Test
    void toComment_mapsFieldsProperly() {
        CommentDto dto = new CommentDto();
        dto.setText("Nice item!");

        User author = new User();
        author.setId(2L);
        author.setName("John");

        Item item = new Item();
        item.setId(3L);

        Comment comment = CommentMapper.toComment(dto, author, item);

        assertThat(comment.getText()).isEqualTo("Nice item!");
        assertThat(comment.getAuthor()).isSameAs(author);
        assertThat(comment.getItem()).isSameAs(item);
        assertThat(comment.getCreated()).isNotNull();
    }

    @Test
    void toCommentDto_mapsAllFields() {
        User author = new User();
        author.setName("Mike");
        Item item = new Item();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great!");
        comment.setAuthor(author);
        comment.setItem(item);
        LocalDateTime now = LocalDateTime.now();
        comment.setCreated(now);

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Great!");
        assertThat(dto.getAuthorName()).isEqualTo("Mike");
        assertThat(dto.getCreated()).isEqualTo(now);
    }
}