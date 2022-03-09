package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.Preprocessor.BasicPreprocessor;
import uk.ac.ed.yazzzam.Preprocessor.FullPreprocessor;
import uk.ac.ed.yazzzam.Preprocessor.Preprocessor;
import uk.ac.ed.yazzzam.database.Database;
import uk.ac.ed.yazzzam.database.Sql2oModel;

public class GlobalSettings {

    public final static String inputFile = "test_song01.csv";
    public final static String stopwordsFile = "englishST.txt";

    // used in bm25:
    public final static int ranker_n = 100; // how many songs to be returned from scorer

    public final static double ranker_k1 = 1.2; // keep between (0.75, 2.25). recommended is 1.2. higher means we care more how many times word occurs in document, 0 means we only check if it exists.
    public final static double ranker_b = 0.75; // keep between (0.75, 2.25). recommended is 0.75. represents how much we care about document length, higher gives advantage to shorter documents.
    public final static double ranker_epsilon = 0.25; // keep under 0.4. recommended is 0.25. higher decreases score penalty of very common words.

    // used in the proximity scoring optimisation
    public final static double proximity_c = 28; // how important is words being close to each other. this should be the main experimentation
    public final static double proximity_threshold = 12; // if words are more than this value apart, pretend they are this value apart. probably better to keep above 10


//    private static Preprocessor preprocessor = new BasicPreprocessor();

//	full preprocessor settings
  private static Preprocessor preprocessor = new FullPreprocessor(stopwordsFile);

//    public final static String preprocessorMode = "stem";
//    public final static String preprocessorMode = "metaphone";
    public final static String preprocessorMode = "soundex";

    private static IndexBuilder ib = new IndexBuilder(preprocessor);

    private static Database db;





    public static Sql2oModel getDB() {
        return db.getModel();
    }
    public static IndexBuilder getIndex() {
        return ib;
    }

    public static Preprocessor getPreprocessor() {
        return preprocessor;
    }

}
