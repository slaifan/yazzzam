package uk.ac.ed.yazzzam.Indexer;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class TextFileReader implements Reader{

	public String readFile(String fileName) {
		try (Scanner in = new Scanner(new InputStreamReader
				(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName)), StandardCharsets.UTF_8))) {
			StringBuilder file_content = new StringBuilder();

			while(in.hasNext()) {
				file_content.append(in.next());
				file_content.append(" ");
			}
			return file_content.toString();
		}
	}
	
	
}

	
