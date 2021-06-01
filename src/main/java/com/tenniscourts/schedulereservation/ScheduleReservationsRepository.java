package com.tenniscourts.schedulereservation;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface ScheduleReservationsRepository extends JpaRepository<SR, Long> {

    @Transactional
    Integer deleteByScheduleIdAndReservationId(Long scheduleId, Long reservationId);
}