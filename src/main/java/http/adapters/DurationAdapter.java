package http.adapters;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(duration.toString());
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String value = in.nextString();
        if (value.isBlank()) {
            return null;
        }
        if (value.matches("\\d+")) {
            return Duration.ofMinutes(Long.parseLong(value));
        }
        try {
            return Duration.parse(value);
        } catch (Exception e) {
            throw new JsonParseException("Невозможно распарсить Duration: " + value, e);
        }
    }
}
