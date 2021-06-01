package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.GuestMapper;
import com.tenniscourts.guests.GuestService;
import com.tenniscourts.schedulereservation.SR;
import com.tenniscourts.schedulereservation.ScheduleReservationsRepository;
import com.tenniscourts.schedules.ScheduleMapper;
import com.tenniscourts.schedules.ScheduleService;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class ReservationService {
    
    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    private final GuestService guestService;

    private final GuestMapper guestMapper;

    private final ScheduleService scheduleService;

    private final ScheduleMapper scheduleMapper;

    private final ScheduleReservationsRepository scheduleReservationsRepository;

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
        Reservation reservation = new Reservation();
        reservation.setGuest(guestMapper.map(guestService.findGuestById(createReservationRequestDTO.getGuestId())));
        reservation.setSchedule(scheduleMapper.map(scheduleService.findSchedule(createReservationRequestDTO.getScheduleId())));
        reservation.setValue(createReservationRequestDTO.getValue());
        ReservationDTO reservationDTO = reservationMapper.map(reservationRepository.save(reservation));
        SR scheduleReservations = new SR(reservation.getSchedule().getId(), reservationDTO.getId());
        scheduleReservationsRepository.save(scheduleReservations);
        return reservationDTO;
    }

    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    private Reservation cancel(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }


    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);
        scheduleReservationsRepository.deleteByScheduleIdAndReservationId(reservation.getSchedule().getId(), reservation.getId());
        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (hours >= 24) {
            return reservation.getValue();
        }

        if(hours > 12 && hours < 24) {
            return reservation.getValue().multiply(BigDecimal.valueOf(0.75));
        }

        if(hours > 2 && hours < 12) {
            return reservation.getValue().multiply(BigDecimal.valueOf(0.5));
        }

        if(hours > 0 && hours < 12) {
            return reservation.getValue().multiply(BigDecimal.valueOf(0.25));
        }

        return BigDecimal.ZERO;
    }

    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) {
        Reservation previousReservation = cancel(previousReservationId);

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(scheduleId)
                .value(previousReservation.getValue())
                .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));

        if (scheduleId.equals(previousReservation.getSchedule().getId())) {
            // throw new IllegalArgumentException("Cannot reschedule to the same slot.");
            log.info("Reservation with id {} was rescheduled to the same slot - NEW RESERVATION_ID {} ON SCHEDULE_ID {} !!!", previousReservationId, newReservation.getId(), scheduleId);
        }

        return newReservation;
    }

    public List<ReservationDTO> getPastReservations() {
        return reservationMapper.map(reservationRepository.getPastReservations());
    }
}
