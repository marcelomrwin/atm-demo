package com.redhat.atm.model.ed254;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArrivalSequence(MessageMeta messageMeta, List<Entry> entry, String topic) {

}
