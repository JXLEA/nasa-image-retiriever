package ua.jxlea.integrationcamel.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ua.jxlea.integrationcamel.dto.CameraDto;
import ua.jxlea.integrationcamel.dto.RoverDto;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RequestDto(
        Long id,
        Integer sol,
        CameraDto camera,
        @JsonProperty("img_src")
        String imageSrc,
        @JsonProperty("earth_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date earthDate,
        RoverDto rover
) {
}

