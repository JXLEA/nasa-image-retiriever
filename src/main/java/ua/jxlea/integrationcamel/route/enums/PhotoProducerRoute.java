package ua.jxlea.integrationcamel.route.enums;

import lombok.Getter;

@Getter
public enum PhotoProducerRoute {

    DIRECT_PHOTO("direct:nasa-photo-producer", "nasa-photo-producer"),
    DIRECT_GATE_WAY("direct:photo-processor-gateway", "photo-processor-gateway"),
    DIRECT_NASA_API("direct:nasa-api-route", "nasa-api-route");

    public final String uri;
    private final String routeId;

    PhotoProducerRoute(String uri, String routeId) {
        this.uri = uri;
        this.routeId = routeId;
    }

}
