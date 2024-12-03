package org.assignment.wallet.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.assignment.wallet.exception.InvalidRequestArgumentException;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public Class<BigDecimal> handledType() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String rawValue = p.getText();
        if (!rawValue.matches("\\d+\\.\\d{2}")) {
            throw new InvalidRequestArgumentException(
                "Сумма должна содержать точно 2 знака после запятой"
            );
        }
        return new BigDecimal(rawValue);
    }
}
