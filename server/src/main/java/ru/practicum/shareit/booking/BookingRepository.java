package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByItemOwnerId(Long bookerId, Sort sort);


    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId,
                                                          LocalDateTime now,
                                                          LocalDateTime oneMoreNow, Sort sort);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long bookerId,
                                                             LocalDateTime now,
                                                             LocalDateTime oneMoreNow, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

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

    Optional<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId, Long itemId, BookingStatus status,
                                                                   LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBefore(Long itemId, BookingStatus status,
                                                               LocalDateTime now, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfter(Long itemId, BookingStatus status,
                                                              LocalDateTime now, Sort sort);
}
