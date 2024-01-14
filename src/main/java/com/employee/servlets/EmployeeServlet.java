package com.employee.servlets;

import com.employee.dao.EmployeeDao;
import com.employee.dao.impl.EmployeeDaoImpl;
import com.employee.entities.Employee;
import com.employee.enums.ResponseStatus;
import com.employee.utils.JsonUtility;
import com.employee.utils.ServerResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/employees")
public class EmployeeServlet extends HttpServlet {
    private final EmployeeDao employeeDao = new EmployeeDaoImpl();
    public static ObjectMapper MAPPER = JsonUtility.getObjectMapper();
    private static final Logger logger = LogManager.getLogger(EmployeeServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ServerResponseDto responseDto;
        try {
            List<Employee> employees = employeeDao.getAllEmployees();
            logger.debug("employees: {}", employees);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            if (employees.isEmpty()) {
                responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Sorry, no employees in the database right now!");
                out.println(mapper.writeValueAsString(responseDto));
            } else {
                out.println(MAPPER.writeValueAsString(employees));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            int deptId = request.getParameter("deptId").isEmpty() ? 0: Integer.parseInt(request.getParameter("deptId"));
            int projectId = request.getParameter("projectId").isEmpty() ? 0: Integer.parseInt(request.getParameter("projectId"));
            int locationId = request.getParameter("locationId").isEmpty() ? 0: Integer.parseInt(request.getParameter("locationId"));

            responseDto = employeeDao.saveEmployee(payload, deptId, projectId, locationId);
        } catch (Exception e) {
            e.printStackTrace();
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

            int employeeId = request.getParameter("employeeId").isEmpty() ? 0: Integer.parseInt(request.getParameter("employeeId"));
            int deptId = request.getParameter("deptId").isEmpty() ? 0: Integer.parseInt(request.getParameter("deptId"));
            int projectId = request.getParameter("projectId").isEmpty() ? 0: Integer.parseInt(request.getParameter("projectId"));
            int locationId = request.getParameter("locationId").isEmpty() ? 0: Integer.parseInt(request.getParameter("locationId"));

            responseDto = employeeDao.updateEmployee(payload, employeeId, deptId, projectId, locationId);
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
            int employeeId = request.getParameter("employeeId").isEmpty() ? 0: Integer.parseInt(request.getParameter("employeeId"));
            responseDto = employeeDao.deleteEmployee(employeeId);
        } catch (Exception e) {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.println(MAPPER.writeValueAsString(responseDto));
    }
}
