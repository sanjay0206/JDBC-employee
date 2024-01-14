package com.employee.dao.impl;

import com.employee.dao.DepartmentDao;
import com.employee.dao.EmployeeDao;
import com.employee.dao.LocationDao;
import com.employee.dao.ProjectDao;
import com.employee.entities.Employee;
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

public class EmployeeDaoImpl implements EmployeeDao {
    private static final Connection DB_CONN = DBConnector.getConnection();
    public static String sqlStatement = "";
    public static ObjectMapper MAPPER = JsonUtility.getObjectMapper();
    private static final Logger logger = LogManager.getLogger(EmployeeDaoImpl.class);

    // Inject dependencies (DepartmentDao, LocationDao, ProjectDao)
    public DepartmentDao departmentDao = new DepartmentDaoImpl();
    public LocationDao locationDao = new LocationDaoImpl();
    public ProjectDao projectDao = new ProjectDaoImpl();

    @Override
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employeesList = new ArrayList<>();
        Statement stmt = DB_CONN.createStatement();
        sqlStatement = "SELECT * FROM employees";
        ResultSet rs = stmt.executeQuery(sqlStatement);
        while (rs.next()) {
            int employeeId = rs.getInt("employee_id");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            double salary = rs.getDouble("salary");
//            int deptId = rs.getInt("fk_dept_id");
//            int projectId = rs.getInt("fk_proj_id");
//            int locationId = rs.getInt("fk_loc_id");
            Employee employee = new Employee(employeeId, firstName, lastName, salary);
            employeesList.add(employee);
        }
        return employeesList;
    }

    @Override
    public ServerResponseDto saveEmployee(String payload, int deptId, int projectId, int locationId) throws SQLException, JsonProcessingException {
        Employee employee = MAPPER.readValue(payload, Employee.class);
        logger.debug("employee: {}", employee);

        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        double salary = employee.getSalary();

        // Check whether the project exists or not
        if (!projectDao.projectExists(projectId)) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Project with ID " + projectId + " not found");
        }

        // Check whether the department exists or not
        if (!departmentDao.departmentExists(deptId)) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Department with ID " + deptId + " not found");
        }

        // Check whether the location exists or not
        if (!locationDao.locationExists(locationId)) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Location with ID " + locationId + " not found");
        }

        // Validation checks
        if (firstName == null || firstName.trim().isEmpty()) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Employee first name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Employee last name cannot be null or empty");
        }
        if (salary <= 0) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Employee salary must be greater than 0");
        }

        sqlStatement = "INSERT INTO employees (first_name, last_name, salary, fk_dept_id, fk_proj_id, fk_loc_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setString(1, firstName);
        pstmt.setString(2, lastName);
        pstmt.setDouble(3, salary);
        pstmt.setInt(4, deptId);
        pstmt.setInt(5, projectId);
        pstmt.setInt(6, locationId);
        int rowsAffected = pstmt.executeUpdate();
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "New employee is created");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Could not add new employee");
        }
        return responseDto;
    }

    @Override
    public ServerResponseDto updateEmployee(String payload, int employeeId,
                                            int newDeptId, int newProjectId, int newLocationId)
            throws SQLException, JsonProcessingException {
        Employee employee = MAPPER.readValue(payload, Employee.class);
        logger.debug("employee: {}", employee);

        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        double salary = employee.getSalary();

        // Check whether the employee exists or not
        if (!employeeExists(employeeId)) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Employee with ID " + employeeId + " not found");
        }

        // Check and update department
        if (newDeptId > 0 && !departmentDao.departmentExists(newDeptId)) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Department with ID " + newDeptId + " not found");
        }

        // Check and update location
        if (newLocationId > 0 && !locationDao.locationExists(newLocationId)) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Location with ID " + newLocationId + " not found");
        }

        // Check and update project
        if (newProjectId > 0 && !projectDao.projectExists(newProjectId)) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Project with ID " + newProjectId + " not found");
        }
        
        int rowsAffected =  executeEmployeeUpdateQuery(employeeId, firstName, lastName, salary, newProjectId, newDeptId, newLocationId);
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "Employee details with ID " + employeeId + " is updated successfully");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Could not update Employee with ID " + employeeId);
        }
        return responseDto;
    }

    @Override
    public int executeEmployeeUpdateQuery(int employeeId, String firstName, String lastName, double salary,
                                          int newProjectId, int newDeptId, int newLocationId) throws SQLException {
        int parameterIndex = 1;
        String query = "UPDATE employees SET ";
        PreparedStatement pstmt = DB_CONN.prepareStatement(query);
        if (firstName != null && !firstName.trim().isEmpty()) {
            query += "first_name = ?, ";
            pstmt.setString(parameterIndex++, firstName);
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            query += "last_name = ?, ";
            pstmt.setString(parameterIndex++, lastName);
        }
        if (salary > 0) {
            query += "salary = ?, ";
            pstmt.setDouble(parameterIndex++, salary);
        }
        if (newProjectId > 0) {
            query += "fk_proj_id = ?, ";
            pstmt.setInt(parameterIndex++, newProjectId);
        }
        if (newDeptId > 0) {
            query += "fk_dept_id = ?, ";
            pstmt.setInt(parameterIndex++, newDeptId);
        }
        if (newLocationId > 0) {
            query += "fk_loc_id = ?, ";
            pstmt.setInt(parameterIndex++, newLocationId);
        }
        int lastIndex = query.lastIndexOf(",");
        query = (lastIndex != -1) ? query.substring(0, lastIndex) + query.substring(lastIndex + 1) : query;
        query += " WHERE employee_id = ?";
        pstmt.setInt(parameterIndex, employeeId);
        logger.debug("update query: {}", query);
        return pstmt.executeUpdate();
    }

    @Override
    public ServerResponseDto deleteEmployee(int employeeId) throws SQLException {
        sqlStatement = "DELETE FROM employees WHERE employee_id = ?";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setInt(1, employeeId);
        int rowsAffected = pstmt.executeUpdate();
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "Employee with ID " + employeeId + " is deleted successfully");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Employee with ID " + employeeId + " is not found");
        }
        return responseDto;
    }

    @Override
    public boolean employeeExists(int employeeId) throws SQLException {
        sqlStatement = "SELECT COUNT(*) FROM employees WHERE employee_id = ?";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setInt(1, employeeId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int count = rs.getInt(1);
            return count > 0;
        }
        return false;
    }
}
