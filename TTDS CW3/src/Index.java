import java.io.IOException;

public class Index{
	
	
	

    public static void main(String[] args) throws IOException {
    	
    	var file = Reader.readFile("songs.txt");
    	
  
    	IndexBuilder ib = new IndexBuilder(file);
    	
    	System.out.println(ib.buildIndex());
    	
    	
    }
}	