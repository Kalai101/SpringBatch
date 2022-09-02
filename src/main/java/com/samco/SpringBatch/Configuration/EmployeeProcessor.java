package com.samco.SpringBatch.Configuration;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import com.samco.SpringBatch.Entity.Employees;

@Service
public class EmployeeProcessor implements ItemProcessor<Employees, Employees> {

	@Override
	public Employees process(Employees employees) throws Exception {
		
		return employees;
	}
}