package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long bookerId);


    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                          LocalDateTime now,
                                                                          LocalDateTime oneMoreNow);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                             LocalDateTime now,
                                                                             LocalDateTime oneMoreNow);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("""
                select b from Booking b
                where b.item.id in :itemIds
                  and b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
                  and b.start <= :now
                order by b.start desc
            """)
    List<Booking> findPastForItems(@Param("itemIds") List<Long> itemIds,
                                   @Param("now") LocalDateTime now);

    @Query("""
                select b from Booking b
                where b.item.id in :itemIds
                  and b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
                  and b.start > :now
            """)
    List<Booking> findFutureForItems(@Param("itemIds") List<Long> itemIds,
                                     @Param("now") LocalDateTime now);

    Optional<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId, Long itemId, BookingStatus status
            , LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Long itemId, BookingStatus status, LocalDateTime now);
    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStart(Long itemId, BookingStatus status, LocalDateTime now);
}
