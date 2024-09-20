package ua.jxlea.integrationcamel.route.enums;

import lombok.Getter;

@Getter
public enum ProcessorRoute {

    CAMERA("direct:camera-processor", "camera-processor"),
    PHOTO("direct:photo-processor", "photo-processor"),
    ROVER("direct:rover-processor", "rover-processor");

    private final String uri;
    private final String routeId;

    ProcessorRoute(String uri, String routeId) {
        this.uri = uri;
        this.routeId = routeId;
    }
}
