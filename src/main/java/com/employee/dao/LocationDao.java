package com.employee.dao;

import com.employee.entities.Location;
import com.employee.utils.ServerResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.List;

public interface LocationDao {
    List<Location> getAllLocations() throws SQLException;

    ServerResponseDto saveLocation(String payload) throws SQLException, JsonProcessingException;

    ServerResponseDto updateLocation(String payload, int locationId) throws SQLException, JsonProcessingException;

    ServerResponseDto deleteLocation(int locationId) throws SQLException;

    boolean locationExists(int locationId) throws SQLException;

    int executeLocationUpdateQuery (int locationId, String address, String city, String country, String postalCode) throws SQLException;
}
