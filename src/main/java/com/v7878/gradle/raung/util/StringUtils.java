package com.v7878.gradle.raung.util;

public final class StringUtils {
    public static String capitalize( String value) {
        if (value.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
    public static String computeTaskName(String first, String... parts) {
        StringBuilder b = new StringBuilder(first);
        for(String part:parts){
            b.append(capitalize(part));
        }
        return b.toString();
    }
}
