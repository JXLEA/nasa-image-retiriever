package ua.jxlea.integrationcamel.route.gateway;

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
import ua.jxlea.integrationcamel.dto.request.RequestDto;
import ua.jxlea.integrationcamel.dto.request.RequestWrapperDto;
import ua.jxlea.integrationcamel.route.enums.PhotoProducerRoute;
import ua.jxlea.integrationcamel.route.enums.ProcessorRoute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
@CamelSpringBootTest
@EnableAutoConfiguration
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NasaDataProcessorRouteGateWayTest {

    @Autowired
    private FluentProducerTemplate producerTemplate;

    @Autowired
    private CamelContext context;

    @Autowired
    private ObjectMapper mapper;

    @EndpointInject("mock:direct:camera-processor")
    private MockEndpoint mockCameraProcessor;

    @EndpointInject("mock:direct:photo-processor")
    private MockEndpoint mockPhotoProcessor;

    @EndpointInject("mock:direct:rover-processor")
    private MockEndpoint mockRoverProcessor;

    private RequestDto requestDto;

    @BeforeEach
    public void init() throws IOException {
        var photoDataJson = Files.readString(Path.of("src/test/resources/mock/nasa-image-data/RoverCuriosityPhoto.json"));
        requestDto = mapper.readValue(photoDataJson, RequestWrapperDto.class).photos().getFirst();
    }

    @Test
    @DisplayName("Test: Route Processor Gateway multicasted data by processors")
    public void testRouteProcessorReceivedAndMulticastedData() throws Exception {

        AdviceWith.adviceWith(context, PhotoProducerRoute.DIRECT_GATE_WAY.getRouteId(), routeBuilder -> {
            routeBuilder.interceptSendToEndpoint(ProcessorRoute.CAMERA.getUri())
                    .skipSendToOriginalEndpoint()
                    .to("mock:direct:camera-processor");

            routeBuilder.interceptSendToEndpoint(ProcessorRoute.PHOTO.getUri())
                    .skipSendToOriginalEndpoint()
                    .to("mock:direct:photo-processor");

            routeBuilder.interceptSendToEndpoint(ProcessorRoute.ROVER.getUri())
                    .skipSendToOriginalEndpoint()
                    .to("mock:direct:rover-processor");
        });

        mockCameraProcessor.expectedMessageCount(1);
        mockCameraProcessor.expectedBodiesReceived(requestDto);

        mockPhotoProcessor.expectedBodiesReceived(1);
        mockPhotoProcessor.expectedBodiesReceived(requestDto);

        mockRoverProcessor.expectedBodiesReceived(1);
        mockRoverProcessor.expectedBodiesReceived(requestDto);

        context.start();

        producerTemplate.to(PhotoProducerRoute.DIRECT_GATE_WAY.getUri())
                .withBody(requestDto)
                .request(Exchange.class);

        mockCameraProcessor.assertIsSatisfied();
        mockPhotoProcessor.assertIsSatisfied();
        mockRoverProcessor.assertIsSatisfied();
    }

}