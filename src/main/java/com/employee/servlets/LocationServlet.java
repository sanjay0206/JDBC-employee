package com.employee.servlets;

import com.employee.dao.LocationDao;
import com.employee.dao.impl.LocationDaoImpl;
import com.employee.entities.Location;
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

@WebServlet("/locations")
public class LocationServlet extends HttpServlet {
    private final LocationDao locationDao = new LocationDaoImpl();
    public static ObjectMapper MAPPER = JsonUtility.getObjectMapper();
    private static final Logger logger = LogManager.getLogger(LocationServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ServerResponseDto responseDto;
        try {
            List<Location> locations = locationDao.getAllLocations();
            logger.debug("locations: {}", locations);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if (locations.isEmpty()) {
                responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Sorry, no locations in the database right now!");
                out.println(MAPPER.writeValueAsString(responseDto));
            } else {
                out.println(MAPPER.writeValueAsString(locations));
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
            responseDto = locationDao.saveLocation(payload);
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
            int locationId = request.getParameter("locationId").isEmpty() ? 0: Integer.parseInt(request.getParameter("locationId"));
            responseDto = locationDao.updateLocation(payload, locationId);
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
            int locationId = request.getParameter("locationId").isEmpty() ? 0: Integer.parseInt(request.getParameter("locationId"));
            responseDto = locationDao.deleteLocation(locationId);
        } catch (Exception e) {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.println(MAPPER.writeValueAsString(responseDto));
    }
}
