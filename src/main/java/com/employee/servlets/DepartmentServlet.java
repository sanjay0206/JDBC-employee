package com.employee.servlets;

import com.employee.dao.DepartmentDao;
import com.employee.dao.impl.DepartmentDaoImpl;
import com.employee.entities.Department;
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

@WebServlet("/departments")
public class DepartmentServlet extends HttpServlet {
    private final DepartmentDao departmentDao = new DepartmentDaoImpl();
    public static ObjectMapper MAPPER = JsonUtility.getObjectMapper();
    private static final Logger logger = LogManager.getLogger(DepartmentServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ServerResponseDto responseDto;
        try {
            List<Department> departments = departmentDao.getAllDepartments();
            logger.debug("departments: {}", departments);

            if (departments.isEmpty()) {
                responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Sorry, no departments in the database right now!");
                out.println(MAPPER.writeValueAsString(responseDto));
            } else {
                out.println(MAPPER.writeValueAsString(departments));
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
            responseDto = departmentDao.saveDepartment(payload);
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
            int deptId = request.getParameter("deptId").isEmpty() ? 0: Integer.parseInt(request.getParameter("deptId"));
            responseDto = departmentDao.updateDepartment(payload, deptId);
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
            int deptId = request.getParameter("deptId").isEmpty() ? 0: Integer.parseInt(request.getParameter("deptId"));
            responseDto = departmentDao.deleteDepartment(deptId);
        } catch (Exception e) {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.println(MAPPER.writeValueAsString(responseDto));
    }
}
