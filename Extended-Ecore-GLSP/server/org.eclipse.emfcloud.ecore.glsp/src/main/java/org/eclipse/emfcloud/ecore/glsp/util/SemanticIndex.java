package org.eclipse.emfcloud.ecore.glsp.util;

public class SemanticIndex {
    public String id;
    public String type;
    public String element;

    public SemanticIndex(String id, String type, String element) {
        this.id = id;
        this.type = type; 
        this.element = element; 
    }
    
    public String getID() {
        return id;
    }

    public String getType() {
        return type;
    }
    
    public String getElement() {
        return element;
    }
        
    public void setID(String id) {
    	this.id = id ; 
    }
    
    public void setType(String type) {
    	this.type = type ; 
    }
    
    public void setElement(String element) {
    	this.element = element ; 
    }

}

