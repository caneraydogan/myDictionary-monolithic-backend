package com.caner.bean;

public enum Artikel {
    DER, DIE, DAS;

    public static boolean isValid(String candidate) {
        for (Artikel c : Artikel.values()) {
            if (c.name().equals(candidate)) {
                return true;
            }
        }
        return false;
    }
}
