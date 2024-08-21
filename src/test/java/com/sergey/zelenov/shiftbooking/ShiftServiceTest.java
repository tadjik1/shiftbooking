package com.sergey.zelenov.shiftbooking;

import com.sergey.zelenov.shiftbooking.model.Shift;
import com.sergey.zelenov.shiftbooking.repository.ShiftRepository;
import com.sergey.zelenov.shiftbooking.service.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class ShiftServiceTest {

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private ShiftRepository shiftRepository;

    @BeforeEach
    void setUp() {
        shiftRepository.deleteAll();
    }

    @Test
    void shouldCreateShiftSuccessfully() {
        Shift shift = new Shift();
        shift.setTitle("Morning Shift");
        shift.setDescription("Warehouse morning1 shift");
        shift.setLocation("Berlin");
        shift.setHourlyRate(20.0);
        shift.setStartTime(LocalDateTime.now());
        shift.setEndTime(LocalDateTime.now().plusHours(8));

        Shift createdShift = shiftService.createShift(shift);

        assertNotNull(createdShift.getId());
        assertEquals("Morning Shift", createdShift.getTitle());
        assertEquals("Berlin", createdShift.getLocation());
    }

    @Test
    void shouldUpdateShiftSuccessfully() {
        Shift shift = new Shift();
        shift.setTitle("Morning Shift");
        shift.setDescription("Warehouse morning shift");
        shift.setLocation("Berlin");
        shift.setHourlyRate(20.0);
        shift.setStartTime(LocalDateTime.now());
        shift.setEndTime(LocalDateTime.now().plusHours(8));

        Shift createdShift = shiftService.createShift(shift);

        createdShift.setTitle("Updated Shift Title");
        createdShift.setLocation("Hamburg");
        Shift updatedShift = shiftService.updateShift(createdShift.getId(), createdShift);

        assertEquals("Updated Shift Title", updatedShift.getTitle());
        assertEquals("Hamburg", updatedShift.getLocation());
    }

    @Test
    void shouldDeleteShiftSuccessfully() {
        Shift shift = new Shift();
        shift.setTitle("Morning Shift");
        shift.setDescription("Warehouse morning shift");
        shift.setLocation("Berlin");
        shift.setHourlyRate(20.0);
        shift.setStartTime(LocalDateTime.now());
        shift.setEndTime(LocalDateTime.now().plusHours(8));

        Shift createdShift = shiftService.createShift(shift);
        shiftService.deleteShift(createdShift.getId());

        Optional<Shift> deletedShift = shiftRepository.findById(createdShift.getId());
        assertTrue(deletedShift.isEmpty());
    }

    @Test
    void shouldGetShiftByIdSuccessfully() {
        Shift shift = new Shift();
        shift.setTitle("Morning Shift");
        shift.setDescription("Warehouse morning shift");
        shift.setLocation("Berlin");
        shift.setHourlyRate(20.0);
        shift.setStartTime(LocalDateTime.now());
        shift.setEndTime(LocalDateTime.now().plusHours(8));

        Shift createdShift = shiftService.createShift(shift);
        Shift fetchedShift = shiftService.getShiftById(createdShift.getId());

        assertEquals(createdShift.getId(), fetchedShift.getId());
        assertEquals("Morning Shift", fetchedShift.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenShiftNotFound() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            shiftService.getShiftById(999L);
        });
        assertEquals("Shift not found", thrown.getMessage());
    }

    @Test
    void shouldGetAllShiftsSuccessfully() {
        Shift shift1 = new Shift();
        shift1.setTitle("Morning Shift");
        shift1.setDescription("Warehouse morning shift");
        shift1.setLocation("Berlin");
        shift1.setHourlyRate(20.0);
        shift1.setStartTime(LocalDateTime.now());
        shift1.setEndTime(LocalDateTime.now().plusHours(8));
        shiftService.createShift(shift1);

        Shift shift2 = new Shift();
        shift2.setTitle("Night Shift");
        shift2.setDescription("Warehouse night shift");
        shift2.setLocation("Hamburg");
        shift2.setHourlyRate(25.0);
        shift2.setStartTime(LocalDateTime.now().plusHours(12));
        shift2.setEndTime(LocalDateTime.now().plusHours(20));
        shiftService.createShift(shift2);

        assertEquals(2, shiftService.getAllShifts().size());
    }
}
