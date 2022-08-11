package app.propagation;

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
import com.google.common.collect.ImmutableList;

public class PropagationDao {

    private final String propagationFile = "src/main/resources/data/propagations.json";
	
    private List<Propagation> propagations  = ImmutableList.of();

    public Iterable<Propagation> getAllPropagations() {
        return propagations;
    }

    public void addPropagation(Propagation propagation) throws IOException {
    	propagations = ImmutableList.<Propagation>builder()
                .addAll(propagations)
                .add(propagation)
                .build();
    	
    	writePropagations();
    }
    
    public void updatePropagation(Propagation propagation, String publishStrategy, String subscribeStrategy, String scheduleTime, String setter) throws IOException {
    	for(Propagation p : propagations)
    		if(p.equals(propagation)) {
    			p.setPublishStrategy(publishStrategy);
    			p.setSubscribeStrategy(subscribeStrategy);
    			p.setscheduleTime(scheduleTime);
    			p.setSetter(setter);
    		}
    	
    	writePropagations();
    }
    
    public Propagation getPropagation(String resourceID, String clientID) {
//    	for(Propagation p : propagations)
//    		if(p.resourceID.equals(resourceID) && p.clientID.equals(clientID))
//    			return p; 
//    	return null ; 
        return propagations.stream().filter(r -> r.resourceID.equals(resourceID) && r.clientID.equals(clientID)).findFirst().orElse(null);
    }
    
    public Boolean exsitPropagation(String resourceID, String clientID) {
    	for(Propagation p : this.propagations) {
    		if(p.getResourceID().equals(resourceID) && p.getClientID().equals(clientID))
    			return true ;
    	}
    	return false ; 
    }
    
    public Iterable<Propagation> getPropagationsByresourceID(String resourceID) {
    	List<Propagation> list = new ArrayList<Propagation>();
    	for(Propagation p : propagations)
    		if(p.resourceID.equals(resourceID))
    			list.add(p);
        return list;
    }

    public Iterable<Propagation> getPropagationsByclientID(String clientID) {
    	List<Propagation> list = new ArrayList<Propagation>();
    	for(Propagation r : propagations)
    		if(r.clientID.equals(clientID))
    			list.add(r);
        return list;
    }
    
    public Iterable<Propagation> getPropagationsBySetter(String name) {
    	List<Propagation> list = new ArrayList<Propagation>();
    	for(Propagation r : propagations)
    		if(r.setter.equals(name))
    			list.add(r);
        return list;
    }
    
    public void writePropagations() throws IOException {
        
        try (FileOutputStream fos = new FileOutputStream(propagationFile);
                OutputStreamWriter isr = new OutputStreamWriter(fos, 
                        StandardCharsets.UTF_8)) {
            
            Gson gson = new Gson();
            gson.toJson(this.propagations, isr);
        }
    }
    
    
    public void readPropagations() throws IOException {
        Gson gson = new GsonBuilder().create();
        Path path = new File(propagationFile).toPath();
        
        try (Reader reader = Files.newBufferedReader(path, 
                StandardCharsets.UTF_8)) {
            
        	Propagation[] propagationList = gson.fromJson(reader, Propagation[].class);
            
        	if(propagationList!=null) {
	            Arrays.stream(propagationList).forEach( p -> {
	                try {
						this.addPropagation(p) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            });
        	}
        }
    }
    

    
    
    
    
    
    
}
