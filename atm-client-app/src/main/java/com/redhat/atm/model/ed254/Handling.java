package com.redhat.atm.model.ed254;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Handling(Integer seqnr,Boolean isLastRecord,Boolean isPriority,String status){

}
