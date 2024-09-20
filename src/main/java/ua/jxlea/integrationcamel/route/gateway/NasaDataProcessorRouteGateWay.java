package ua.jxlea.integrationcamel.route.gateway;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import ua.jxlea.integrationcamel.route.enums.PhotoProducerRoute;
import ua.jxlea.integrationcamel.route.enums.ProcessorRoute;

@Component
public class NasaDataProcessorRouteGateWay extends RouteBuilder {

    @Override
    public void configure() {
        from(PhotoProducerRoute.DIRECT_GATE_WAY.getUri())
                .routeId(PhotoProducerRoute.DIRECT_GATE_WAY.getRouteId())
                .process(exchange -> log.info("Getting RequestData from {}, {}", exchange.getFromRouteId(), exchange.getIn().getBody()))
                .multicast().parallelProcessing()
                .to(ProcessorRoute.CAMERA.getUri())
                .to(ProcessorRoute.PHOTO.getUri())
                .to(ProcessorRoute.ROVER.getUri());
    }
}
