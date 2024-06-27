package com.redhat.atm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.redhat.atm.model.ed254.TopicType;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SubscriptionRequest(List<TopicType> topicTypes) {
}
