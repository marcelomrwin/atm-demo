package com.redhat.atm;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsConstants;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.DataFormat;


@ApplicationScoped
public class MessageExportRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        DataFormat jaxb = new JaxbDataFormat("com.redhat.atm.ed254");
        from("activemq:queue:{{amq.queue}}")
                .id("activemq")
                .log("The route received XML: ${body}\n${headers}")
                .unmarshal(jaxb)
                .marshal().json(JsonLibrary.Jackson)
                .removeHeaders("*", JmsConstants.JMS_HEADER_CORRELATION_ID)
                .setHeader("Content-Type", constant("application/json"))
                .setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http.HttpMethods.POST))
                .setHeader(Exchange.HTTP_QUERY, simple("correlationId=${headers.JMS_HEADER_CORRELATION_ID}"))
                .process((exchange -> exchange.setProperty(Exchange.CHARSET_NAME, "UTF-8")))
                .toD("{{application.callback.url}}")
        ;
    }
}
