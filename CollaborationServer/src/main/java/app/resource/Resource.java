package app.resource;

import java.time.LocalDateTime;


public class Resource {
	public final String resourceID;
	public final String projectName; 
	public final String ecoreAddress;
	public final String enotationAddress;
	public final String creator; 
	public final LocalDateTime lastUpdate; 
	
	
    public Resource(String resourceID, String projectName, String ecoreAddress, String enotationAddress, String creator, LocalDateTime lastUpdate) {
        this.resourceID = resourceID;
        this.projectName = projectName;
        this.ecoreAddress = ecoreAddress;
        this.enotationAddress = enotationAddress;
        this.creator = creator;
        this.lastUpdate = lastUpdate; 
    }

    public String getResourceID() {
        return resourceID;
    }

    public String getprojectName() {
        return projectName;
    }
    
    public String getecoreAddress() {
        return ecoreAddress;
    }

    public String getenotationAddress() {
        return enotationAddress;
    }

    public String getCreator() {
        return creator;
    }
    
    public LocalDateTime getlastUpdate() {
    	return lastUpdate ; 
    }
	
}
