package com.redhat.atm.dto;

public record EntryDTO(String arcid, String arctyp, Integer seqnr) implements Comparable<EntryDTO> {
    @Override
    public int compareTo(EntryDTO o) {
        return this.seqnr.compareTo(o.seqnr);
    }
}
