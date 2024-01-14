package com.employee.dao.impl;

import com.employee.dao.ProjectDao;
import com.employee.entities.Project;
import com.employee.enums.ProjectStatus;
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

public class ProjectDaoImpl implements ProjectDao {
    private static final Connection DB_CONN = DBConnector.getConnection();
    public static String sqlStatement = "";
    public static ObjectMapper MAPPER = JsonUtility.getObjectMapper();
    private static final Logger logger = LogManager.getLogger(ProjectDaoImpl.class);

    @Override
    public List<Project> getAllProjects() throws SQLException {
        List<Project> projectsList = new ArrayList<>();
        sqlStatement = "SELECT * FROM projects";
        Statement stmt = DB_CONN.createStatement();
        ResultSet rs = stmt.executeQuery(sqlStatement);
        while (rs.next()) {
            int projectId = rs.getInt("project_id");
            String projectName = rs.getString("project_name");
            Date startDate = rs.getDate("start_date");
            Date endDate = rs.getDate("end_date");
            String projectStatus = rs.getString("project_status");
            Project project = new Project(projectId, projectName, startDate, endDate, ProjectStatus.valueOf(projectStatus));
            projectsList.add(project);
        }
        return projectsList;
    }

    @Override
    public ServerResponseDto saveProject(String payload) throws SQLException, JsonProcessingException {
        ServerResponseDto serverResponseDto = new ServerResponseDto();
        Project project = MAPPER.readValue(payload, Project.class);
        logger.debug("project: {}", project);

        String projectName = project.getProjectName();
        Date startDate = project.getStartDate();
        Date endDate = project.getEndDate();
        ProjectStatus projectStatus = project.getProjectStatus();

        // Validation checks
        if (projectName == null || projectName.trim().isEmpty()) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Project name cannot be null or empty");
        }
        if (startDate == null || endDate == null || startDate.toLocalDate().isAfter(endDate.toLocalDate())) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Invalid project dates");
        }
        if (projectStatus == null) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Project status cannot be null");
        }

        sqlStatement = "INSERT INTO projects (project_name, start_date, end_date, project_status) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setString(1, projectName);
        pstmt.setObject(2, startDate);
        pstmt.setObject(3, endDate);
        pstmt.setString(4, projectStatus.name());
        int rowsAffected = pstmt.executeUpdate();
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "New project is created");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Could not add new project");
        }
        return responseDto;
    }

    @Override
    public ServerResponseDto updateProject(String payload, int projectId) throws SQLException, JsonProcessingException {
        ServerResponseDto serverResponseDto = new ServerResponseDto();
        Project project = MAPPER.readValue(payload, Project.class);
        logger.debug("project: {}", project);

        String projectName = project.getProjectName();
        Date startDate = project.getStartDate();
        Date endDate = project.getEndDate();
        ProjectStatus projectStatus = project.getProjectStatus();
        if (startDate != null) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Project start date cannot be updated");
        }

        if (!projectExists(projectId)) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Project with ID " + projectId + " not found");
        }

        int rowsAffected = executeProjectUpdateQuery(projectId, projectName, endDate, projectStatus);
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "Project details with ID " + projectId + " is updated successfully");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Could not update project with ID " + projectId);
        }
        return responseDto;
    }

    @Override
    public int executeProjectUpdateQuery(int projectId, String projectName, Date endDate, ProjectStatus projectStatus) throws SQLException {
        String query = "UPDATE projects SET ";
        PreparedStatement pstmt = DB_CONN.prepareStatement(query);
        int parameterIndex = 1;
        if (projectName != null && !projectName.trim().isEmpty()) {
            query += "project_name = ?, ";
            pstmt.setString(parameterIndex++, projectName);
        }
        if (endDate != null) {
            query += "end_date = ?, ";
            pstmt.setDate(parameterIndex++, endDate);
        }
        if (projectStatus != null) {
            query += "project_status = ?, ";
            pstmt.setString(parameterIndex++, projectStatus.name());
        }
        int lastIndex = query.lastIndexOf(",");
        query = (lastIndex != -1) ? query.substring(0, lastIndex) + query.substring(lastIndex + 1) : query;
        query += " WHERE project_id = ?";
        pstmt.setInt(parameterIndex, projectId);
        logger.debug("update query: {}", query);
        return pstmt.executeUpdate();
    }

    @Override
    public ServerResponseDto deleteProject(int projectId) throws SQLException {
        ServerResponseDto serverResponseDto = new ServerResponseDto();
        sqlStatement = "DELETE FROM projects WHERE project_id = ?";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setInt(1, projectId);
        int rowsAffected = pstmt.executeUpdate();
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "Project with ID " + projectId + " is deleted successfully");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Project with ID " + projectId + " is not found");
        }
        return responseDto;
    }

    @Override
    public boolean projectExists(int projectId) throws SQLException {
        sqlStatement = "SELECT COUNT(*) FROM projects WHERE project_id = ?";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setInt(1, projectId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int count = rs.getInt(1);
            return count > 0;
        }
        return false;
    }
}
