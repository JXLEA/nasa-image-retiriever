package ua.jxlea.integrationcamel.route;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.*;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.apache.logging.log4j.util.Strings;
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

import java.nio.file.Files;
import java.nio.file.Path;

import static org.apache.camel.language.constant.ConstantLanguage.constant;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnableAutoConfiguration
@CamelSpringBootTest
@UseAdviceWith
@MockEndpointsAndSkip("direct:photo-processor-gateway")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NasaPhotoRetrieverRouteTest {

    @Autowired
    protected FluentProducerTemplate producerTemplate;

    @Autowired
    protected CamelContext context;

    @Autowired
    protected ObjectMapper mapper;

    @EndpointInject("mock:direct:photo-processor-gateway")
    MockEndpoint mockedOutputEndpoint;

    private String photoDataJson;
    private RequestDto photo;

    @BeforeEach
    public void initialize() throws Exception {
        photoDataJson = Files.readString(Path.of("src/test/resources/mock/nasa-image-data/RoverCuriosityPhoto.json"));
        photo = mapper.readValue(photoDataJson, RequestWrapperDto.class).photos().getFirst();
    }

    @Test
    @DisplayName("Route nasa-photo-producer process data correctly")
    public void testNasaPhotoProducerCorrectlyPathDataThough() throws Exception {

        AdviceWith.adviceWith(context, PhotoProducerRoute.DIRECT_PHOTO.getRouteId(), routeBuilder ->
                routeBuilder.interceptSendToEndpoint(PhotoProducerRoute.DIRECT_NASA_API.getUri())
                        .skipSendToOriginalEndpoint()
                        .to("mock:direct:nasa-api-route")
                        .setBody(new ConstantExpression(photoDataJson)));

        mockedOutputEndpoint.expectedMessageCount(1);
        mockedOutputEndpoint.expectedBodiesReceived(photo);

        context.start();

        var responseData = producerTemplate.to(PhotoProducerRoute.DIRECT_PHOTO.getUri()).request(Exchange.class);

        mockedOutputEndpoint.assertIsSatisfied();

        assertEquals(photo, responseData.getIn().getBody(RequestDto.class));
    }


    @Test
    @DisplayName("Route nasa-photo-producer doesnt process data if http response is empty")
    public void testNasaPhotoProducerDoNotProcessDataIfEmpty() throws Exception {

        AdviceWith.adviceWith(context, PhotoProducerRoute.DIRECT_PHOTO.getRouteId(), routeBuilder ->
                routeBuilder.interceptSendToEndpoint(PhotoProducerRoute.DIRECT_NASA_API.getUri())
                        .skipSendToOriginalEndpoint()
                        .to("mock:nasa-api-route")
                        .setBody(constant(Strings.EMPTY)));

        mockedOutputEndpoint.expectedMessageCount(0);
        context.start();

        var responseData = producerTemplate.to(PhotoProducerRoute.DIRECT_PHOTO.getUri()).request(Exchange.class);

        mockedOutputEndpoint.assertIsSatisfied();

        assertEquals(Strings.EMPTY, responseData.getIn().getBody());
    }
}