package org.example.backend.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class UnuversalOkResponce {

    private Map<String, Object> response;

    public UnuversalOkResponce(Object data,
                               String message,
                               String status,
                               String error) {
        this.response = createHashMap(data, message, status, error);
    }

    public UnuversalOkResponce(Object data, String message) {
        this.response = createHashMap(data, message, null, null);
    }

    public UnuversalOkResponce(Object data, String message, String status) {
        this.response = createHashMap(data, message, status, null);
    }

    private Map<String, Object> createHashMap(Object data,
                                              String message,
                                              String status,
                                              String error) {
        Map<String, Object> response = new HashMap<>();
        if (data != null) response.put("data", data);
        if (message != null) response.put("message", message);
        if (error != null) response.put("error", error);
        if (status != null) response.put("status", status);
        return response;
    }

}
