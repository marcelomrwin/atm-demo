package com.redhat.atm.model.ed254;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Point(LocalDateTime aptto, String pfl, Integer ttl, Integer order, String pointName, String pointUsage) {
}
