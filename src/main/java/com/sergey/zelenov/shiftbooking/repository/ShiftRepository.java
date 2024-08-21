package com.sergey.zelenov.shiftbooking.repository;

import com.sergey.zelenov.shiftbooking.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
