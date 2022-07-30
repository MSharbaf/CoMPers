package app.cmanagement;

public class CPriority{
	public String clientID;
	public int priority;
	
	public CPriority(String clientID, int priority) {
		this.clientID = clientID; 
		this.priority = priority;
	}
	
    public String getClientID() {
        return clientID;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
    	this.priority = priority ; 
    }
}

