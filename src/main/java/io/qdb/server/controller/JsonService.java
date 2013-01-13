package io.qdb.server.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.google.inject.Inject;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;

/**
 * Marshaling of objects to/from JSON using Jackson.
 */
@Singleton
public class JsonService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectMapper mapperNoIdentOutput;

    @Inject
    @SuppressWarnings("deprecation")
    public JsonService(@Named("prettyPrint") boolean prettyPrint) {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

        if (prettyPrint) {
            mapperNoIdentOutput = mapper.copy();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        } else {
            mapperNoIdentOutput = mapper;
        }
    }

    /**
     * Convert o to JSON.
     */
    public byte[] toJson(Object o) throws IOException {
        return mapper.writeValueAsBytes(o);
    }

    /**
     * Convert o to JSON with no indenting.
     */
    public byte[] toJsonNoIndenting(Object o) throws IOException {
        return mapperNoIdentOutput.writeValueAsBytes(o);
    }

    /**
     * Converts content to an instance of a particular type.
     */
    public <T> T fromJson(InputStream ins, Class<T> klass) throws IOException {
        return mapper.readValue(ins, klass);
    }
}