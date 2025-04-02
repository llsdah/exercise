package com.fastcampus.sns.util;

import java.util.Optional;

/**
 * 안정적은 Class Casting을 위한 클래스
 */
public class ClassUtils {
    public static <T>Optional<T> getSafeCastInstance(Object o, Class<T> clazz){
        return clazz != null &&
                clazz.isInstance(o) ? Optional.of(clazz.cast(o)) : Optional.empty();
    }
}
