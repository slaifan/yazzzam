package uk.ac.ed.yazzzam.Preprocessor;

import java.util.Arrays;
import java.util.List;

public class BasicPreprocessor implements Preprocessor {
    @Override
    public List<String> preprocess(String text) {
        return Arrays.asList(tokenize(caseFold(text)));
    }

    private String caseFold(String text) {
        return text.toLowerCase();
    }

    private String[] tokenize(String text) {
        return text.split("[^a-zA-Z0-9]+");
    }
}
