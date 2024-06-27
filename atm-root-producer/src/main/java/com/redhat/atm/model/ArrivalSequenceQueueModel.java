package com.redhat.atm.model;

import com.redhat.atm.model.ed254.ArrivalSequence;
import com.redhat.atm.model.ed254.Entry;
import com.redhat.atm.model.ed254.TopicType;
import lombok.Builder;
import lombok.Data;

import java.util.PriorityQueue;
import java.util.Queue;

@Data
@Builder
public class ArrivalSequenceQueueModel {
    private Integer maxArrivalSequence;
    private TopicType topic;
    private final Queue<Entry> arrivalSequenceEntries = new PriorityQueue<>(new EntryHandlingSeqComparator());
    private ArrivalSequence arrivalSequence;
}
