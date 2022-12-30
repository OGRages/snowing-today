package me.yattaw.snowingtoday.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public class JsonParser {


    /**
     * Get Json From URL
     *
     * @param url the url in string format
     * @return json data from the url
     */
    public static String getJsonFromUrl(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                return in.lines().collect(Collectors.joining());
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Convert String to JsonNode
     *
     * @param json string data to parse
     * @return JsonNode from String
     */
    public static JsonNode parseJsonNode(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
