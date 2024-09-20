package ua.jxlea;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import ua.jxlea.integrationcamel.route.enums.PhotoProducerRoute;

@SpringBootApplication
@EnableJms
public class NasaImageRetrieverServiceApplication {

    public static void main(String[] args) {
        var appContext = SpringApplication.run(NasaImageRetrieverServiceApplication.class, args);
        var camelContext = (CamelContext) appContext.getBean("camelContext");

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.sendBody(PhotoProducerRoute.DIRECT_PHOTO.getUri(), null);
        producerTemplate.cleanUp();
    }
}

