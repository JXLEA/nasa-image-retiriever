package ua.jxlea.integrationcamel.route.processor;

import jakarta.jms.DeliveryMode;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsMessageType;
import org.springframework.stereotype.Component;
import ua.jxlea.integrationcamel.dto.request.RequestDto;
import ua.jxlea.integrationcamel.route.enums.ProcessorRoute;

@Component
public class CameraProcessorRoute extends RouteBuilder {

    @Override
    public void configure() {
        from(ProcessorRoute.CAMERA.getUri())
                .routeId(ProcessorRoute.CAMERA.getRouteId())
                .process(setCamera())
                .process(exchange -> log.info("Getting Camera information from {}, {}", exchange.getFromRouteId(), exchange.getIn().getBody()))
                .marshal().json()
                .convertBodyTo(String.class)
                .setHeader("JMSType", constant(JmsMessageType.Text))
                .setHeader("JMSDeliveryMode", constant(DeliveryMode.NON_PERSISTENT))
                .to("activemq:queue:cameras?jmsMessageType=Object");
    }

    private Processor setCamera() {
        return exchange -> {
            var camera = ((RequestDto) exchange.getIn().getBody()).camera();
            exchange.getIn().setBody(camera);
        };
    }
}
