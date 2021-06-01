package com.tenniscourts.guests;

import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GuestMapper {
    GuestDTO map(Guest source);

    @InheritInverseConfiguration
    Guest map(GuestDTO source);

    List<GuestDTO> map(List<Guest> source);

}
