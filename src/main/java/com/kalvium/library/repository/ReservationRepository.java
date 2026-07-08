package com.kalvium.library.repository;

import com.kalvium.library.entity.Reservation;
import com.kalvium.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    int countByBorrowerIdAndStatusIn(String borrowerId, List<Reservation.ReservationStatus> statuses);
    List<Reservation> findByBookAndStatusOrderByReservationDateAsc(Book book, Reservation.ReservationStatus status);
    boolean existsByBookAndBorrowerIdAndStatusIn(Book book, String borrowerId, List<Reservation.ReservationStatus> statuses);
}
