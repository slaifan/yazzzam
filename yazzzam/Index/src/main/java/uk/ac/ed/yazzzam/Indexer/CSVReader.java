package uk.ac.ed.yazzzam.Indexer;

import java.io.*;
import java.util.List;
import com.opencsv.bean.CsvToBeanBuilder;


public class CSVReader {

    public static List readFile(String fileName) throws FileNotFoundException {
        //Pattern pattern = Pattern.compile();
        var songs = new CsvToBeanBuilder(new FileReader(fileName))
                .withType(Song.class).build().parse();
        return songs;
    }
}
