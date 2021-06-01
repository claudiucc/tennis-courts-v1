package com.tenniscourts.schedulereservation;

import com.tenniscourts.config.persistence.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="SCHEDULE_RESERVATIONS")
public class SR extends BaseEntity<Long> {

    @Column(name = "SCHEDULE_ID")
    @NotNull
    private Long scheduleId;

    @Column(name = "RESERVATIONS_ID")
    @NotNull
    private Long reservationId;
}
