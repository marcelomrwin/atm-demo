package com.redhat.atm;

import com.redhat.atm.dto.ArrivalSequenceDTO;
import com.redhat.atm.dto.EntryDTO;
import com.redhat.atm.ed254.ArrivalSequence;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.component.jms.JmsConstants;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.DataFormat;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;


@ApplicationScoped
public class MessageExportRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        DataFormat jaxb = new JaxbDataFormat("com.redhat.atm.ed254");
        from("activemq:queue:{{amq.queue}}")
                .id("activemq")
                .unmarshal(jaxb)
//                .log("Event arrived ${body}")
                /*

                .process((exchange) -> {
                    ArrivalSequence arrivalSequence = exchange.getIn().getBody(ArrivalSequence.class);

                    SortedSet<EntryDTO> entryDTOS = arrivalSequence.getEntry().stream().map(entry -> {
                        return new EntryDTO(entry.getFlightID().getArcid(), entry.getAircraft().getArctyp(), entry.getHandling().getSeqnr().intValue());
                    }).collect(Collectors.toCollection(TreeSet::new));

                    ArrivalSequenceDTO dto = getArrivalSequenceDTO(arrivalSequence, entryDTOS);
                    exchange.getIn().setBody(dto);
                })

                 */

                .marshal().json(JsonLibrary.Jackson)
                .removeHeaders("*", JmsConstants.JMS_HEADER_CORRELATION_ID)
                .setHeader("Content-Type", constant("application/json"))
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                .setHeader(Exchange.HTTP_QUERY, simple("correlationId=${headers.JMSCorrelationID}"))
                .process((exchange -> exchange.setProperty(Exchange.CHARSET_NAME, "UTF-8")))
//                .log("Event Sent: ${body}")
                .toD("{{application.callback.url}}")
        ;
    }

//    private static ArrivalSequenceDTO getArrivalSequenceDTO(ArrivalSequence arrivalSequence, SortedSet<EntryDTO> entryDTOS) {
//        XMLGregorianCalendar calendar = arrivalSequence.getMessageMeta().getPublicationTime();
//        LocalDateTime localDateTime = LocalDateTime.of(calendar.getYear(), calendar.getMonth(), calendar.getDay(), calendar.getHour(), calendar.getMinute(), calendar.getSecond()).atOffset(ZoneOffset.UTC).toLocalDateTime();
//
//        return new ArrivalSequenceDTO(arrivalSequence.getTopic().value(), arrivalSequence.getMessageMeta().getManagedAerodrome(), localDateTime, entryDTOS);
//    }
}
