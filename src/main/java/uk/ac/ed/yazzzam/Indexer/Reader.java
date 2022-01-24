package uk.ac.ed.yazzzam.Indexer;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class Reader {

    public static String readFile(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new InputStreamReader
                (Objects.requireNonNull(Reader.class.getClassLoader().getResourceAsStream(fileName))));
        StringBuilder file_content = new StringBuilder();

        while (in.hasNext()) {
            file_content.append(in.next());
            file_content.append(" ");
        }

        in.close();

        return file_content.toString();
    }


}

	