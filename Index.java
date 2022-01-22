import java.io.IOException;

public class Index{
	
	
	

    public static void main(String[] args) throws IOException {
    	
    	TextFileReader r = new TextFileReader();
    	
    	var documentsFile = r.readFile(args[0]);
    	
  
    	IndexBuilder ib = new IndexBuilder();
    	ib.preprocess_documents(documentsFile);
    	
    	System.out.println(ib.buildIndex());
    	
    }
}	