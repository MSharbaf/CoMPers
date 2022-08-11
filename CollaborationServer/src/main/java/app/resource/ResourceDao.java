package app.resource;

//import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
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

import app.Main;



import com.google.common.collect.ImmutableList;

public class ResourceDao {

    private final String resourceFile = "src/main/resources/data/resources.json";
    
    private List<ArrayList<Boolean>> onClose = new ArrayList<ArrayList<Boolean>>();
	
    
    private List<Resource> resources  = ImmutableList.of(
       // new Resource("Resources1", "..\\source\\Ecore.ecore", "..\\soruce\\Ecore.enotation", "Bahman", now)
    );

    public Iterable<Resource> getAllResources() {
        return resources;
    }

    public void addResource(Resource resource) throws IOException {
    	resources = ImmutableList.<Resource>builder()
                .addAll(resources)
                .add(resource)
                .build();
    	
    	ArrayList<Boolean> temp = new ArrayList<Boolean>();
    	
//    	System.out.println("clientDao size is " + Main.clientDao.getSize());
    	for(int i=0; i<Main.clientDao.getSize(); i++) {
    		temp.add(true);
    	}
    	onClose.add(temp);
    	
    	
//    	ConflictCheckingThread cct = null;  //= new ConflictCheckingThread(resource.getResourceID());
//    	confCheckingThreads.add(cct);
    	
    	writeResources();
    }
    
    public int getIndex(String resourceID) {
    	for(int i=0; i<resources.size(); i++) 
    		if(resources.get(i).resourceID.equals(resourceID))
    			return i; 
    		
    	return -1; 
    }
   
    public Resource getResourceByresourceID(String resourceID) {
        return resources.stream().filter(b -> b.resourceID.equals(resourceID)).findFirst().orElse(null);
    }
    

    public Iterable<Resource> getResourceByCreator(String name) {
    	List<Resource> list = new ArrayList<Resource>();
    	for(Resource r : resources)
    		if(r.creator.equals(name))
    			list.add(r);
        return list;
    }

//    public void getOndispose(String resourceID) {
//    	Resource r = resources.stream().filter(b -> b.resourceID.equals(resourceID)).findFirst().orElse(null);
//    	//create and download a zip file 
//    }
    
    public Resource getRandomResource() {
        return resources.get(new Random().nextInt(resources.size()));
    }
    
    public void writeResources() throws IOException {
        
        try (FileOutputStream fos = new FileOutputStream(resourceFile);
                OutputStreamWriter isr = new OutputStreamWriter(fos, 
                        StandardCharsets.UTF_8)) {
            
            Gson gson = new Gson();
            gson.toJson(this.resources, isr);
        }        
    }
    
    
    public void readResources() throws IOException {
        Gson gson = new GsonBuilder().create();
        Path path = new File(resourceFile).toPath();
        
        try (Reader reader = Files.newBufferedReader(path, 
                StandardCharsets.UTF_8)) {
            
        	Resource[] resourceList = gson.fromJson(reader, Resource[].class);
            
        	if(resourceList!=null) {
	            Arrays.stream(resourceList).forEach( r -> {

	                try {
						this.addResource(r) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            });
        	}
        }        
    }
    
    public void updateOnClose(String clientID, String resourceID, Boolean value) {
    	
    	int i = getIndex(resourceID); // row
    	int j = Main.clientDao.getIndex(clientID); //column 
    	
    	System.out.println("i in isTrueOnClose for " + resourceID + " is: " + i);
    	System.out.println("j in isTrueOnClose for " + clientID + " is: " + j);

    
    	if(i>=0 && j>=0)
    		onClose.get(i).set(j, value);
    	
    }
    
    public Boolean isTrueOnClose(String resourceID) {
    	
    	int i = getIndex(resourceID);
    	
    	System.out.println("i in isTrueOnClose for " + resourceID + " is: " + i);
    	
    	if(i==-1)
    		return true;
    	
    	for(int j=0; j<onClose.get(i).size(); j++) {
    		if(onClose.get(i).get(j)==false) {
    			System.out.println("i is: " + i  + " ,j is: " + j);
    			return false ;
    		}
    	}
    	
    	return true ; 
    }
    
    public int onCloseSize() {
    	return onClose.size();
    }
    
}
