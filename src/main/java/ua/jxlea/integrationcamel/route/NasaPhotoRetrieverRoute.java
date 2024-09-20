package ua.jxlea.integrationcamel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import ua.jxlea.integrationcamel.dto.request.RequestWrapperDto;
import ua.jxlea.integrationcamel.route.enums.PhotoProducerRoute;

@Component
public class NasaPhotoRetrieverRoute extends RouteBuilder {

    @Override
    public void configure() {
        from(PhotoProducerRoute.DIRECT_PHOTO.getUri())
                .routeId(PhotoProducerRoute.DIRECT_PHOTO.getRouteId())
                .to(PhotoProducerRoute.DIRECT_NASA_API.getUri())
                .unmarshal().json(RequestWrapperDto.class)
                .transform().simple("${body.photos}")
                .split(body())
                .to(PhotoProducerRoute.DIRECT_GATE_WAY.getUri());
    }
}
