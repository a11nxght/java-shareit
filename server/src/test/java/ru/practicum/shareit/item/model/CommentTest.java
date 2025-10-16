package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CommentTest {
    @Test
    void gettersSetters_work() {
        Comment c = new Comment();
        c.setId(10L);
        c.setText("text");
        Item item = new Item();
        item.setId(5L);
        c.setItem(item);
        User author = new User();
        author.setId(7L);
        c.setAuthor(author);
        LocalDateTime now = LocalDateTime.now();
        c.setCreated(now);

        assertThat(c.getId()).isEqualTo(10L);
        assertThat(c.getText()).isEqualTo("text");
        assertThat(c.getItem()).isSameAs(item);
        assertThat(c.getAuthor()).isSameAs(author);
        assertThat(c.getCreated()).isEqualTo(now);
    }


    @Test
    void equals_nullOrDifferentClass_false() {
        Comment c = new Comment();
        assertThat(c == null).isFalse();
    }

    @Test
    void equals_bothIdsNull_true_currentImplementation() {
        // ВАЖНО: при текущей реализации два transient-комментария считаются равными
        Comment a = new Comment();
        Comment b = new Comment();
        assertThat(a.equals(b)).isTrue();
        // и hashCode тоже совпадает
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_sameId_true_andHashCodesEqual() {
        Comment a = new Comment();
        a.setId(1L);
        Comment b = new Comment();
        b.setId(1L);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_differentIds_false() {
        Comment a = new Comment();
        a.setId(1L);
        Comment b = new Comment();
        b.setId(2L);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toString_doesNotTouchLazyFields_andContainsIdAndText() {
        Comment c = new Comment();
        c.setId(42L);
        c.setText("hello");
        Item item = new Item();
        item.setId(5L);
        User author = new User();
        author.setId(7L);
        c.setItem(item);
        c.setAuthor(author);

        String s = c.toString();
        // @ToString помечен @ToString.Exclude для item/author — они не должны участвовать
        assertThat(s).contains("id=42");
        assertThat(s).contains("text=hello");
        assertThat(s).doesNotContain("item=");
        assertThat(s).doesNotContain("author=");
    }
}