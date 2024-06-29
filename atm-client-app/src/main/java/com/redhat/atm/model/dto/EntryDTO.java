package com.redhat.atm.model.dto;

import java.time.LocalDateTime;

public record EntryDTO(String arcid, String arctyp, LocalDateTime eobt, Integer seqnr) implements Comparable<EntryDTO> {
    @Override
    public int compareTo(EntryDTO o) {
        return this.seqnr.compareTo(o.seqnr);
    }
}
