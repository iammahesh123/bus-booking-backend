package com.mahesh.busbookingbackend.service;

import com.mahesh.busbookingbackend.entity.BusScheduleEntity;

import java.time.LocalDate;

public interface ScheduleAutomationService {
    public void performInitialGeneration(BusScheduleEntity master);
    public void maintainRollingWindow();
    void createAndSaveNewSchedule(BusScheduleEntity master, LocalDate scheduleDate);
}
