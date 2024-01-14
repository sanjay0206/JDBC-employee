package com.employee.dao;

import com.employee.entities.Employee;
import com.employee.utils.ServerResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeDao {
    List<Employee> getAllEmployees() throws SQLException;

    ServerResponseDto saveEmployee(String payload, int deptId, int projectId, int locationId) throws SQLException, JsonProcessingException;

    ServerResponseDto updateEmployee(String payload, int employeeId, int deptId, int projectId, int locationId) throws SQLException, JsonProcessingException;

    ServerResponseDto deleteEmployee(int employeeId) throws SQLException;

    boolean employeeExists(int employeeId) throws SQLException;

    int executeEmployeeUpdateQuery(int employeeId, String firstName, String lastName, double salary,
                                   int newProjectId, int newDeptId, int newLocationId) throws SQLException;
}
