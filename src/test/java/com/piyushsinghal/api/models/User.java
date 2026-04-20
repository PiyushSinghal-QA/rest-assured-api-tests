package com.piyushsinghal.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User POJO - represents a user in the ReqRes API.
 *
 * Using Lombok (@Data, @Builder) to avoid 50 lines of getters/setters boilerplate.
 * @JsonInclude.NON_NULL ensures null fields aren't serialized (cleaner payloads).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Integer id;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar;
    private String name;     // Used by POST /users endpoint
    private String job;      // Used by POST /users endpoint
    private String createdAt;
    private String updatedAt;
}
