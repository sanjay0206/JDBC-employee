package com.employee.dao.impl;

import com.employee.dao.LocationDao;
import com.employee.entities.Location;
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

public class LocationDaoImpl implements LocationDao {
    private static final Connection DB_CONN = DBConnector.getConnection();
    public static String sqlStatement = "";
    public static ObjectMapper MAPPER = JsonUtility.getObjectMapper();
    private static final Logger logger = LogManager.getLogger(LocationDaoImpl.class);

    @Override
    public List<Location> getAllLocations() throws SQLException {
        List<Location> locationsList = new ArrayList<>();
        sqlStatement = "SELECT * FROM locations";
        Statement stmt = DB_CONN.createStatement();
        ResultSet rs = stmt.executeQuery(sqlStatement);
        while (rs.next()) {
            int locationId = rs.getInt("location_id");
            String address = rs.getString("address");
            String city = rs.getString("city");
            String country = rs.getString("country");
            String postalCode = rs.getString("postal_code");
            Location location = new Location(locationId, address, city, country, postalCode);
            locationsList.add(location);
        }
        return locationsList;
    }

    @Override
    public ServerResponseDto saveLocation(String payload) throws SQLException, JsonProcessingException {
        Location location = MAPPER.readValue(payload, Location.class);
        logger.debug("location: {}", location);

        String address = location.getAddress();
        String city = location.getCity();
        String country = location.getCountry();
        String postalCode = location.getPostalCode();

        // Validation checks
        if (address == null || address.trim().isEmpty()) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Location address cannot be null or empty");
        }
        if (city == null || city.trim().isEmpty()) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Location city cannot be null or empty");
        }
        if (country == null || country.trim().isEmpty()) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Location country cannot be null or empty");
        }
        if (postalCode == null || postalCode.trim().isEmpty()) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Location postal code cannot be null or empty");
        }

        sqlStatement = "INSERT INTO locations (address, city, country, postal_code) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setString(1, address);
        pstmt.setString(2, city);
        pstmt.setString(3, country);
        pstmt.setString(4, postalCode);
        int rowsAffected = pstmt.executeUpdate();
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "New location is created");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Could not add new location");
        }
        return responseDto;
    }

    @Override
    public ServerResponseDto updateLocation(String payload, int locationId) throws SQLException, JsonProcessingException {
        Location location = MAPPER.readValue(payload, Location.class);
        logger.debug("location: {}", location);

        String address = location.getAddress();
        String city = location.getCity();
        String country = location.getCountry();
        String postalCode = location.getPostalCode();

        if (!locationExists(locationId)) {
            return new ServerResponseDto(ResponseStatus.FAILURE, "Location with ID " + locationId + " not found");
        }

        int rowsAffected = executeLocationUpdateQuery(locationId, address, country, city, postalCode);
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "Location details with ID " + locationId + " is updated successfully");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Could not update Location with ID " + locationId);
        }
        return responseDto;
    }

    @Override
    public int executeLocationUpdateQuery(int locationId, String address, String city, String country, String postalCode) throws SQLException {
        String query = "UPDATE locations SET ";
        PreparedStatement pstmt = DB_CONN.prepareStatement(query);
        int parameterIndex = 1;
        if (address != null && !address.trim().isEmpty()) {
            query += "address = ?, ";
            pstmt.setString(parameterIndex++, address);
        }
        if (city != null && !city.trim().isEmpty()) {
            query += "city = ?, ";
            pstmt.setString(parameterIndex++, city);
        }
        if (country != null && !country.trim().isEmpty()) {
            query += "country = ?, ";
            pstmt.setString(parameterIndex++, country);
        }
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            query += "postal_code = ?, ";
            pstmt.setString(parameterIndex++, postalCode);
        }

        int lastIndex = query.lastIndexOf(",");
        query = (lastIndex != -1) ? query.substring(0, lastIndex) + query.substring(lastIndex + 1) : query;
        query += " WHERE location_id = ?";
        pstmt.setInt(parameterIndex, locationId);
        logger.debug("update query: {}", query);
        return pstmt.executeUpdate();
    }

    @Override
    public ServerResponseDto deleteLocation(int locationId) throws SQLException {
        sqlStatement = "DELETE FROM locations WHERE location_id = ?";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setInt(1, locationId);
        int rowsAffected = pstmt.executeUpdate();
        logger.debug("rowsAffected: {}", rowsAffected);
        ServerResponseDto responseDto;
        if (rowsAffected > 0) {
            DB_CONN.commit();
            responseDto = new ServerResponseDto(ResponseStatus.SUCCESS, "Location with ID " + locationId + " is deleted successfully");
        } else {
            responseDto = new ServerResponseDto(ResponseStatus.FAILURE, "Location with ID " + locationId + " is not found");
        }
        return responseDto;
    }

    @Override
    public boolean locationExists(int locationId) throws SQLException {
        sqlStatement = "SELECT COUNT(*) FROM locations WHERE location_id = ?";
        PreparedStatement pstmt = DB_CONN.prepareStatement(sqlStatement);
        pstmt.setInt(1, locationId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int count = rs.getInt(1);
            return count > 0;
        }
        return false;
    }
}
