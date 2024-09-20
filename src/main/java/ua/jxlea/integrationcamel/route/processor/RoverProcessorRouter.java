package ua.jxlea.integrationcamel.route.processor;

import jakarta.jms.DeliveryMode;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsMessageType;
import org.springframework.stereotype.Component;
import ua.jxlea.integrationcamel.dto.request.RequestDto;
import ua.jxlea.integrationcamel.route.enums.ProcessorRoute;

@Component
public class RoverProcessorRouter extends RouteBuilder {

    @Override
    public void configure() {
        from(ProcessorRoute.ROVER.getUri())
                .routeId(ProcessorRoute.ROVER.getRouteId())
                .process(setRover())
                .process(exchange -> log.info("Getting Rover information from {}, {}", exchange.getFromRouteId(), exchange.getIn().getBody()))
                .marshal().json()
                .convertBodyTo(String.class)
                .setHeader("JMSType", constant(JmsMessageType.Object))
                .setHeader("JMSDeliveryMode", constant(DeliveryMode.NON_PERSISTENT))
                .to("activemq:queue:rovers?jmsMessageType=Object");
    }

    private Processor setRover() {
        return exchange -> {
            var rover = ((RequestDto) exchange.getIn().getBody()).rover();
            exchange.getIn().setBody(rover);
        };
    }
}
