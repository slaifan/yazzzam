package uk.ac.ed.yazzzam.Indexer;

import java.io.*;
import java.util.List;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;


public class CSVReader {

    public static List readFile(String fileName) throws FileNotFoundException {
        var songs = new CsvToBeanBuilder(new InputStreamReader(CSVReader.class.getClassLoader().getResourceAsStream(fileName)))
                .withSeparator('\t').withType(Song.class)
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH).build().parse();
        return songs;
    }
}
