package app.propagation;


public class Propagation {
	public final String clientID;
	public final String resourceID;
	public String publishStrategy;
	public String subscribeStrategy;
	public String scheduleTime; 
	public String setter; 
	
	
    public Propagation(String clientID, String resourceID, String publishStrategy, String subscribeStrategy, String scheduleTime, String setter) {
        this.clientID = clientID;
        this.resourceID = resourceID;
        this.publishStrategy = publishStrategy;
        this.subscribeStrategy = subscribeStrategy;
        this.scheduleTime = scheduleTime ;
        this.setter = setter;
    }

    public String getResourceID() {
        return resourceID;
    }

    public String getClientID() {
        return clientID;
    }
    
    public String getpublishStrategy() {
        return publishStrategy;
    }

    public String getsubscribeStrategy() {
        return subscribeStrategy;
    }

    public String getscheduleTime() {
        return scheduleTime;
    }
    
    public String getSetter() {
        return setter;
    }
    
    public void setPublishStrategy(String publishStrategy) {
    	this.publishStrategy = publishStrategy ; 
    }
    
    public void setSubscribeStrategy(String subscribeStrategy) {
    	this.subscribeStrategy = subscribeStrategy ; 
    }
    
    public void setscheduleTime(String scheduleTime) {
    	this.scheduleTime = scheduleTime ; 
    }
	
    public void setSetter(String setter) {
    	this.setter = setter ; 
    }
}
