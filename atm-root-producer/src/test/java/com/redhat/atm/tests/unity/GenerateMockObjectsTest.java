package com.redhat.atm.tests.unity;

import com.github.javafaker.Faker;
import com.redhat.atm.model.*;
import com.redhat.atm.model.ed254.ArrivalSequence;
import com.redhat.atm.model.ed254.Entry;
import com.redhat.atm.model.ed254.TopicType;
import com.redhat.atm.repository.ArrivalSequenceRepository;
import com.redhat.atm.service.ArrivalSequenceProducer;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.junit.jupiter.api.*;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Validates the generation of arrival orders")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GenerateMockObjectsTest {

    private static ArrivalSequenceRepository arrivalSequenceRepository;
    private static ArrivalSequenceProducer arrivalSequenceProducer;
    private static Faker faker;
    private static TopicType topicType;

    @BeforeAll
    public static void prepare() {
        arrivalSequenceRepository = new ArrivalSequenceRepository();
        arrivalSequenceProducer = new ArrivalSequenceProducer(arrivalSequenceRepository);
        faker = new Faker();
        topicType = TopicType.values()[faker.number().numberBetween(0, TopicType.values().length)];
    }


    @Test
    @Order(1)
    @DisplayName("When a new arrival order list is generated")
    public void givenDefaultConfiguration_thenGenerateSingleObject() throws JAXBException {
        //ensure no list is present before generation
        assertThat(arrivalSequenceRepository.getArrivalSequences().isEmpty(), is(true));

        ArrivalSequence arrivalSequence = arrivalSequenceProducer.generateArrivalSequence(topicType);

        //ensures that there is at least one item in the list
        assertThat(arrivalSequenceRepository.getArrivalSequences().isEmpty(), is(false));

        JAXBContext context = JAXBContext.newInstance(ArrivalSequence.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(arrivalSequence, System.out);
    }

    @Test
    @Order(2)
    public void givenCollectionOfEntries_thenRotateTheFirstMember() throws JAXBException {
        ArrivalSequenceQueueModel asm = arrivalSequenceRepository.getArrivalSequences().get(topicType);
        assertThat(asm, notNullValue());

        int originalListSize = asm.getArrivalSequenceEntries().size();
        Entry firstEntry = asm.getArrivalSequenceEntries().peek();

        Entry lastEntry = asm.getArrivalSequenceEntries().stream().max(new EntryHandlingSeqComparator()).get();
        BigInteger lastEntryOriginalSeqValue = lastEntry.getHandling().getSeqnr();

        assertThat(firstEntry, notNullValue());

        // Check that the first item in the list must have the sequence 1
        assertThat(firstEntry.getHandling().getSeqnr().intValue(), is(1));

        ArrivalSequence rotateArrivalSequence = arrivalSequenceProducer.rotateArrivalSequence(topicType);
        assertThat(rotateArrivalSequence, notNullValue());
        Entry rotatedEntry = asm.getArrivalSequenceEntries().peek();

        // Check that the new first item in the list must have the sequence 1
        assertThat(rotatedEntry.getHandling().getSeqnr().intValue(), is(1));

        //Checks if the new first item is different from the previous one
        assertThat(new EntryComparator().compare(rotatedEntry,firstEntry), is(not(equalTo(0))));

        //Checks if the list continues with the same number of items
        assertThat(asm.getArrivalSequenceEntries().size(), is(originalListSize));

        //Checks if the last original item now has the second to last item in the list
        assertThat(lastEntry.getHandling().getSeqnr(),is(not(equalTo(lastEntryOriginalSeqValue))));
        assertThat(lastEntry.getHandling().getSeqnr(),is(equalTo(lastEntryOriginalSeqValue.subtract(BigInteger.valueOf(1)))));
    }


}
