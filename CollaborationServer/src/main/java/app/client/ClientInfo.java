package app.client;

import java.util.List;

public class ClientInfo {
	public final String clientID; 
	public String clientAddress;
	public String serverAddress;
	public final String creator; 
	
	public String defaultPublish; 
	public String defaultSubscribe; 
	public String scheduleTime; 
	
	public List<String> resourceList;
	
    public ClientInfo(String clientID, String clientAddress, String serverAddress, String creator, String defaultPublish, String defaultSubscribe, String scheduleTime, List<String> resourceList) {
        this.clientID = clientID;
        this.clientAddress = clientAddress;
        this.serverAddress = serverAddress;
        this.creator = creator;
        this.defaultPublish = defaultPublish; 
        this.defaultSubscribe = defaultSubscribe;
        this.scheduleTime = scheduleTime; 
        this.resourceList = resourceList;
    }

    public String getClientID() {
        return clientID;
    }

    public String getclientAddress() {
        return clientAddress;
    }

    public String getserverAddress() {
        return serverAddress;
    }

    public String getCreator() {
        return creator;
    }
    
    public String getdefaultPublish() {
    	return defaultPublish;
    }
    
    public String getdefaultSubscribe() {
    	return defaultSubscribe;
    }
    
    public String getscheduleTime() {
    	return scheduleTime;
    }
    
    public List<String> getResourceList() {
        return resourceList;
    } 
    
	public void setResourceList(List<String> resourceList) {
		this.resourceList = resourceList;
	}
	
	public void addResourceList(String resourceID) {
		this.resourceList.add(resourceID);
	}
    
    public void setclientAddress(String clientAddress) {
    	this.clientAddress = clientAddress ; 
    }
    
    public void setserverAddress(String serverAddress) {
    	this.serverAddress = serverAddress ; 
    }
	
    public void setdefaultPublish(String defaultPublish) {
    	this.defaultPublish = defaultPublish ; 
    }
    
    public void setdefaultSubscribe(String defaultSubscribe) {
    	this.defaultSubscribe = defaultSubscribe ; 
    }
    
    public void setscheduleTime(String scheduleTime) {
    	this.scheduleTime = scheduleTime ; 
    }
    
}
