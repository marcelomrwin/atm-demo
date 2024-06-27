package com.redhat.atm.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

public class PropertiesWithoutComments extends Properties {
    @Override
    public void store(Writer writer, String comments) throws IOException {
        super.store(writer, null);
        StringWriter stringWriter = (StringWriter) writer;
        StringBuffer buffer = stringWriter.getBuffer();
        int commentEndIndex = buffer.indexOf("\n") + 1;
        buffer.delete(0, commentEndIndex);
    }
}
