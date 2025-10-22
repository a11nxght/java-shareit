package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId, Sort sort);

    @Query("select c from Comment c where c.item.id in :itemIds order by c.created desc")
    List<Comment> findAllByItemIds(List<Long> itemIds, Sort sort);
}
