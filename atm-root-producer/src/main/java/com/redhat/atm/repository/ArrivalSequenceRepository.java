package com.redhat.atm.repository;

import com.redhat.atm.model.ArrivalSequenceQueueModel;
import com.redhat.atm.model.ed254.TopicType;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Getter
public class ArrivalSequenceRepository {
    private final Map<TopicType, ArrivalSequenceQueueModel> arrivalSequences = new HashMap<>();
}
