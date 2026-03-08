package com.example.scheduler.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * DB의 'Y'/'N' (VARCHAR2) 컬럼과 Java의 Boolean 타입을 매핑하는 컨버터.
 * <p>
 * @Converter(autoApply = true) 를 사용하면 모든 Boolean 필드에 자동 적용되나,
 * 명시적인 설계를 위해 필요한 필드에만 @Convert를 지정하는 것을 권장합니다.
 */
@Converter
public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {

    /**
     * Java Boolean -> DB String ('Y' or 'N')
     */
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        // null이거나 false이면 "N", true이면 "Y"
        return (attribute != null && attribute) ? "Y" : "N";
    }

    /**
     * DB String ('Y' or 'N') -> Java Boolean
     */
    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        // null이거나 "Y"가 아니면 false, "Y"이면 true
        return "Y".equalsIgnoreCase(dbData);
    }
}