package com.employee.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class JsonUtility {
    public static ObjectMapper MAPPER;
    private static final Logger logger = LogManager.getLogger(JsonUtility.class);

    static {
        logger.debug("MAPPER static block is executed...");
        MAPPER = new ObjectMapper();
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static ObjectMapper getObjectMapper() {
        return MAPPER;
    }

    public static String readRequestBody(HttpServletRequest request) {
        String payload = "";
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            payload = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        logger.debug("Incoming payload: {}", payload);
        return payload;
    }
}
