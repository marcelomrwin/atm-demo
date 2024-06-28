package com.redhat.atm.model.ed254;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageMeta(LocalDateTime creationTime,Boolean isFirstAfterOutage,String managedAerodrome,LocalDateTime publicationTime){

}