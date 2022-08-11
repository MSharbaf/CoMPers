package org.eclipse.emfcloud.ecore.glsp.handler;
import java.util.ArrayList;
import java.util.List;


import org.eclipse.emfcloud.ecore.glsp.util.Propagation;
import org.eclipse.emfcloud.ecore.glsp.util.Resource;
import org.eclipse.emfcloud.ecore.glsp.util.SocketPacket;



public class ChangePropagationHandler {
    
	private final String clientID = "Client1"; 
	private final String serverAddress = "localhost" ; //Add server address, for example 192.168.1.5
	
	private String address;
    private String defaultPublish;
    private String defaultSubscribe;
	private String scheduleTime; 
    
    //Resource, Value (online, offline)
//    private BiMap<String, String> checkinMethods;
    
    private List<Resource> resourceList; 
    private List<Propagation> propagationMethods; 
    
    private List<SocketPacket> operationQueue ;  
    

    public ChangePropagationHandler(String resourceID) {    	
    	this.resourceList = new ArrayList<Resource>() ; 
    	this.propagationMethods = new ArrayList<Propagation>() ;
    	this.operationQueue = new ArrayList<SocketPacket>(); 
//    	this.checkinMethods = HashBiMap.create();
    	
//    	requestClientInfo(this.getClientID()); 
    	new RequestInfoThread(this.getClientID(), resourceID);
    }

    public String getClientID(){
    	return clientID ; 
    }
    
    public String getServerAddress(){
    	return serverAddress ; 
    }
    
//    public Boolean getLocalTest(){
//    	return localTest ; 
//    }
    
    public String getAddress() {
    	return address;
    }
    
    public String getDefultPublishMethod() {
    	return defaultPublish;
    }
    
    public String getDefultSubscribeMethod() {
    	return defaultSubscribe;
    }
    
    public String getScheduleTime() {
    	return scheduleTime;
    }
    
    public String getPublishMethod(String resourceID) {
    	for(Propagation p : this.propagationMethods){
    		if(p.resourceID.equals(resourceID))
    			return p.publishStrategy ;
    	}
    	return this.defaultPublish;
    }
    
    public List<Resource> getResourceList(){
    	return this.resourceList; 
    }
    
    public SocketPacket getFirstOperation() {
    	if(this.operationQueue.size()>0) {
    		SocketPacket sp = this.operationQueue.get(0);
    		this.operationQueue.remove(0);
    		return sp;
    	}
    	return null;
    }
    
    public List<SocketPacket> getResourceOperation(String resourceID){
    	List<SocketPacket> opQ = new ArrayList<SocketPacket>(); 
    	
    	for(SocketPacket sp : this.operationQueue)
    		if(sp.getResourceAddress().equals(resourceID)) {
    			opQ.add(sp);
    			this.operationQueue.remove(sp);
    		}
    	
//    	for(int i=0; i<this.operationQueue.size(); i++)
//    		if(this.operationQueue.get(i).getResourceID().equals(resourceID))
//    			opQ.add(this.operationQueue.get(i));
    	
    	return opQ; 
    }
    
    
    public List<SocketPacket> getOfflineOperation() {
//    	SocketPacket[] opQ = new SocketPacket[this.operationQueue.size()];
    	List<SocketPacket> opQ = new ArrayList<SocketPacket>(); 
    	for(SocketPacket sp : this.operationQueue) {
			opQ.add(sp);
//			this.operationQueue.remove(sp);
    	}
    	return opQ; 
    }
    
    
    public List<SocketPacket> getOperationQueue(){
    	return this.operationQueue; 
    }
    
    public void clearOperationQueue() {
    	this.operationQueue.clear();
    }
    
    public void removeOperationOfQueue(SocketPacket sp) {
    	
    	this.operationQueue.remove(sp);
    	
//    	List<SocketPacket> tempQueue = new ArrayList<SocketPacket>();
//    	for(SocketPacket t: this.operationQueue) {
//    		if(t.equals(sp))
//    			;
//    		else
//    			tempQueue.add(t);
//    	}
//    	this.operationQueue = tempQueue; 
    
    }
    
    public void setAddress(String address) {
    	this.address = address; 
    }
        
    public void setDefultPublishMethod(String defultCheckinMethod) {
    	this.defaultPublish = defultCheckinMethod; 
    }
    
    public void setDefultSubscribeMethod(String defultCheckoutMethod) {
    	this.defaultSubscribe = defultCheckoutMethod; 
    }
    
    public void setScheduleTime(String scheduleTime) {
    	this.scheduleTime = scheduleTime; 
    }
    
    public void addResource(Resource res) {
    	for(int i=0; i<this.resourceList.size(); i++)
    		if(this.resourceList.get(i).resourceID.equals(res.resourceID))
    			return ; 
    	
    	this.resourceList.add(res);
    }
    
    public void addPropagation(Propagation pro) {
    	for(int i=0; i<this.propagationMethods.size(); i++)
    		if(this.propagationMethods.get(i).resourceID.equals(pro.resourceID)) {
    			this.propagationMethods.get(i).setPublishStrategy(pro.publishStrategy);
    			this.propagationMethods.get(i).setSubscribeStrategy(pro.subscribeStrategy);
    			this.propagationMethods.get(i).setscheduleTime(pro.scheduleTime);
    			this.propagationMethods.get(i).setSetter(pro.setter);
    			return ; 
    		}
    	
		this.propagationMethods.add(pro);
    }
    
//    public void setCheckinMethod(String resID, String value) {
//    	if(this.checkinMethods.get(resID) != null)
//    		this.checkinMethods.put(resID, value);
////		this.checkinMethods.putIfAbsent(resID, value);
//
//    	else {
//    		this.checkinMethods.remove(resID);
//    		this.checkinMethods.putIfAbsent(resID, value); 
//    	}
//    }
    
    public void addOperationToQueue(SocketPacket sp) {
    	
    	System.out.println("[Eclipse1-->ChangePropagationHandler-->addOperationToQueue] start of addOperationToQueue ... ");
    	
//    	String resourceID = "";
//    	for(Resource r : resourceList){
//    		if(sp.getResourceAddress().contains(r.getecoreAddress()) && sp.getResourceAddress().contains(r.getprojectName())) {
//    			resourceID = r.getResourceID() ; 
//    			break; 
//    		}
//    	}
    	
    	if(sp.getResourceID().equals("")) {
    		if(this.defaultPublish.equals("Online"))
    	    	new SendChangeOnlineThread(sp);
    		else if(this.defaultPublish.equals("Offline"))
    			this.operationQueue.add(sp);
    	}
    	else {
    		// If getPublishMethod finds the resource, it returns the strategy, else it returns the default strategy 
    		if(getPublishMethod(sp.getResourceID()).equals("Online"))
    	    	new SendChangeOnlineThread(sp);
    		else if(getPublishMethod(sp.getResourceID()).equals("Offline"))
    			this.operationQueue.add(sp);
    	}
    		
       	System.out.println("[Eclipse1-->ChangePropagationHandler-->addOperationToQueue] end of addOperationToQueue ... ");
    	
    	
//    	this.operationQueue.add(sp);
//    	
////    	String chekcinMethod = Optional.ofNullable(this.checkinMethods.get(sp.getResourceID()));
//    	
////    	if(this.checkinMethods.get(sp.getResourceID()).equals("Online") || this.getDefultCheckinMethod().equals("Online")) {
////    		sendSocket(sp);
////    	}
//    	System.out.println("[Eclipse1-->ChangePropagationHandler-->addOperationToQueue] before creation of thread ... ");
//    	new SendChangeThread(sp);
//    	System.out.println("[Eclipse1-->ChangePropagationHandler-->addOperationToQueue] after creation of thread ... ");
    }
    
    
    
    
}