package app.client;

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

import com.google.common.collect.ImmutableList;

public class ClientDao {
	
    private final String clientFile = "src/main/resources/data/clients.json";

    private List<ClientInfo> clients = ImmutableList.of();
    
//    private List<Boolean> onClose = new ArrayList<Boolean>();

    public Iterable<ClientInfo> getAllClients() {
        return clients;
    }

    public int getSize() {
    	return clients.size();
    }
    
    public int getIndex(String clientID) {
    	for(int i=0; i<clients.size(); i++) 
    		if(clients.get(i).clientID.equals(clientID))
    			return i; 
    		
    	return -1; 
    }
    
    public void addClient(ClientInfo client) throws IOException {
    	clients = ImmutableList.<ClientInfo>builder()
                .addAll(clients)
                .add(client)
                .build();
    	
//    	onClose.add(true);
    	
    	writeClients();     
    }
   
    public void updateClient(ClientInfo client, String clientAddress, String serverAddress, String defaultPublish, String defaultSubscribe, String scheduleTime, List<String> resourceList) throws IOException {
    	for(ClientInfo c : clients)
    		if(c.equals(client)) {
    			c.setclientAddress(clientAddress);
    			c.setserverAddress(serverAddress);
    			c.setdefaultPublish(defaultPublish);
    			c.setdefaultSubscribe(defaultSubscribe);
    			c.setscheduleTime(scheduleTime);
    			c.setResourceList(resourceList);
    		}
    	
    	writeClients();
    }
    
    public ClientInfo getClientByClientID(String clientID) {
        return clients.stream().filter(b -> b.clientID.equals(clientID)).findFirst().orElse(null);
    }

    public Iterable<ClientInfo> getClientByCreator(String name) {
    	List<ClientInfo> list = new ArrayList<ClientInfo>();
    	for(ClientInfo r : clients)
    		if(r.creator.equals(name))
    			list.add(r);
        return list;
    }
    
    public ClientInfo getRandomClient() {
        return clients.get(new Random().nextInt(clients.size()));
    }
    
    public void writeClients() throws IOException {
        
        try (FileOutputStream fos = new FileOutputStream(clientFile);
                OutputStreamWriter isr = new OutputStreamWriter(fos, 
                        StandardCharsets.UTF_8)) {
            
            Gson gson = new Gson();
            gson.toJson(this.clients, isr);
        }
        
    }
    
    
    public void readClients() throws IOException {
        Gson gson = new GsonBuilder().create();
        Path path = new File(clientFile).toPath();
        
        try (Reader reader = Files.newBufferedReader(path, 
                StandardCharsets.UTF_8)) {
            
            ClientInfo[] clientList = gson.fromJson(reader, ClientInfo[].class);
            
        	if(clientList!=null) {
	            Arrays.stream(clientList).forEach( c -> {
	                try {
						this.addClient(c) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            });
        	}
        }
    }
    
    
//    public void updateOnClose(String client, Boolean value) {
//    	for(int i=0; i<clients.size(); i++) {
//    		if(clients.get(i).getClientID().equals(client)) {
//    			onClose.set(i, value);
//    		}
//    	}
//    }
//    
//    public Boolean isTrueOnClose() {
//    	
//    	for(int i=0; i<onClose.size(); i++) {
//    		if(onClose.get(i)==false)
//    			return false ;
//    	}
//    	
//    	return true ; 
//    }
    
}
