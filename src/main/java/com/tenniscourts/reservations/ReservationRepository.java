package com.tenniscourts.reservations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findBySchedule_Id(Long scheduleId);

    List<Reservation> findByReservationStatusAndSchedule_StartDateTimeGreaterThanEqualAndSchedule_EndDateTimeLessThanEqual(ReservationStatus reservationStatus, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query(value = "SELECT R.*, S.START_DATE_TIME, S.END_DATE_TIME FROM RESERVATION R " +
                   "LEFT JOIN SCHEDULE S ON S.ID = R.SCHEDULE_ID " +
                   "WHERE S.START_DATE_TIME < CURRENT_TIMESTAMP() ", nativeQuery = true)
    List<Reservation> getPastReservations();

//    List<Reservation> findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndTennisCourt(LocalDateTime startDateTime, LocalDateTime endDateTime, TennisCourt tennisCourt);
}
