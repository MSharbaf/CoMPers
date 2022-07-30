package app.resource;

public class ElementInfo {
	public final String elementID;
	public String elementName;
	public String createOp; 
	public String lastEditOp;
	
	
    public ElementInfo(String elementID, String elementName, String createOp, String lastEditOp) {
        this.elementID = elementID;
        this.elementName = elementName; 
        this.createOp = createOp;
        this.lastEditOp = lastEditOp;
    }

    public String getElementID() {
        return elementID;
    }
    
    public String getElementName() {
        return elementName;
    }

    public String getCreateOp() {
        return createOp;
    }
    
    public String getLastEditOp() {
        return lastEditOp;
    }

    public void setElementName(String elementName) {
    	this.elementName = elementName;
    }
    
    public void setLastEditOp(String lastEditOp) {
    	this.lastEditOp = lastEditOp;
    }
	
}
