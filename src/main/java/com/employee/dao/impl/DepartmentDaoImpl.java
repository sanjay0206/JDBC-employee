package com.employee.dao.impl;

import com.employee.dao.DepartmentDao;
import com.employee.entities.Department;
import com.employee.enums.ResponseStatus;
import com.employee.utils.DBConnector;
import com.employee.utils.JsonUtility;
import com.employee.utils.ServerResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoImpl implements DepartmentDao {
    private static final Connection DB_CONN = DBConnector.getConnection();
    public static String sqlStatement = "";
    public static ObjectMapper MAPPER = JsonUtility.getObjectMapper();
    private static final Logger logger = LogManager.getLogger(DepartmentDaoImpl.class);

    @Override
    public List<Department> getAllDepartments() throws SQLException {
        List<Department> departmentsList = new ArrayList<>();
        sqlStatement = "SELECT * FROM departments";
        Statement stmt = DB_CONN.createStatement();
        ResultSet rs = stmt.executeQuery(sqlStatement);
        while (rs.next()) {
            int deptId = rs.getInt("dept_id");
            String deptName = rs.getString("dept_name");
            Department department = new Department(deptId, deptName);
            departmentsList.add(department);
        }
        return departmentsList;
    }

    @Override
    public ServerResponseDto saveDepartment(String payload) throws SQLException, JsonProcessingException {
        Department department = MAPPER.readValue(payload, Department.class);
        logger.debug("department: {}", department);

        String deptName = department.getDeptName();
        // Validation checks
        if (deptName == null || deptName.trim().isEmpty()) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Department name cannot be null or empty");
        }

        sqlStatement = "INSERT INTO departments (dept_name) VALUES (?)";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setString(1, deptName);
        int rowsAffected = pstmt.executeUpdate();
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "New department is created");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Could not add new department");
        }
        return responseDto;
    }

    @Override
    public ServerResponseDto updateDepartment(String payload, int deptId) throws SQLException, JsonProcessingException {
        Department department = MAPPER.readValue(payload, Department.class);
        logger.debug("department: {}", department);

        String deptName = department.getDeptName();
        if (!departmentExists(deptId)) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Department with ID " + deptId + " not found");
        }

        sqlStatement = "UPDATE departments SET dept_name = ? where dept_id = ?";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setString(1, deptName);
        pstmt.setInt(2, deptId);
        int rowsAffected = pstmt.executeUpdate();
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "Department details with ID " + deptId + " is updated successfully");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Could not update Department with ID " + deptId);
        }
        return responseDto;
    }

    @Override
    public ServerResponseDto deleteDepartment(int deptId) throws SQLException {
        sqlStatement = "DELETE FROM departments WHERE dept_id = ?";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setInt(1, deptId);
        int rowsAffected = pstmt.executeUpdate();
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "Department with ID " + deptId + " is deleted successfully");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Department with ID " + deptId + " is not found");
        }
        return responseDto;
    }

    @Override
    public boolean departmentExists(int deptId) throws SQLException {
        sqlStatement = "SELECT COUNT(*) FROM departments WHERE dept_id = ?";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setInt(1, deptId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int count = rs.getInt(1);
            return count > 0;
        }
        return false;
    }
}
