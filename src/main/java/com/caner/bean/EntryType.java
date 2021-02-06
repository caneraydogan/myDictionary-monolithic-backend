package com.caner.bean;

public enum EntryType {
    VERB, NOUN, ADVERB, ADJECTIVE;

    public static boolean isValid(String candidate) {
        for (EntryType c : EntryType.values()) {
            if (c.name().equals(candidate)) {
                return true;
            }
        }

        return false;
    }
}
