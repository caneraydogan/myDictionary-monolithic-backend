package com.caner.bean;

public enum EntryLanguage {
    DE, EN;

    public static boolean isValid(String candidate) {
        for (EntryLanguage c : EntryLanguage.values()) {
            if (c.name().equals(candidate)) {
                return true;
            }
        }

        return false;
    }
}
