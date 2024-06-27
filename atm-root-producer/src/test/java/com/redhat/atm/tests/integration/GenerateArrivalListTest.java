package com.redhat.atm.tests.integration;

import com.redhat.atm.repository.ArrivalSequenceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Validates the generation of arrival orders (integration)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GenerateArrivalListTest {
    @Autowired
    ArrivalSequenceRepository arrivalSequenceRepository;

    @Test
    public void check(){

    }
}
