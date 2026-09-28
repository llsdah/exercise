package com.example.scheduler.dependency.domain;
import java.util.*;
/** Same tenant/group names. Newlines are reserved for durable encoding. */
public final class DependencyNames {
    private DependencyNames() { }
    public static List<String> normalize(List<String> names) {
        if (names == null) return List.of();
        var result = new LinkedHashSet<String>();
        for (String name : names) {
            if (name == null || name.isBlank() || name.length() > 200 || name.contains("\n") || name.contains("\r"))
                throw new InvalidDependencyException("dependsOn must contain nonblank Job names of at most 200 characters");
            result.add(name);
        }
        return List.copyOf(result);
    }
    public static String encode(List<String> names) { return String.join("\n", normalize(names)); }
    public static List<String> decode(String value) { return value == null || value.isEmpty() ? List.of() : List.of(value.split("\n")); }
}
