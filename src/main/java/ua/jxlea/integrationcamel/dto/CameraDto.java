package ua.jxlea.integrationcamel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CameraDto(
        Long id,
        String name,
        @JsonProperty("rover_id")
        Long roverId,
        @JsonProperty("full_name")
        String fullName
) {
}
