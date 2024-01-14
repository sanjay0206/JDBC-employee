package com.employee.servlets;

import com.employee.dao.ProjectDao;
import com.employee.dao.impl.ProjectDaoImpl;
import com.employee.entities.Project;
import com.employee.enums.ResponseStatus;
import com.employee.utils.JsonUtility;
import com.employee.utils.ServerResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/projects")
public class ProjectServlet extends HttpServlet {
    private final ProjectDao projectDao = new ProjectDaoImpl();
    public static ObjectMapper MAPPER = JsonUtility.getObjectMapper();
    private static final Logger logger = LogManager.getLogger(ProjectServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ServerResponseDto responseDto;
        try {
            List<Project> projects = projectDao.getAllProjects();
            logger.debug("projects: {}", projects);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if (projects.isEmpty()) {
                responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Sorry, no projects in the database right now!");
                out.println(MAPPER.writeValueAsString(responseDto));
            } else {
                out.println(MAPPER.writeValueAsString(projects));
            }
        } catch (Exception e) {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, e.getMessage());
            out.println(MAPPER.writeValueAsString(responseDto));
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ServerResponseDto responseDto;
        try {
            String payload = JsonUtility.readRequestBody(request);
            responseDto = projectDao.saveProject(payload);
        } catch (Exception e) {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.println(MAPPER.writeValueAsString(responseDto));
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ServerResponseDto responseDto;
        try {
            String payload = JsonUtility.readRequestBody(request);
            int projectId = request.getParameter("projectId").isEmpty() ? 0: Integer.parseInt(request.getParameter("projectId"));
            responseDto = projectDao.updateProject(payload, projectId);
        } catch (Exception e) {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.println(MAPPER.writeValueAsString(responseDto));
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ServerResponseDto responseDto;
        try {
            int projectId = request.getParameter("projectId").isEmpty() ? 0: Integer.parseInt(request.getParameter("projectId"));
            responseDto = projectDao.deleteProject(projectId);
        } catch (Exception e) {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.println(MAPPER.writeValueAsString(responseDto));
    }
}
