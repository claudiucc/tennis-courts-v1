package com.tenniscourts.schedules;

import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtMapper;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import com.tenniscourts.tenniscourts.TennisCourtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    private final TennisCourtRepository tennisCourtRepository;

    private final TennisCourtMapper tennisCourtMapper;

    public ScheduleDTO addSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        Schedule schedule = new Schedule();
        TennisCourt tennisCourt = new TennisCourt();
        tennisCourt.setId(tennisCourtId);
        schedule.setTennisCourt(tennisCourt);
        schedule.setStartDateTime(createScheduleRequestDTO.getStartDateTime());
        schedule.setEndDateTime(createScheduleRequestDTO.getStartDateTime().plusHours(1));
        return scheduleMapper.map(scheduleRepository.save(schedule));
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleMapper.map(scheduleRepository.findByStartDateTimeAfterAndEndDateTimeBefore(startDate, endDate));
    }

    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleMapper.map(scheduleRepository.findById(scheduleId).get());
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }

    public List<ScheduleDTO> findAllFreeSchedules() {
        return scheduleMapper.map(scheduleRepository.findAllFreeSchedules());
    }

    public List<ScheduleDTO> createScheduleSlots(Long courtId) {
        TennisCourt tennisCourt = tennisCourtRepository.findById(courtId).get();
        List<Schedule> schedules = new ArrayList<Schedule>();
        IntStream.range(10, 22) // It's supposed to generate from 10:00am to 10:00pm (normal opening hours) - this could be passed as parameter
                .forEach(i -> {
                    Schedule schedule = new Schedule();
                    schedule.setTennisCourt(tennisCourt);
                    LocalDateTime startDateTime = LocalDate.now().atTime(i, 0);
                    LocalDateTime endDateTime = LocalDate.now().atTime(i + 1, 0);
                    schedule.setStartDateTime(startDateTime);
                    schedule.setEndDateTime(endDateTime);
                    schedules.add(schedule);
                });
        return scheduleMapper.map(scheduleRepository.saveAll(schedules));
    }
}
