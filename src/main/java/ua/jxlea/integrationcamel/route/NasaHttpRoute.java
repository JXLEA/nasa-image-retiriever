package ua.jxlea.integrationcamel.route;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import ua.jxlea.integrationcamel.route.enums.PhotoProducerRoute;

@Component
public class NasaHttpRoute extends RouteBuilder {

    @Override
    public void configure() {
        from(PhotoProducerRoute.DIRECT_NASA_API.getUri())
                .routeId(PhotoProducerRoute.DIRECT_NASA_API.getRouteId())
                .setExchangePattern(ExchangePattern.InOut)
                .setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
                .to("https:{{nasa.url}}");
    }
}
