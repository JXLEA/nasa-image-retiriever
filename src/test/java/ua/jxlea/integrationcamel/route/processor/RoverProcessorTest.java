package ua.jxlea.integrationcamel.route.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ua.jxlea.integrationcamel.dto.RoverDto;
import ua.jxlea.integrationcamel.dto.request.RequestDto;
import ua.jxlea.integrationcamel.dto.request.RequestWrapperDto;
import ua.jxlea.integrationcamel.route.enums.ProcessorRoute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
@CamelSpringBootTest
@EnableAutoConfiguration
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RoverProcessorTest {

    private static final String JMS_ROUTE_URL = "activemq:queue:rovers?jmsMessageType=Object";

    @Autowired
    private FluentProducerTemplate producerTemplate;

    @Autowired
    private CamelContext context;

    @Autowired
    private ObjectMapper mapper;

    @EndpointInject("mock:activemq:queue:rovers?jmsMessageType=Object")
    private MockEndpoint mockActiveMq;

    private RequestDto requestDto;

    private String roverDtoJson;

    @BeforeEach
    public void init() throws IOException {
        var photoDataJson = Files.readString(Path.of("src/test/resources/mock/nasa-image-data/RoverCuriosityPhoto.json"));
        requestDto = mapper.readValue(photoDataJson, RequestWrapperDto.class).photos().getFirst();
        roverDtoJson = mapper.writeValueAsString(requestDto.rover());
    }

    @Test
    @DisplayName("Rover processor correctly retrieve Rover data")
    public void testRoverProcessorRetrieveRoverInfoCorrectly() throws Exception {
        AdviceWith.adviceWith(context, ProcessorRoute.ROVER.getRouteId(), routeBuilder -> {
            routeBuilder.interceptSendToEndpoint(JMS_ROUTE_URL)
                    .skipSendToOriginalEndpoint()
                    .to(STR."mock:\{JMS_ROUTE_URL}");
        });

        mockActiveMq.expectedMessageCount(1);
        mockActiveMq.expectedBodiesReceived(roverDtoJson);

        context.start();

        producerTemplate.to(ProcessorRoute.ROVER.getUri())
                .withBody(requestDto)
                .request(Exchange.class);

        mockActiveMq.assertIsSatisfied();
    }
}
