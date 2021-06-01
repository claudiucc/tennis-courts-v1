package com.tenniscourts.schedules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByTennisCourt_IdOrderByStartDateTime(Long id);
    List<Schedule> findByStartDateTimeAfterAndEndDateTimeBefore(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT s.* FROM SCHEDULE s WHERE s.ID NOT IN (SELECT sr.SCHEDULE_ID FROM SCHEDULE_RESERVATIONS sr)", nativeQuery = true)
    List<Schedule> findAllFreeSchedules();
}