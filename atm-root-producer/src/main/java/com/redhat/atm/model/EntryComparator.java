package com.redhat.atm.model;

import com.redhat.atm.model.ed254.Entry;

import java.util.Comparator;

public class EntryComparator implements Comparator<Entry> {
    @Override
    public int compare(Entry o1, Entry o2) {
        int compareResult = o1.getFlightID().getArcid().compareTo(o2.getFlightID().getArcid());
        if (compareResult != 0) return compareResult;

        compareResult = o1.getFlightID().getAdep().compareTo(o2.getFlightID().getAdep());
        if (compareResult != 0) return compareResult;

        compareResult = o1.getFlightID().getAdes().compareTo(o2.getFlightID().getAdes());
        if (compareResult != 0) return compareResult;

        compareResult = o1.getAssignedArrRwy().getName().compareTo(o2.getAssignedArrRwy().getName());
        if (compareResult != 0) return compareResult;

        return o1.getAircraft().getArctyp().compareTo(o2.getAircraft().getArctyp());
    }
}
