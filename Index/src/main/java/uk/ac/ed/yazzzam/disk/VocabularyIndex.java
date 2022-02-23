package uk.ac.ed.yazzzam.disk;


import java.util.Map;

public class VocabularyIndex {
    private static VocabularyIndex instance;

    private static Map<String, Integer> termOffsets;

    public VocabularyIndex() {

    }

    public static VocabularyIndex getInstance() {
        if (instance == null) {
            instance = new VocabularyIndex();
        }
        return instance;
    }
}
