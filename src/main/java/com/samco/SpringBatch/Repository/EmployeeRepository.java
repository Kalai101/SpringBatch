package com.samco.SpringBatch.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samco.SpringBatch.Entity.Employees;

@Repository
public interface EmployeeRepository extends JpaRepository<Employees, Integer> {

}
