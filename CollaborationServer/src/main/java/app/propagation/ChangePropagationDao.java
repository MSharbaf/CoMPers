package app.propagation;

import java.util.*;

import app.util.SocketPacket;

public class ChangePropagationDao {
	
    //Resource, Operation (SocketPacket)
//	private HashMap<String, List<SocketPacket>> resourceOperationQueues = new HashMap<>();
	
	private HashMap<String, List<SocketPacket>> resourceOperationFinalList = new HashMap<>();
	private HashMap<String, List<SocketPacket>> resourceOperationOnClosedList = new HashMap<>();
	private HashMap<String, List<SocketPacket>> resourceOperationOnSchedule = new HashMap<>();
	
	public List<SocketPacket> resourceQueue = new ArrayList<SocketPacket>(); 
    
	
    public void addOperationToFinalList(SocketPacket sp) {
    	if(resourceOperationFinalList.containsKey(sp.getResourceID())) {
    		resourceOperationFinalList.get(sp.getResourceID()).add(sp);
    	}
    	else {
    		List<SocketPacket> SPList = new ArrayList<SocketPacket>();
    		SPList.add(sp);
    		resourceOperationFinalList.put(sp.getResourceID(), SPList);
    	}	
    }
    
    public void addOperationToOnClosedList(SocketPacket sp) {
    	if(resourceOperationOnClosedList.containsKey(sp.getResourceID())) {
    		resourceOperationOnClosedList.get(sp.getResourceID()).add(sp);
    	}
    	else {
    		List<SocketPacket> SPList = new ArrayList<SocketPacket>();
    		SPList.add(sp);
    		resourceOperationOnClosedList.put(sp.getResourceID(), SPList);
    	}	
    }
    
    public void addOperationToOnSchedule(SocketPacket sp) {
    	if(resourceOperationOnSchedule.containsKey(sp.getResourceID())) {
    		resourceOperationOnSchedule.get(sp.getResourceID()).add(sp);
    	}
    	else {
    		List<SocketPacket> SPList = new ArrayList<SocketPacket>();
    		SPList.add(sp);
    		resourceOperationOnSchedule.put(sp.getResourceID(), SPList);
    	}	
    }
	
    public void removeOperationOfFinalList(String resourceID, SocketPacket sp) {
		if(resourceOperationFinalList.get(resourceID).contains(sp)==true) {
			resourceOperationFinalList.get(resourceID).remove(sp); 
		}
    }
	
    public void removeOperationOfOnClosedList(String resourceID, SocketPacket sp) {
		if(resourceOperationOnClosedList.get(resourceID).contains(sp)==true) {
			resourceOperationOnClosedList.get(resourceID).remove(sp); 
		}
    }
    
    public void removeOperationOfOnSchedule(String resourceID, SocketPacket sp) {
		if(resourceOperationOnSchedule.get(resourceID).contains(sp)==true) {
			resourceOperationOnSchedule.get(resourceID).remove(sp); 
		}
    }
	
    public List<SocketPacket> getAllOperationsOfOnClosedList(String resourceID) {
        return this.resourceOperationOnClosedList.get(resourceID);
    }
    
    public int getSizeOfOnClosedList(String resourceID) {
    	if(resourceOperationOnClosedList.isEmpty())
    		return 0;
    	else if(resourceOperationOnClosedList.get(resourceID).isEmpty())
    		return 0; 
    	else
    		return this.resourceOperationOnClosedList.get(resourceID).size();
    }
    
    public List<SocketPacket> getAllOperationsOfOnScheduleList(String resourceID) {
    	return this.resourceOperationOnSchedule.get(resourceID);
    }
    
    public int getSizeOfOnScheduleList(String resourceID) {
   	 return this.resourceOperationOnSchedule.get(resourceID).size();
   }
	
    public void removeAllOperationOfOnClosedList(String resourceID) {
		List<SocketPacket> SPList = new ArrayList<SocketPacket>();
		resourceOperationOnClosedList.put(resourceID, SPList);
    }
    
    public void removeAllOperationOfOnScheduleList(String resourceID) {
		List<SocketPacket> SPList = new ArrayList<SocketPacket>();
		resourceOperationOnSchedule.put(resourceID, SPList);
    }
    
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Set<String> getAllResources(){
		return this.resourceOperationFinalList.keySet(); 
	}
	
    public List<SocketPacket> getAllresourceOperations() {
        return this.resourceQueue;
    }

    public void addOperationToQueue(SocketPacket sp) {
    	this.resourceQueue.add(sp);
    }
    
    public void removeOperationOfQueue(SocketPacket sp) {
    	this.resourceQueue.remove(sp);
    			
		// remove dependencies after sp 
		removeDepndencies(sp);
    }
    
    public void removeDepndencies(SocketPacket sp) {
    	if(sp.getOperation().contains("createNode") || sp.getOperation().contains("createEdge")) {
	    	for(SocketPacket tsp : this.resourceQueue){
	    		if(tsp.getResourceID().equals(sp.getResourceID()) && tsp.getOperation().contains(sp.getElementID())){
	    				this.resourceQueue.remove(tsp);
	    				removeDepndencies(tsp);
	    		}
	    	}
    	}
    }
    
    /**
     * Get and Remove the first SocketPacket for the resource
     * @param resourceID
     * @return the first SP or null
     */
    public SocketPacket pickHeadOfOperationQueue(String resourceID) {
    	System.out.println("Before Remove, Size of queue is: " + resourceQueue.size());
    	if(this.resourceQueue.size()>0) {
    		for(int i=0; i<this.resourceQueue.size(); i++) {
    			if(this.resourceQueue.get(i).getResourceID().equals(resourceID)) {
    				SocketPacket sp = this.resourceQueue.get(i);
    				this.resourceQueue.remove(i);
    				System.out.println("After Remove, Size of queue is: " + resourceQueue.size());
    				return sp; 
    			}
    		}
    	}
    	return null; 
    }
    
    
    public void updateOperationQueueByTSP(SocketPacket sp) {
    	
    	List<SocketPacket> NewSPList = new ArrayList<SocketPacket>(); 
    	
    	for(SocketPacket tsp : this.resourceQueue) {
    		if(tsp.equals(sp) == true) {
    			NewSPList.add(tsp);
    			this.resourceQueue.remove(tsp);
    			break; 
    		}
    		else if(tsp.getClientID().equals(sp.getClientID()) == true) {
    			NewSPList.add(tsp);
    			this.resourceQueue.remove(tsp);
    		}
    	}
    	
    	for(SocketPacket tsp : this.resourceQueue) {
    		NewSPList.add(tsp);
    	}
    	
    	this.resourceQueue.clear();
    	this.resourceQueue.addAll(NewSPList);        	
    }
    
    
    
    
    /**
     * 
     * @param resourceID
     * @param sp Conflicting operation 
     * @param osp Original operation
     */
    public void updateOperationQueue(SocketPacket sp, SocketPacket osp) {
    	List<SocketPacket> NewSPList = new ArrayList<SocketPacket>() ;
    	
    	for(SocketPacket tsp : this.resourceQueue) {
    		if(tsp.equals(sp) == true) {
    			NewSPList.add(tsp);
    			NewSPList.add(osp);
    			this.resourceQueue.remove(tsp);
    			break; 
    		}
    		else if(tsp.getClientID().equals(sp.getClientID())==true) {
    			NewSPList.add(tsp);
    			this.resourceQueue.remove(tsp);
    		}
    	}
    	
    	for(SocketPacket tsp : resourceQueue) {
    		NewSPList.add(tsp);
    	}
    	
    	
    	this.resourceQueue.clear();
    	this.resourceQueue.addAll(NewSPList);  

    }
    

//	public Set<String> getAllResources(){
//		return this.resourceOperationQueues.keySet(); 
//	}
//	
//    public List<SocketPacket> getAllresourceOperations(String resourceID) {
//        return this.resourceOperationQueues.get(resourceID);
//    }
//
//    public void addOperationToQueue(SocketPacket sp) {
//    	if(resourceOperationQueues.containsKey(sp.getResourceID())) {
//    		resourceOperationQueues.get(sp.getResourceID()).add(sp);
//    	}
//    	else {
//    		List<SocketPacket> SPList = new ArrayList<SocketPacket>();
//    		SPList.add(sp);
//    		resourceOperationQueues.put(sp.getResourceID(), SPList);
//    	}	
//    }
//    
//    public void removeOperationOfQueue(String resourceID, SocketPacket sp) {
//		if(resourceOperationQueues.get(resourceID).contains(sp)==true) {
//			resourceOperationQueues.get(resourceID).remove(sp); 
//		}
//		
//		// remove dependencies after sp 
//		removeDepndencies(sp);
//		
//    }
//    
//    public void removeDepndencies(SocketPacket sp) {
//    	if(sp.getOperation().contains("createNode") || sp.getOperation().contains("createEdge")) {
//	    	for(SocketPacket tsp : resourceOperationQueues.get(sp.getResourceID())){
//	    		if(tsp.getOperation().contains(sp.getElementID())){
//	    				resourceOperationQueues.get(sp.getResourceID()).remove(tsp);
//	    				removeDepndencies(tsp);
//	    		}
//	    	}
//    	}
//    }
//    
//    /**
//     * Get and Remove the first SocketPacket for the resource
//     * @param resourceID
//     * @return the first SP or null
//     */
//    public SocketPacket pickHeadOfOperationQueue(String resourceID) {
//    	if(resourceOperationQueues.get(resourceID).size()>0) {
//    		System.out.println("Before, Size of queue is: " + resourceOperationQueues.get(resourceID).size());
//    		SocketPacket sp = resourceOperationQueues.get(resourceID).get(0);
////    		resourceOperationQueues.get(resourceID).remove(sp);
//    		resourceOperationQueues.get(resourceID).remove(0);
//    		
//    		List<SocketPacket> NewSPList = new ArrayList<SocketPacket>(); 
//    		for(SocketPacket tsp : resourceOperationQueues.get(resourceID))
//    			NewSPList.add(tsp);
//    		resourceOperationQueues.replace(resourceID, NewSPList);
//    		
//    		System.out.println("After, Size of queue is: " + resourceOperationQueues.get(resourceID).size());
//    		return sp ;
//    	}
//    	return null ; 
//    }
//    
//    
//    public void updateOperationQueueByTSP(String resourceID, SocketPacket sp) {
//    	
//    	List<SocketPacket> NewSPList = new ArrayList<SocketPacket>(); 
//    	
//    	for(SocketPacket tsp : this.resourceOperationQueues.get(resourceID)) {
//    		if(tsp.equals(sp) == true) {
//    			NewSPList.add(tsp);
//    			this.resourceOperationQueues.get(resourceID).remove(tsp);
//    			break; 
//    		}
//    		else if(tsp.getClientID().equals(sp.getClientID()) == true) {
//    			NewSPList.add(tsp);
//    			this.resourceOperationQueues.get(resourceID).remove(tsp);
//    		}
//    	}
//    	
//    	for(SocketPacket tsp : this.resourceOperationQueues.get(resourceID)) {
//    		NewSPList.add(tsp);
//    	}
//    	
//    	this.resourceOperationQueues.replace(resourceID, NewSPList);
//        	
//    }
//    
//    
//    
//    
//    /**
//     * 
//     * @param resourceID
//     * @param sp Conflicting operation 
//     * @param osp Original operation
//     */
//    public void updateOperationQueue(String resourceID, SocketPacket sp, SocketPacket osp) {
//    	List<SocketPacket> NewSPList = new ArrayList<SocketPacket>() ;
//    	
//    	for(SocketPacket tsp : this.resourceOperationQueues.get(resourceID)) {
//    		if(tsp.equals(sp) == true) {
//    			NewSPList.add(tsp);
//    			NewSPList.add(osp);
//    			this.resourceOperationQueues.get(resourceID).remove(tsp);
//    			break; 
//    		}
//    		else if(tsp.getClientID().equals(sp.getClientID()) == true) {
//    			NewSPList.add(tsp);
//    			this.resourceOperationQueues.get(resourceID).remove(tsp);
//    		}
//    	}
//    	
//    	for(SocketPacket tsp : resourceOperationQueues.get(resourceID)) {
//    		NewSPList.add(tsp);
//    	}
//    	
//    	this.resourceOperationQueues.replace(resourceID, NewSPList);
//
//    }
    
    
    
    
    
    
}
