package uk.ac.ed.yazzzam.Preprocessor;

import java.util.List;

public interface Preprocessor {
    List<String> preprocess(String text);
    List<String> preprocess(String text, String wordReducAlg);
}
