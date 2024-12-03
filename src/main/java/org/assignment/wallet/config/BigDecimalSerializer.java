package org.assignment.wallet.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public Class<BigDecimal> handledType() {
        return BigDecimal.class;
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            BigDecimal scaledValue = value.setScale(2, RoundingMode.UNNECESSARY);
            gen.writeString(scaledValue.toPlainString());
        }
    }
}
