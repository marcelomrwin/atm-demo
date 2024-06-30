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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Validates the generation of arrival orders")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GenerateMockObjectsTest {

    private static ArrivalSequenceRepository arrivalSequenceRepository;
    private static ArrivalSequenceProducer arrivalSequenceProducer;
    private static Faker faker;
    private static TopicType topicType;
    private static Logger logger = LoggerFactory.getLogger(GenerateMockObjectsTest.class);

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

        ArrivalSequence arrivalSequence = asm.getArrivalSequence();
        Map<String, Integer> sequence = new HashMap<>();
        for (Entry entry : arrivalSequence.getEntry())
            sequence.put(entry.getFlightID().getArcid(), entry.getHandling().getSeqnr().intValue());

        ArrivalSequence rotateArrivalSequence = arrivalSequenceProducer.rotateArrivalSequence(topicType);
        assertThat(rotateArrivalSequence, notNullValue());
        Entry rotatedEntry = asm.getArrivalSequenceEntries().peek();

        // Check that the new first item in the list must have the sequence 1
        assertThat(rotatedEntry.getHandling().getSeqnr().intValue(), is(1));

        //Checks if the new first item is different from the previous one
        assertThat(new EntryComparator().compare(rotatedEntry, firstEntry), is(not(equalTo(0))));

        //Checks if the list continues with the same number of items
        assertThat(asm.getArrivalSequenceEntries().size(), is(originalListSize));

        //Checks if the last original item now has the second to last item in the list
        assertThat(lastEntry.getHandling().getSeqnr(), is(not(equalTo(lastEntryOriginalSeqValue))));
        assertThat(lastEntry.getHandling().getSeqnr(), is(equalTo(lastEntryOriginalSeqValue.subtract(BigInteger.valueOf(1)))));

        Integer index = null;
        for (Entry entry : rotateArrivalSequence.getEntry()) {
            if (index == null) {
                assertThat(entry.getHandling().getSeqnr().intValue(), is(1));
            } else {
                assertThat(entry.getHandling().getSeqnr().intValue(), is(org.hamcrest.number.OrderingComparison.greaterThan(index)));
            }
            index = entry.getHandling().getSeqnr().intValue();

        }
        //Check if the new sequence removed only one item
        long foundEntries = 0;
        for (String key : sequence.keySet()) {
            foundEntries += rotateArrivalSequence.getEntry().stream().filter(entry -> entry.getFlightID().getArcid().equals(key)).count();
        }

        long entries = rotateArrivalSequence.getEntry().size() - foundEntries;
        assertThat(entries, is(lessThanOrEqualTo(1L)));

        //check new indexes
        for (String key : sequence.keySet()) {
            Optional<Entry> optional = rotateArrivalSequence.getEntry().stream().filter(entry -> entry.getFlightID().getArcid().equals(key)).findFirst();
            optional.ifPresentOrElse(entry -> assertThat("Check new sequence for " + key + ", original was " + sequence.get(key), entry.getHandling().getSeqnr().intValue(), is(equalTo(sequence.get(key) - 1))), () -> logger.info("Key {} not found", key));
        }

        //force rotation and check new index for last item
        final Entry maxEntry = asm.getArrivalSequenceEntries().stream().max(new EntryHandlingSeqComparator()).get();
        logger.info("Last Entry {} = {}", maxEntry.getFlightID().getArcid(), maxEntry.getHandling().getSeqnr());
        for (int i = 0; i < originalListSize - 1; i++) {
            rotateArrivalSequence = arrivalSequenceProducer.rotateArrivalSequence(topicType);
            Optional<Entry> optional = rotateArrivalSequence.getEntry().stream().filter(entry -> entry.getFlightID().getArcid().equals(maxEntry.getFlightID().getArcid())).findFirst();
            assertThat(optional.isPresent(), is(true));
            Entry entry = optional.get();
            assertThat("The flight " + entry.getFlightID().getArcid(), entry.getHandling().getSeqnr().intValue(), is(equalTo(originalListSize - (i + 1))));
        }
        //ensure that the maxEntry now is the first item in the queue
        assertThat(maxEntry.getHandling().getSeqnr().intValue(), is(1));

    }


}
