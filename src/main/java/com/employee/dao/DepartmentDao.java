package com.employee.dao;

import com.employee.entities.Department;
import com.employee.utils.ServerResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.List;

public interface DepartmentDao {
    List<Department> getAllDepartments() throws SQLException;

    ServerResponseDto saveDepartment(String payload) throws SQLException, JsonProcessingException;

    ServerResponseDto updateDepartment(String payload, int deptId) throws SQLException, JsonProcessingException;

    ServerResponseDto deleteDepartment(int deptId) throws SQLException;

    boolean departmentExists(int deptId) throws SQLException;
}
