package app.cmanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.Main;
import app.client.ClientInfo;
import app.propagation.SendOneChangeThread;
import app.resource.ElementInfo;
import app.util.SocketPacket;


public class ConflictCheckingThread implements Runnable  {

	   Thread t;
	   String resID ; 
	   SocketPacket sp; 
	   
	   public ConflictCheckingThread(SocketPacket tsp) { 
		   
	      // thread created
		  Main.confThread = false;
	      t = new Thread(this, "Conflict Checking Thread");
	     
//	      resID = resourceID;
	      sp = tsp; 
	      resID = tsp.getResourceID();
	      // prints thread created
	      System.out.println("thread  = " + t);
	      
	      // this will call run() function
	      System.out.println("Calling run() function... ");
	      t.start();
	   }
	   
	   public String getResourceID() {
		   return resID;
	   }
	   
	   
	   public void run() {
		      System.out.println("Inside run()function => Before");
		      System.out.println("Size of Operation list is: " + Main.changePropagationDao.getAllresourceOperations().size());
		     
		    	  
		      System.out.println("sp is: " + sp.getOperation());
	    	  
	    	  List<SocketPacket> RemainingOpInList = Main.changePropagationDao.getAllresourceOperations();
	    	  
	    	  
	    	  //Conflict Detection and Resolution if Conflict arisen
	    	  Boolean isVerified = HandleConflict(sp, RemainingOpInList);
	    	  
	    	  System.out.println("isVerified is: " + isVerified);
	    	  
	    	  //Propagate Operation to Other Clients if Verified (No conflict, or it was winner)
	    	  if(isVerified.equals(true)) {
	    		  
	    		  // Save Data for Undoing Operations
	    		  if(sp.getOperation().contains("createNode") || sp.getOperation().contains("createEdge")) { 
	    			  ElementInfo element = new ElementInfo(sp.getElementID(), sp.getElementName(), sp.getOperation(), "");
	    			  try {
	    				  Main.elementInfoDao.addElement(element);
	    			  } catch (IOException e) {
	    				  // TODO Auto-generated catch block
	    				  e.printStackTrace();
	    			  }
	    		  } else if(sp.getOperation().contains("applyLabelEdit")) {
	    			  ///////////////////////////
	    			  System.out.println("ConflictChecking: applylabelEdit" + sp.getOperation());
	    			  Main.elementInfoDao.getElementByID(sp.getElementID()).setElementName(sp.getElementName());
	    			  Main.elementInfoDao.getElementByID(sp.getElementID()).setLastEditOp(sp.getOperation());    			  
	    			  try {
	    				  Main.elementInfoDao.writeElements();
	    			  } catch (IOException e) {
	    				  // TODO Auto-generated catch block
	    				  e.printStackTrace();
	    			  }
	    		  } else if(sp.getOperation().contains("deleteElement")) {
	    			  Main.elementInfoDao.getElementByID(sp.getElementID()).setElementName(sp.getElementName());
	    			  Main.elementInfoDao.getElementByID(sp.getElementID()).setLastEditOp(sp.getOperation()); 
	    			  try {
	    				  Main.elementInfoDao.writeElements();
	    			  } catch (IOException e) {
	    				  // TODO Auto-generated catch block
	    				  e.printStackTrace();
	    			  }
	    		  }
	    		  
	    		  
	    		  
	    		  
	    		  // Send OnDemand
	    		  Main.changePropagationDao.addOperationToFinalList(sp);	    		
	    		  for(ClientInfo c : Main.clientDao.getAllClients()) {
	    			  if( (c.getClientID().equals(sp.getClientID())==false) && (c.getResourceList().contains(sp.getResourceID())==true)) {
	    				  
//	    				  if(Main.propagationDao.getPropagation(c.getClientID(), sp.getResourceID())==null) {
//	    					  if(c.getdefaultSubscribe().equals("OnDemand")) {
//	    						  System.out.println("Send OnDemand One Change to " + c.getClientID());
//	    						  new SendOneChangeThread(c.getClientID(),sp);
//	    					  }
//	    				  }
//	    				  if(Main.propagationDao.exsitPropagation(c.getClientID(), sp.getResourceID())==false) {
//	    					  if(c.getdefaultSubscribe().equals("OnDemand")) {
//	    						  System.out.println("Send OnDemand One Change to " + c.getClientID());
//	    						  new SendOneChangeThread(c.getClientID(),sp);
//	    					  }
//	    				  }
//	    				  else if(Main.propagationDao.getPropagation(c.getClientID(), sp.getResourceID()).getsubscribeStrategy().equals("OnDemand")){
//	    					  System.out.println("OnDemand Send One Change to " + c.getClientID());
//	    					  new SendOneChangeThread(c.getClientID(),sp);
//	    				  }
	    				  
	    				  if(Main.propagationDao.exsitPropagation(sp.getResourceID(), c.getClientID())) {
	    					  if(Main.propagationDao.getPropagation(sp.getResourceID(), c.getClientID()).getsubscribeStrategy().equals("OnDemand")){
		    					  System.out.println("OooonDemand Send One Change to " + c.getClientID());
		    					  new SendOneChangeThread(c.getClientID(),sp);
		    				  }
	    				  }
	    				  else if(c.getdefaultSubscribe().equals("OnDemand")) {
    						  System.out.println("Sssssend OnDemand One Change to " + c.getClientID());
    						  new SendOneChangeThread(c.getClientID(),sp);
    					  }

	    			  }
	    		  }
	    		  
			      
			      // Send OnClose
	    		  Main.changePropagationDao.addOperationToOnClosedList(sp);
			      
			      
			      // Send OnSchedule
	    		  Main.changePropagationDao.addOperationToOnSchedule(sp);
	    		  
			      
	    	  }
	    	  else //Reject and Undo Operation in the Client if Not Verified 
	    	  {
	    		  String reverseOperation = UndoOperation(sp);
	    		  System.out.println("reverseOperation is: " + reverseOperation);
	    		  
	    		  String elementName = Main.elementInfoDao.getElementByID(sp.getElementID()).getElementName();
	    		  SocketPacket reverseSP = new SocketPacket("Server", sp.getResourceID(), sp.getResourceAddress(), sp.getElementID(), elementName, reverseOperation, null, 0);
	    		  new SendOneChangeThread(sp.getClientID(),reverseSP);
	    	  }
		      
	    	  if(Main.changePropagationDao.getAllresourceOperations().isEmpty()) {
	    		  Main.confThread = true;
	    		  System.out.println("Set ConfThread to true");
	    	  }else {
	    		  SocketPacket tsp = Main.changePropagationDao.pickHeadOfOperationQueue(sp.getResourceID());
	    		  System.out.println("call conflict thread for new tsp: " + tsp.getOperation());
	    		  new ConflictCheckingThread(tsp); 
	    	  }
	    	  
//	    	  if(Main.changePropagationDao.getAllresourceOperations().size()>0) {
//	    		  SocketPacket tsp = Main.changePropagationDao.pickHeadOfOperationQueue(sp.getResourceID());
//	    		  System.out.println("call conflict thread for new tsp: " + tsp.getOperation());
//	    		  new ConflictCheckingThread(tsp); 
//	    	  }else {
//	    		  Main.confThread = true;
//	    		  System.out.println("Set ConfThread to true");
//	    	  }
		    		  
//	    	  Main.confThread = true;
		      System.out.println("Inside run()function => After");	      
	   }

	   
	   
	   public String UndoOperation(SocketPacket sp) {
		   String reversedOp = ""; 
		   
	        if(sp.getOperation().contains("applyLabelEdit")) {
//	        	{"labelId":"d60d8f8b-a8e2-4979-b010-7f58d9010dad_header_label","text":"Types","kind":"applyLabelEdit"}
//	        	"{\"labelId\":\"d60d8f8b-a8e2-4979-b010-7f58d9010dad_header_label\",\"text\":\"Types\",\"kind\":\"applyLabelEdit\"}"
	        	
	        	reversedOp = Main.elementInfoDao.getElementByID(sp.getElementID()).getLastEditOp();
	        	if(reversedOp.equals(""))
		        	reversedOp = "{\"labelId\":" + sp.getElementID() + "_header_label\",\"text\":" + sp.getElementName() + "\",\"kind\":\"applyLabelEdit\"}" ;
	        }
	        else if(sp.getOperation().contains("createEdge")) {
	        	reversedOp = "{\"elementIds\":[\"" + sp.getElementID() + "\"],\"kind\":\"deleteElement\"}" ;
//	        		{"elementIds":["b60149ce-dcaf-4de9-b9f8-c1f1eef79c6e"],"kind":"deleteElement"}
	        } 
	        else if(sp.getOperation().contains("createNode")) {      		        		
	        	reversedOp = "{\"elementIds\":[\"" + sp.getElementID() + "\"],\"kind\":\"deleteElement\"}" ;
	        } 
	        else if(sp.getOperation().contains("deleteElement")) {
	        	
	        	// if it was a delete node, so we must first create that, then edit its label 
	        	if(Main.elementInfoDao.getElementByID(sp.getElementID()).getCreateOp().contains("createNode")) {
	        		// Create Element
	        		String createElement = 	Main.elementInfoDao.getElementByID(sp.getElementID()).getCreateOp();
	        		SocketPacket reverseSP = new SocketPacket("Server", sp.getResourceID(), sp.getResourceAddress(), sp.getElementID(), "", createElement, null, 0);
	        		new SendOneChangeThread(sp.getClientID(),reverseSP);
	        		
	        		reversedOp = "{\"labelId\":" + sp.getElementID() + "_header_label\",\"text\":" + sp.getElementName() + "\",\"kind\":\"applyLabelEdit\"}" ;
	        	}
	        	// if it was a delete node, we only need to create it again
	        	else if(Main.elementInfoDao.getElementByID(sp.getElementID()).getCreateOp().contains("createNode")) {
	        		reversedOp = Main.elementInfoDao.getElementByID(sp.getElementID()).getCreateOp();
	        	}
	        } 

		   return reversedOp; 
	   }
	   
	   
	   public Boolean HandleConflict(SocketPacket sp, List<SocketPacket> RemainingOpInList) {
		   Boolean spIsVerified = true; //No Conflict or Conflict is resolved
		   
		   System.out.println("HandelConflict is started, Size of RemainingOpInList is: " + RemainingOpInList.size());

		   if(Main.cmanagementDao.getCManagement().getDetectionStrategy().equals("Change Overlapping")) {
			   
			   System.out.println("Change OVerlapping started");
			   
			   
//			   SocketPacket finalSP = sp;
			   for(SocketPacket tsp : RemainingOpInList) {
				   
				   Boolean isConflicting = false ; 
				   isConflicting = checkContradictionForTwoOps(sp, tsp);
				   
				   if(isConflicting == true) {
					   if(Main.cmanagementDao.getCManagement().getResolutionStrategy().equals("Latest Operation Wins")) {
						   
						   // if sp is winner, we must maintain spIsVerified true and compare sp with other operation, and finally is was winner, we must send change to other clients 
						   if(sp.getClientTime().compareTo(tsp.getClientTime()) > 0) { 
							   // sp time is greater and it's latest operation, so we must remove tsp from splist and continue to compare with other remaining Op in List 
							   Main.changePropagationDao.removeOperationOfQueue(tsp);
						   }
						   
						   // else we must Abort the change and update the splist with moving tsp on top of the Queue
						   else {
							   spIsVerified = false; 
							   // remove dependencies
							   Main.changePropagationDao.updateOperationQueueByTSP(tsp);
							   break; 
						   }
					   }
					   else if(Main.cmanagementDao.getCManagement().getResolutionStrategy().equals("Highest Priority Wins")) {
						   
						   int spPriority = Main.cmanagementDao.getClientPriority(sp.getClientID());
						   int tspPriority = Main.cmanagementDao.getClientPriority(tsp.getClientID());
						   
						   if(spPriority <= tspPriority) {
							   Main.changePropagationDao.removeOperationOfQueue(tsp);
						   }
						   else {
							   spIsVerified = false; 
							   Main.changePropagationDao.updateOperationQueueByTSP(tsp);
							   break; 
						   }
					   }
					   else if(Main.cmanagementDao.getCManagement().getResolutionStrategy().equals("RL-Based Resolution")) {
						   int action = callCoReRL(sp, tsp);
						  
						   // action == 1 => move tsp (and prerequisites) to be executed before sp
						   if (action == 1) {
							   Main.changePropagationDao.updateOperationQueue(tsp, sp);
							   
						   }
						   
						   // action == 2 => apply sp and remove tsp
						   else if(action == 2) {
							   Main.changePropagationDao.removeOperationOfQueue(tsp);
						   }

						   // action == 3 => apply tsp and remove sp						   
						   else if(action == 3) {
							   spIsVerified = false; 
							   Main.changePropagationDao.updateOperationQueueByTSP(tsp);
							   break;
						   }
						   
						   // action == 4 => None of them
						   else {
							   spIsVerified = false; 
							   Main.changePropagationDao.removeOperationOfQueue(tsp);
							   break;							    
						   }
						 
						   
					   } 
					   else if(Main.cmanagementDao.getCManagement().getResolutionStrategy().equals("Abort Change")) {
						   spIsVerified = false; 
						   Main.changePropagationDao.updateOperationQueueByTSP(tsp);
						   break; 
					   }
				   }
			   }
			      
		   }
		   else if(Main.cmanagementDao.getCManagement().getDetectionStrategy().equals("Rule-Based Matching")) {
			   
			   System.out.println("Rule-based Matching started");
			   
			   int conflictCode = 0; //No conflict code
			   conflictCode = E3MPConflictDetection(sp, Main.cmanagementDao.getCManagement().getConflicRules());
			   
			   System.out.println("conflict Code is: " + conflictCode);
			   
			   if(conflictCode != 0) {
				   if(Main.cmanagementDao.getCManagement().getResolutionStrategy().equals("Rule-Based Resolution")) {
					   
					   Boolean isResolved = E3MPConflictResolution(sp, conflictCode, Main.cmanagementDao.getCManagement().getConflicRules());
					   if(isResolved == false)
						   spIsVerified = false ; 
					   
					   System.out.println("Rule-based Resolution, isResolved is: " + isResolved);
					   
				   }else if(Main.cmanagementDao.getCManagement().getResolutionStrategy().equals("Abort Change")) {
					   spIsVerified = false; 
					   
					   System.out.println("Abort Change, spIsVerified is: " + spIsVerified);
				   }
			   }
			   
		   }
		   
		   // Check Consistency for Update or Adds of Deleted elements
//		   if(update or addEdge addNode with container)
		   if(spIsVerified.equals(true)){
			   if(sp.getOperation().contains("applyLabelEdit") || sp.getOperation().contains("createNode") || sp.getOperation().contains("createEdge")) {
				   for(ElementInfo e: Main.elementInfoDao.getAllElements()) {
					   if(e.lastEditOp.contains("delete")) {
						   if(sp.getOperation().contains(e.getElementID())) {
							   spIsVerified = false;
							   break ; 
						   }
					   }
				   }
			   }
		   }
		   
		   return spIsVerified;
	   }
	   
	   
	   public Boolean checkContradictionForTwoOps(SocketPacket sp, SocketPacket tsp) {
//		   Boolean isConflicting = false ;
		   
		   if(sp.getClientID().equals(tsp.getClientID()) == false) {
			   
			   // Check for contradicting changes, such as Add-Add, Update-Update, and Delete-Update, which are applied to the same model element
			   // Also to remove duplicated operations to prevent applying one task by two clients 
			   if(sp.getElementID().equals(tsp.getElementID())) {
				   
				   // sp is Add Node
				   if(sp.getOperation().contains("createNode")) {
			  
					   // tsp is Add Node
					   if(tsp.getOperation().contains("createNode")) {
						   return true;
					   }
				   }
				   
				   // sp is Add Edge
				   else if(sp.getOperation().contains("createEdge")) {
					   
					   // tsp is Add Edge
					   if(tsp.getOperation().contains("createEdge")) {
						   return true; 
					   }
				   }
				   
				   // sp is Delete Element
				   else if(sp.getOperation().contains("deleteElement")) {
					   
					   // tsp is Add Edge
					   if(tsp.getOperation().contains("deleteElement")) {
						   return true; 
					   } 
					   // tsp is Update
					   else if(tsp.getOperation().contains("applyLabelEdit")) {
						   return true; 
					   }
					   // tsp is Update
					   else if(tsp.getOperation().contains("reconnectEdge")) {
						   return true; 
					   }
					   // tsp is Update
					   else if(tsp.getOperation().contains("changeContainer")) {
						   return true; 
					   }
					   // tsp is Update
					   else if(tsp.getOperation().contains("changeBounds")) {
						   return true; 
					   }
					   // tsp is Update
					   else if(tsp.getOperation().contains("compound")) {
						   return true; 
					   }
				   }
				   
				   // sp is Update Element
				   else if(sp.getOperation().contains("applyLabelEdit")) {
					   
					   // tsp is Add Edge
					   if(tsp.getOperation().contains("applyLabelEdit")) {
						   return true; 
					   }
					   // tsp is Delete
					   else if(tsp.getOperation().contains("deleteElement")) {
						   return true; 
					   }
				   }
				   
				   // sp is Update Element
				   else if(sp.getOperation().contains("reconnectEdge")) {
					   
					   // tsp is Add Edge
					   if(tsp.getOperation().contains("reconnectEdge")) {
						   return true; 
					   }
				   }
				   
				   // sp is Update Element
				   else if(sp.getOperation().contains("changeContainer")) {
					   
					   // tsp is Add Edge
					   if(tsp.getOperation().contains("changeContainer")) {
						   return true; 
					   }
					   // tsp is Delete
					   else if(tsp.getOperation().contains("deleteElement")) {
						   return true; 
					   }
				   }
				   
				   // sp is Update Element
				   else if(sp.getOperation().contains("changeBounds")) {
					   
					   // tsp is Add Edge
					   if(tsp.getOperation().contains("changeBounds")) {
						   return true; 
					   }
				   }
				   
				   // sp is Update Element
				   else if(sp.getOperation().contains("compound")) {
					   
					   // tsp is Add Edge
					   if(tsp.getOperation().contains("compound")) {
						   return true; 
					   }
				   }
			   }
			   
			   
			   // Check for the conflicting changes over different model elements leading to the operation contract violations 
			   // (i. e., Delete-Use, Update-Use, and Add-Forbid)
			   
			   if(sp.getOperation().contains("deleteElement")) {
				   
				   // tsp is Add Element to a Deleted Container 
				   if(tsp.getOperation().contains("createNode")) {
					   if(tsp.getOperation().contains(sp.getElementID()))					   
						   return true; 
				   } 
				   
				   
				   // tsp is Add Edge to a Deleted Container 
				   else if(tsp.getOperation().contains("createEdge")) {
					   if(tsp.getOperation().contains(sp.getElementID()))					   
						   return true; 
				   } 
				   
			   }
			   
			   
		   }
		   
		   return false ; //isConflicting;
	   }
	   
	   
	   public int E3MPConflictDetection(SocketPacket sp, ArrayList<CDRule> conflicRules) {
		   int conflictCode = 0; //No conflict
		   
//		   for(CDRule confRule : conflicRules) {
//			   conflictCode = callE3MPConsistencyChecking(sp.getOperation(), sp.getResourceID(), confRule) ;
//		   }
		   
		   return conflictCode; 
	   }

	   
	   public Boolean E3MPConflictResolution(SocketPacket sp, int conflictCode, ArrayList<CDRule> resolutionRules) {
		   Boolean isResolved = false; 
		   
//		   isResolved = callE3MPConsistencyResolution(sp.getOperation(), sp.getResourceID(), resolutionRules.get(conflictCode)) ;
		   
		   return isResolved; 
	   }

	   public int callCoReRL(SocketPacket sp, SocketPacket tsp) {
		   int action = 1; 
		   
		   return action ; 
	   }
	   
}
