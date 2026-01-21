package com.roberto.interview.domain.converters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.AttributeConverter;

public class StringListConverter implements AttributeConverter<List<String>, String> {

  private static final String SEPARATOR = ",";

  @Override
  public String convertToDatabaseColumn(final List<String> attribute) {
    return Objects.isNull(attribute) ? "" : String.join(SEPARATOR, attribute);
  }

  @Override
  public List<String> convertToEntityAttribute(final String dbData) {
    return (Objects.isNull(dbData) || dbData.isEmpty()) ? Collections.emptyList() : Arrays.asList(dbData.split(SEPARATOR));
  }
}
