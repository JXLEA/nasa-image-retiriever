package ua.jxlea.integrationcamel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


public record PhotoDto(
        Long id,
        Long cameraId,
        String imgSrc,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        Date createdAt
) {
}
