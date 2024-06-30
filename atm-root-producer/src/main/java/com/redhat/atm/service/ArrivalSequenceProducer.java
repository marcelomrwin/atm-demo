package com.redhat.atm.service;

import com.github.javafaker.Faker;
import com.redhat.atm.model.EntryHandlingSeqComparator;
import com.redhat.atm.model.*;
import com.redhat.atm.model.ed254.*;
import com.redhat.atm.repository.ArrivalSequenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.api.Randomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ArrivalSequenceProducer {

    private static final Faker faker = new Faker();
    private final ArrivalSequenceRepository arrivalSequenceRepository;

    @Autowired
    public ArrivalSequenceProducer(ArrivalSequenceRepository arrivalSequenceRepository) {
        this.arrivalSequenceRepository = arrivalSequenceRepository;
    }

    @Async
    public CompletableFuture<ArrivalSequence> generateArrivalSequenceAsync(TopicType topicType) {
        ArrivalSequenceQueueModel asm = arrivalSequenceRepository.getArrivalSequences().getOrDefault(topicType, generateArrivalSequenceQueueModel(topicType));
        arrivalSequenceRepository.getArrivalSequences().put(topicType, asm);
        return CompletableFuture.completedFuture(asm.getArrivalSequence());
    }

    public ArrivalSequence generateArrivalSequence(TopicType topicType) {
        ArrivalSequenceQueueModel asm = arrivalSequenceRepository.getArrivalSequences().getOrDefault(topicType, generateArrivalSequenceQueueModel(topicType));
        arrivalSequenceRepository.getArrivalSequences().put(topicType, asm);
        return asm.getArrivalSequence();
    }

    public ArrivalSequence getArrivalSequence(TopicType topicType) {
        return arrivalSequenceRepository.getArrivalSequences().get(topicType).getArrivalSequence();
    }

    public ArrivalSequence rotateArrivalSequence(TopicType topicType) {
        log.info("Rotating arrival sequence for {}", topicType);
        if (!arrivalSequenceRepository.getArrivalSequences().containsKey(topicType))
            throw new RuntimeException("Key " + topicType + " not found for rotate");

        ArrivalSequenceQueueModel asm = arrivalSequenceRepository.getArrivalSequences().get(topicType);
        Entry removedEntry = asm.getArrivalSequenceEntries().poll();
        Integer seqIndex = removedEntry.getHandling().getSeqnr().intValue();
        asm.getArrivalSequenceEntries().stream().forEachOrdered(entry -> entry.getHandling().setSeqnr(entry.getHandling().getSeqnr().subtract(BigInteger.valueOf(1))));
        generateSequenceEntriesInformation(topicType, 1, asm.getArrivalSequenceEntries());

        asm.getArrivalSequence().getEntry().clear();
        asm.getArrivalSequence().getEntry().addAll(asm.getArrivalSequenceEntries().stream().sorted(new EntryHandlingSeqComparator()).toList());

        return asm.getArrivalSequence();
    }

    private ArrivalSequenceQueueModel generateArrivalSequenceQueueModel(TopicType topicType) {
        log.info("Generating arrival sequence Model for topic {}", topicType);
        ArrivalSequenceQueueModel asm = ArrivalSequenceQueueModel.builder().maxArrivalSequence(faker.number().numberBetween(6, 11)).topic(topicType).build();

        ArrivalSequence arrivalSequence = new ArrivalSequence();
        arrivalSequence.setTopic(asm.getTopic());

        MessageMeta messageMeta = new MessageMeta();
        messageMeta.setCreationTime(getRandomXMLGregorianCalendar());
        messageMeta.setPublicationTime(getRandomXMLGregorianCalendar());
        messageMeta.setIsFirstAfterOutage(faker.random().nextBoolean());
        messageMeta.setManagedAerodrome(generateRandomString(4, true));
        arrivalSequence.setMessageMeta(messageMeta);

        generateSequenceEntriesInformation(topicType, asm.getMaxArrivalSequence(), asm.getArrivalSequenceEntries());

        arrivalSequence.getEntry().addAll(asm.getArrivalSequenceEntries());
        asm.setArrivalSequence(arrivalSequence);
        log.debug("Arrival sequence model generated: {}", asm);
        return asm;
    }

    private void generateSequenceEntriesInformation(TopicType topicType, int numberOfSequences, Queue<Entry> sequenceEntries) {
        log.info("Generating sequence Entries {} for topic {}", numberOfSequences, topicType);
        for (int i = 0; i < numberOfSequences; i++) {
            Entry entry = new Entry();

            FlightID flightID = new FlightID();
            flightID.setAdep(generateRandomString(4, true));
            flightID.setAdes(generateRandomString(4, true));
            flightID.setArcid(generateAlphaNumericString(3, 4));
            flightID.setEobt(getRandomXMLGregorianCalendar());
            entry.setFlightID(flightID);

            generateEntryPoints(faker.number().numberBetween(1, 5), entry.getPoint());

            AssignedArrRwy assignedArrRwy = new AssignedArrRwy();
            assignedArrRwy.setName(generateAlphaNumericString(1, 2));
            entry.setAssignedArrRwy(assignedArrRwy);

            Aircraft aircraft = new Aircraft();
            aircraft.setArctyp(faker.aviation().aircraft());
            aircraft.setWktrc(generateRandomString(1, true));
            entry.setAircraft(aircraft);

            Handling handling = new Handling();
            handling.setIsPriority(faker.random().nextBoolean());
            handling.setIsLastRecord(faker.random().nextBoolean());

            if (sequenceEntries.isEmpty()) {
                handling.setSeqnr(BigInteger.valueOf(1));
            } else {
                Optional<BigInteger> maxSeq = sequenceEntries.stream().max(new EntryHandlingSeqComparator()).map((max) -> max.getHandling().getSeqnr());
                handling.setSeqnr(maxSeq.get().add(BigInteger.valueOf(1)));
            }
            log.info("Handling sequence is {}, for topic {}", handling.getSeqnr(), topicType);
            entry.setHandling(handling);
            sequenceEntries.add(entry);
        }
    }

    private void generateEntryPoints(int numberOfPoints, List<Point> points) {
        Point point = new Point();
        point.setPointName(generateRandomString(5, true));
        point.setOrder(BigInteger.valueOf(faker.number().numberBetween(1, numberOfPoints)));
        point.setPointUsage(PointUsageType.values()[faker.number().numberBetween(0, PointUsageType.values().length)]);
        point.setAptto(getRandomXMLGregorianCalendar());
        point.setTtl(BigInteger.valueOf(faker.number().numberBetween(1, 100)));
        point.setPfl(generateAlphaNumericString(2, 3));
        points.add(point);
    }


    private String generateAlphaNumericString(int alphaLength, int numericLength) {
        sleep(100, TimeUnit.MILLISECONDS);
        String alphaPart = generateRandomString(alphaLength, true);
        for (int i = 0; i < numericLength; i++) {
            alphaPart += faker.number().numberBetween(0, 9);
        }
        return shuffleString(alphaPart);
    }

    private void sleep(long duration, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private static LocalDateTime generateRandomLocalDateTime() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.seed(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + +faker.number().numberBetween(1, 100));
        parameters.dateRange(LocalDate.now(), LocalDate.now().plusDays(1));
        parameters.timeRange(LocalTime.MIN, LocalTime.MAX);
        EasyRandom easyRandom = new EasyRandom(parameters);
        return easyRandom.nextObject(LocalDateTime.class);
    }

    private String generateRandomString(int numberOfCharacters, boolean isUpperCase) {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.seed(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + faker.number().numberBetween(1, 100));
        parameters.setCharset(StandardCharsets.UTF_8);
        parameters.setStringLengthRange(new EasyRandomParameters.Range(numberOfCharacters, numberOfCharacters));
        EasyRandom easyRandom = new EasyRandom(parameters);
        String randomString = easyRandom.nextObject(String.class);
        if (isUpperCase) return randomString.toUpperCase();
        return randomString;
    }

    private XMLGregorianCalendar getRandomXMLGregorianCalendar() {
        return new XMLGregorianCalendarGenerator().getRandomValue();
    }


    private static class XMLGregorianCalendarGenerator implements Randomizer<XMLGregorianCalendar> {

        @Override
        public XMLGregorianCalendar getRandomValue() {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            LocalDateTime randomLocalDateTime = generateRandomLocalDateTime();
            gregorianCalendar.set(Calendar.YEAR, randomLocalDateTime.getYear());
            gregorianCalendar.set(Calendar.MONTH, randomLocalDateTime.getMonthValue() - 1);
            gregorianCalendar.set(Calendar.DAY_OF_MONTH, randomLocalDateTime.getDayOfMonth());
            gregorianCalendar.set(Calendar.HOUR_OF_DAY, randomLocalDateTime.getHour());
            gregorianCalendar.set(Calendar.MINUTE, randomLocalDateTime.getMinute());
            gregorianCalendar.set(Calendar.SECOND, randomLocalDateTime.getSecond());
            gregorianCalendar.set(Calendar.MILLISECOND, randomLocalDateTime.getNano());
            gregorianCalendar.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
            try {
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String shuffleString(String input) {

        // Convert String to a list of Characters
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }

        // Shuffle the list
        Collections.shuffle(characters);

        // Convert the list back to String
        StringBuilder shuffledString = new StringBuilder();
        for (char c : characters) {
            shuffledString.append(c);
        }

        return shuffledString.toString();
    }
}
