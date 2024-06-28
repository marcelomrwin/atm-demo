package com.redhat.atm.model.ed254;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Entry(FlightID flightID, List<Point> point, LocalDateTime atldt, AssignedArrRwy assignedArrRwy,
                    Aircraft aircraft, Handling handling) {

}
