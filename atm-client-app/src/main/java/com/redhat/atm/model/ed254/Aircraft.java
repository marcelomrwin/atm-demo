package com.redhat.atm.model.ed254;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Aircraft(String arctyp, String wktrc) {

}
