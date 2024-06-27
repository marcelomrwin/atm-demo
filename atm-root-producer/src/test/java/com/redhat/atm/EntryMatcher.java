package com.redhat.atm;

import com.redhat.atm.model.ed254.Entry;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class EntryMatcher extends TypeSafeMatcher<Entry> {
    @Override
    protected boolean matchesSafely(Entry item) {
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Checks if one Entry is equal to another");
    }
}
