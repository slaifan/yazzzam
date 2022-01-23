import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface Preprocessor  {

	public ArrayList<String> preprocess(String document) throws FileNotFoundException;
	
	
}
