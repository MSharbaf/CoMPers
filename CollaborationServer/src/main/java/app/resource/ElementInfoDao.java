package app.resource;

import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class ElementInfoDao {

    private final String elementFile = "src/main/resources/data/elements.json";
	
    private List<ElementInfo> elements  = new ArrayList<ElementInfo>();

    public List<ElementInfo> getAllElements() {
        return elements;
    }

    public void addElement(ElementInfo element) throws IOException{
    	elements.add(element);
    	
    	writeElements();
    }
   
    public ElementInfo getElementByID(String elementID) {
        return elements.stream().filter(b -> b.elementID.equals(elementID)).findFirst().orElse(null);
    }
    
    
    public void writeElements() throws IOException {
        
        try (FileOutputStream fos = new FileOutputStream(elementFile);
                OutputStreamWriter isr = new OutputStreamWriter(fos, 
                        StandardCharsets.UTF_8)) {
            
            Gson gson = new Gson();
            gson.toJson(this.elements, isr);
        }        
    }
    
    
    public void readElements() throws IOException {
        Gson gson = new GsonBuilder().create();
        Path path = new File(elementFile).toPath();
        
        try (Reader reader = Files.newBufferedReader(path, 
                StandardCharsets.UTF_8)) {
            
        	ElementInfo[] elementList = gson.fromJson(reader, ElementInfo[].class);
            
        	if(elementList!=null) {
	            Arrays.stream(elementList).forEach( r -> {

	                try {
						this.addElement(r) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            });
        	}
        }        
    }
    
}
