package ua.jxlea.integrationcamel.route.processor;

import jakarta.jms.DeliveryMode;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsMessageType;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;
import ua.jxlea.integrationcamel.dto.request.RequestDto;
import ua.jxlea.integrationcamel.dto.PhotoDto;
import ua.jxlea.integrationcamel.route.enums.ProcessorRoute;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

@Component
public class PhotoProcessorRoute extends RouteBuilder {

    @Override
    public void configure() {
        from(ProcessorRoute.PHOTO.getUri())
                .routeId(ProcessorRoute.PHOTO.getRouteId())
                .process(setPhoto())
                .process(exchange -> log.info("Getting Photo information from {}, {}", exchange.getFromRouteId(), exchange.getIn().getBody()))
                .marshal().json()
                .convertBodyTo(String.class)
                .setHeader("JMSType", constant(JmsMessageType.Object))
                .setHeader("JMSDeliveryMode", constant(DeliveryMode.NON_PERSISTENT))
                .to("activemq:queue:photos?jmsMessageType=Object");
    }

    private Processor setPhoto() {
        return exchange -> {
            var imageInfo = ((RequestDto) exchange.getIn().getBody());
            var cameraId = imageInfo.camera().id();
            var photoId = 1L; //new Random().nextLong(100);
            var photo = new PhotoDto(photoId, cameraId, imageInfo.imageSrc(), imageInfo.earthDate());
            exchange.getIn().setBody(photo);
        };
    }
}
