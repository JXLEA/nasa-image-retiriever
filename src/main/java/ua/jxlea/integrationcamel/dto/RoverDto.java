package ua.jxlea.integrationcamel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RoverDto(
        Long id,
        String name,
        @JsonProperty("landing_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date landingDate,
        @JsonProperty("launch_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date launchDate,
        String status,
        @JsonProperty("max_sol")
        Long maxSol,
        @JsonProperty("max_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date maxDate,
        @JsonProperty("totalPhotos")
        Long total_photos,
        List<CameraDto> cameras
) {
}
