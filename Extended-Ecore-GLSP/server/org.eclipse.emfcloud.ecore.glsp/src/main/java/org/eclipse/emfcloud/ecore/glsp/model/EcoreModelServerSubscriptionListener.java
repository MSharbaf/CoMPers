/*******************************************************************************
 * Copyright (c) 2020-2021 EclipseSource and others.
 *  
 *   This program and the accompanying materials are made available under the
 *   terms of the Eclipse Public License v. 2.0 which is available at
 *   http://www.eclipse.org/legal/epl-2.0.
 *  
 *   This Source Code may also be made available under the following Secondary
 *   Licenses when the conditions for such availability set forth in the Eclipse
 *   Public License v. 2.0 are satisfied: GNU General Public License, version 2
 *   with the GNU Classpath Exception which is available at
 *   https://www.gnu.org/software/classpath/license.html.
 *  
 *   SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ******************************************************************************/
package org.eclipse.emfcloud.ecore.glsp.model;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.ecore.glsp.EcoreServerLauncher;
import org.eclipse.emfcloud.ecore.glsp.handler.ChangePropagationHandler;
import org.eclipse.emfcloud.ecore.glsp.handler.WebSocketHandler;
import org.eclipse.emfcloud.ecore.glsp.util.Resource;
import org.eclipse.emfcloud.modelserver.client.XmiToEObjectSubscriptionListener;
import org.eclipse.emfcloud.modelserver.command.CCommandExecutionResult;
import org.eclipse.glsp.server.actions.ActionDispatcher;
import org.eclipse.glsp.server.actions.SetDirtyStateAction;
import org.eclipse.glsp.server.features.core.model.ModelSubmissionHandler;
import org.eclipse.glsp.server.features.core.model.RequestBoundsAction;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.JSONObject;

import com.google.gson.Gson;

public class EcoreModelServerSubscriptionListener extends XmiToEObjectSubscriptionListener {

	private static Logger LOGGER = Logger.getLogger(EcoreModelServerSubscriptionListener.class);
	private ActionDispatcher actionDispatcher;
	private EcoreModelState modelState;
	protected final ModelSubmissionHandler submissionHandler;

	public EcoreModelServerSubscriptionListener(final EcoreModelState modelState,
			final ActionDispatcher actionDispatcher, final ModelSubmissionHandler submissionHandler) {
		this.actionDispatcher = actionDispatcher;
		this.modelState = modelState;
		this.submissionHandler = submissionHandler;
	}

	public void refresh() {
		System.out.println("[Eclipse1-->EcoreModelServerSubscriptionListener] Start refresh ...");
		
		// reload models
		modelState.loadSourceModels();
		// refresh GModelRoot
		submissionHandler.submitModel(modelState);
		// requestboundsaction in submissionhandler not enough?
		actionDispatcher.dispatch(modelState.getClientId(), new RequestBoundsAction(modelState.getRoot()));
		
		/*********** Added by Moahmmadreza Sharbaf ***********/
		EcoreModelSourceLoader.myModelState = modelState; 
		EcoreModelSourceLoader.mySubscriptionListener = this ; 
		System.out.println("[Eclipse1-->EcoreModelServerSubscriptionListener] Finish refresh ...");

	}
	
//	public void myRefresh() {
//		System.out.println("[Eclipse1-->EcoreModelServerSubscriptionListener] Start myRefresh ...");
//		
//		// reload models
//		modelState.loadSourceModels();
//		// refresh GModelRoot
//		submissionHandler.submitModel(modelState);
//		// requestboundsaction in submissionhandler not enough?
////		actionDispatcher.dispatch(modelState.getClientId(), new RequestBoundsAction(modelState.getRoot()));
//		
//		/*********** Added by Moahmmadreza Sharbaf ***********/
//		EcoreModelSourceLoader.myModelState = modelState; 
//		EcoreModelSourceLoader.mySubscriptionListener = this ; 
//		System.out.println("[Eclipse1-->EcoreModelServerSubscriptionListener] Finish refresh ...");
//
//	}

	@Override
	public void onIncrementalUpdate(final CCommandExecutionResult commandResult) {
		LOGGER.debug("Incremental update from model server received: " + commandResult);
		this.refresh();
	}

	@Override
	public void onFullUpdate(final EObject fullUpdate) {
		LOGGER.debug("Full update from model server received: " + fullUpdate);
		this.refresh();
	}

	@Override
	public void onDirtyChange(boolean isDirty) {
		LOGGER.debug("Dirty State Changed: " + isDirty + " for clientId: " + modelState.getClientId());
		actionDispatcher.dispatch(modelState.getClientId(), new SetDirtyStateAction(isDirty));
	}

	@Override
	public void onError(final Optional<String> message) {
		LOGGER.debug("Error from model server received: " + message.get());
	}

	@Override
	public void onClosing(final int code, final String reason) {
		LOGGER.debug("Closing connection to model server, reason: " + reason);
	}

	@Override
	public void onClosed(final int code, final String reason) {
		System.out.println("Eclipse 1 --> EcoreModelServerSubscripationListener: Mohammadreza! Editor is closed ...") ;
//		sendSocket(Onclose = true)
		sendClose();
		LOGGER.debug("Closed connection to model server, reason: " + reason);
	}
	
	public void sendClose() {
		System.out.println("[Client1-->EcoreModelServerSubscriptionListener-->sendClose] Call Socket Startted ... "); 
		
        // Client protocol to be used
		HttpClient httpClient = new HttpClient();
      
	    // Create a Jetty WebSocket client
		WebSocketClient webSocketClient = new WebSocketClient(httpClient);
	
	    // Our application handler to respond to socket events
    	WebSocketHandler webSocketHandler = new WebSocketHandler();
	
	    try{
//	    	String destUri = "ws://192.168.1.1:7000/websocket";
//	    	String destUri = "ws://192.168.1.5:7000/websocket";
//	    	String destUri = "ws://localhost:7000/websocket";
	    	String destUri = "ws://" + EcoreServerLauncher.changePropagation.getServerAddress() + ":7000/websocket";
    	  
	    	webSocketClient.start();
         
	    	System.out.println("[Client1-->EcoreModelServerSubscriptionListener-->sendClose] Web Socket Startted ... "); 
	    	
	    	URI echoUri = new URI(destUri);
	    	ClientUpgradeRequest request = new ClientUpgradeRequest();

	    	// The seeion can be used to gracefully close the connection from the client side.
	    	// The example WebSocket server closes the current WebSocket after replying so we dont
	    	// need it in this example.
	    	Session session = webSocketClient.connect(webSocketHandler, echoUri, request).get();
	    	
	    	//  Identify resourceID
	    	String resourceID = EcoreModelSourceLoader.myModelState.getClientId();
	    	for(Resource r : EcoreServerLauncher.changePropagation.getResourceList()) {
	    		if(resourceID.contains(r.projectName) && resourceID.contains(r.ecoreAddress)) {
	    			resourceID = r.resourceID;
	    			break;
	    		}
	    	}
	    	
	    	
	    	JSONObject json = new JSONObject().put("clientID", EcoreServerLauncher.changePropagation.getClientID()).put("resourceID", resourceID.toString());
	    	String jsonSt = "TrueClose" + json.toString() ;
	    	session.getRemote().sendString(jsonSt);
	    	
	    	
//	    	System.out.println("[Client1-->EcoreModelServerSubscriptionListener-->sendClose] Before Creation of GSON Json String"); 	        
//	        String json = new Gson().toJson("TrueClose" + EcoreServerLauncher.changePropagation.getClientID());
	        
	    	
	    	System.out.println("[Client1-->EcoreModelServerSubscriptionListener-->sendClose] JSON String: " + jsonSt); 
 
      	  	// Connection information
      	  	System.out.println("[Client1-->EcoreModelServerSubscriptionListener-->sendClose] Connecting to: " + echoUri);

          	webSocketHandler.awaitClose(5, TimeUnit.SECONDS);
          
          	System.out.println("[Client1-->EcoreModelServerSubscriptionListener-->sendClose] Client Address: " + session.getLocalAddress().getAddress().toString() + ":" + session.getLocalAddress().getPort());
  			System.out.println("[Client1-->EcoreModelServerSubscriptionListener-->sendClose] Call Socket Finished ... "); 
  			
  			
	    }
	    catch(Throwable t) {
	    	System.out.println("[Client1-->EcoreModelServerSubscriptionListener-->sendClose] WebSocket failed: " + t);
	    }      
	    finally {
	    	try {
				webSocketClient.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}

}
