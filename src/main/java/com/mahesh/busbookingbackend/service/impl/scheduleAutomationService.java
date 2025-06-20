package com.mahesh.busbookingbackend.service.impl;

import com.mahesh.busbookingbackend.entity.BusScheduleEntity;
import com.mahesh.busbookingbackend.repository.BusScheduleRepository;
import com.mahesh.busbookingbackend.service.ScheduleAutomationService;
import com.mahesh.busbookingbackend.service.SeatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class scheduleAutomationService implements ScheduleAutomationService {
    private final BusScheduleRepository scheduleRepository;
    private final SeatService seatService;

    public scheduleAutomationService(BusScheduleRepository scheduleRepository, SeatService seatService) {
        this.scheduleRepository = scheduleRepository;
        this.seatService = seatService;
    }

    @Transactional
    public void performInitialGeneration(BusScheduleEntity master) {
//        if (!master.isMasterRecord() || master.getAutomationDuration() == null) {
//            return;
//        }

        log.info("Performing large initial schedule generation for master record ID: {}", master.getId());
        long daysToGenerate = master.getAutomationDuration().getMonths() * 30L;
        LocalDate startDate = master.getScheduleDate();
        LocalDate endDate = startDate.plusDays(daysToGenerate);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Create schedule only if it doesn't already exist for that date
            if (!scheduleRepository.existsByBusEntityIdAndBusRouteIdAndScheduleDate(
                    master.getBusEntity().getId(), master.getBusRoute().getId(), date)) {
                createAndSaveNewSchedule(master, date);
            }
        }
        log.info("Initial generation complete for master record ID: {}. Created schedules up to {}", master.getId(), endDate);
    }


    /**
     * Daily job to maintain the rolling window.
     * This is an efficient "top-up" operation.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void maintainRollingWindow() {
        log.info("Executing daily rolling window maintenance job...");

        List<BusScheduleEntity> masterRecords = scheduleRepository.findByIsMasterRecordTrue();

        for (BusScheduleEntity master : masterRecords) {
            long windowSizeInDays = master.getAutomationDuration().getMonths() * 30L;
            LocalDate today = LocalDate.now();

            // Find the last schedule we have in the database for this route
            LocalDate lastExistingScheduleDate = scheduleRepository
                    .findLatestScheduleDateForBusAndRoute(master.getBusEntity().getId(), master.getBusRoute().getId())
                    .orElse(today.minusDays(1));

            // Calculate the date the window SHOULD end on
            LocalDate desiredWindowEndDate = today.plusDays(windowSizeInDays);

            // If our last schedule is before the desired end date, add the missing ones.
            // On a normal day, this loop will run only once to add the newest day.
            LocalDate nextDateToCreate = lastExistingScheduleDate.plusDays(1);
            while(!nextDateToCreate.isAfter(desiredWindowEndDate)) {
                createAndSaveNewSchedule(master, nextDateToCreate);
                nextDateToCreate = nextDateToCreate.plusDays(1);
            }
        }
        log.info("Daily rolling window maintenance job finished.");
    }

    public void createAndSaveNewSchedule(BusScheduleEntity master, LocalDate scheduleDate) {
        BusScheduleEntity newSchedule = new BusScheduleEntity();
        newSchedule.setBusEntity(master.getBusEntity());
        newSchedule.setBusRoute(master.getBusRoute());
        newSchedule.setArrivalTime(master.getArrivalTime());
        newSchedule.setDepartureTime(master.getDepartureTime());
        newSchedule.setFarePrice(master.getFarePrice());
        newSchedule.setTotalSeats(master.getTotalSeats());
        newSchedule.setScheduleDate(scheduleDate);
        newSchedule.setMasterRecord(false);

        BusScheduleEntity schedule = scheduleRepository.save(newSchedule);
        seatService.generateSeats(schedule.getId());
        log.info("Auto-generated schedule for Bus {} on {}", master.getBusEntity().getId(), scheduleDate);
    }
}
