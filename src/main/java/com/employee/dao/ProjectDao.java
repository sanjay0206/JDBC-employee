package com.employee.dao;

import com.employee.entities.Project;
import com.employee.enums.ProjectStatus;
import com.employee.utils.ServerResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface ProjectDao {
    List<Project> getAllProjects() throws SQLException;

    ServerResponseDto saveProject(String payload) throws SQLException, JsonProcessingException;

    ServerResponseDto updateProject(String payload, int projectId) throws SQLException, JsonProcessingException;

    ServerResponseDto deleteProject(int projectId) throws SQLException;

    boolean projectExists(int projectId) throws SQLException;

    int executeProjectUpdateQuery(int projectId, String projectName, Date endDate, ProjectStatus projectStatus) throws SQLException;
}

