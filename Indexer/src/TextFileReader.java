import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TextFileReader implements Reader{

	public String readFile(String fileName) throws FileNotFoundException {
		Scanner in = new Scanner(new InputStreamReader
				(new FileInputStream(System.getProperty("user.dir") + "/src/" + fileName), StandardCharsets.UTF_8));
		StringBuilder file_content = new StringBuilder();
		
		while(in.hasNext()) {
			file_content.append(in.next());
			file_content.append(" ");
		}
		
		in.close();
		
		return file_content.toString();
	}
	
	
}

	
