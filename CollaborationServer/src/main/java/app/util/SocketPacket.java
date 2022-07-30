package app.util;

import java.time.LocalTime;

public class SocketPacket {
    private String clientID;
    private String resourceID;
    private String resourceAddress;
    private String elementID;
    private String elementName;
    private String operation; 
    private LocalTime clientTime;
    private int serverTime; 

    public SocketPacket(String clientID, String resourceID, String resourceAddress, String elementID, String elementName, String operation, LocalTime clientTime, int serverTime) {
        this.clientID = clientID;
        this.resourceID = resourceID; 
        this.resourceAddress = resourceAddress; 
        this.elementID = elementID; 
        this.elementName = elementName;
        this.operation = operation;
        this.clientTime = clientTime; 
        this.serverTime = serverTime;
    }
    
    public String getClientID() {
        return clientID;
    }
    
    public String getResourceID() {
    	return resourceID;
    }

    public String getResourceAddress() {
        return resourceAddress;
    }
    
    public String getElementID() {
        return elementID;
    }
    
    public String getElementName() {
        return elementName;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public LocalTime getClientTime() {
        return clientTime;
    }
    
    public int getServerTime() {
        return serverTime;
    }
        
    public void setClientID(String clientID) {
    	this.clientID = clientID ; 
    }
    
    public void setResourceID(String resourceID) {
    	this.resourceID = resourceID ; 
    }
    
    public void setResourceAddress(String resourceAddress) {
    	this.resourceAddress = resourceAddress ; 
    }
    
    public void setElementID(String elementID) {
    	this.elementID = elementID ; 
    }
    
    public void setElementName(String elementName) {
    	this.elementName = elementName ; 
    }
    
    public void setOperation(String operation) {
    	this.operation = operation; 
    }

    public void setClientTime(LocalTime clientTime) {
    	this.clientTime = clientTime; 
    }
    
    public void setServerTime(int serverTime) {
    	this.serverTime = serverTime; 
    }
}

