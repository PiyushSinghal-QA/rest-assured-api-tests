package com.piyushsinghal.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Response wrapper for paginated user list endpoints (GET /users).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserListResponse {
    private int page;
    private int per_page;
    private int total;
    private int total_pages;
    private List<User> data;
    private Support support;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Support {
        private String url;
        private String text;
    }
}
