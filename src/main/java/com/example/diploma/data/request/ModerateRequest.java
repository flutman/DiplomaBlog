package com.example.diploma.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModerateRequest {
    @JsonProperty("post_id")
    private int postId;
    private String decision;
}
