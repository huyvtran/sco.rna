package it.smartcommunitylab.rna.beans;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class LocalDateDeserializer extends StdDeserializer<LocalDate> {
	private static final long serialVersionUID = 1L;

	protected LocalDateDeserializer() {
		super(LocalDate.class);
	}

	public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return Instant.ofEpochMilli(jp.getLongValue()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

}
