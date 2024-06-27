package com.redhat.atm.model;

import com.redhat.atm.model.ed254.Entry;

import java.util.Comparator;

public class EntryHandlingSeqComparator implements Comparator<Entry> {

    @Override
    public int compare(Entry a, Entry b) {
        return a.getHandling().getSeqnr().compareTo(b.getHandling().getSeqnr());
    }
}
