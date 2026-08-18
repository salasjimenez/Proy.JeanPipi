package com.jeanpipi.util;

// Adaptador temporal para Gson.
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.function.Function;

public final class TimeAdapter<T> extends TypeAdapter<T> {
    private final Function<String, T> parser;

    public TimeAdapter(Function<String, T> parser) {
        this.parser = parser;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return parser.apply(in.nextString());
    }
}
