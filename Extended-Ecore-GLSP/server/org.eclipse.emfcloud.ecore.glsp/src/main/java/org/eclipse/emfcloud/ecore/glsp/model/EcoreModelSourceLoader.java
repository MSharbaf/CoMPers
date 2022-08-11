/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.ecore.glsp.model;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emfcloud.ecore.enotation.Diagram;
import org.eclipse.emfcloud.ecore.glsp.EcoreEditorContext;
import org.eclipse.emfcloud.ecore.glsp.EcoreFacade;
import org.eclipse.emfcloud.ecore.glsp.EcoreServerLauncher;
import org.eclipse.emfcloud.ecore.glsp.ModelServerClientProvider;
import org.eclipse.emfcloud.ecore.glsp.handler.ChangePropagationHandler;
import org.eclipse.emfcloud.ecore.glsp.handler.EcoreOperationActionHandler;
import org.eclipse.emfcloud.ecore.glsp.handler.SendChangeOfflineThread;
import org.eclipse.emfcloud.ecore.glsp.handler.SendChangeOnlineThread;
import org.eclipse.emfcloud.ecore.glsp.util.ClientInfo;
import org.eclipse.emfcloud.ecore.glsp.util.ElementInfo;
import org.eclipse.emfcloud.ecore.glsp.util.ElementInfoDao;
import org.eclipse.emfcloud.ecore.glsp.util.Propagation;
import org.eclipse.emfcloud.ecore.glsp.util.Resource;
import org.eclipse.emfcloud.ecore.glsp.util.SocketPacket;
import org.eclipse.emfcloud.ecore.modelserver.EcoreModelServerClient;
import org.eclipse.emfcloud.ecore.modelserver.GPointImplInstanceTypeAdapter;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.graph.builder.impl.GGraphBuilder;
import org.eclipse.glsp.server.actions.ActionDispatcher;
import org.eclipse.glsp.server.actions.ActionMessage;
import org.eclipse.glsp.server.features.core.model.ModelSourceLoader;
import org.eclipse.glsp.server.features.core.model.ModelSubmissionHandler;
import org.eclipse.glsp.server.features.core.model.RequestModelAction;
import org.eclipse.glsp.server.features.directediting.ApplyLabelEditOperation;
import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.operations.ChangeBoundsOperation;
import org.eclipse.glsp.server.operations.ChangeContainerOperation;
import org.eclipse.glsp.server.operations.ChangeRoutingPointsOperation;
import org.eclipse.glsp.server.operations.CompoundOperation;
import org.eclipse.glsp.server.operations.CreateEdgeOperation;
import org.eclipse.glsp.server.operations.CreateNodeOperation;
import org.eclipse.glsp.server.operations.CutOperation;
import org.eclipse.glsp.server.operations.DeleteOperation;
import org.eclipse.glsp.server.operations.LayoutOperation;
import org.eclipse.glsp.server.operations.Operation;
import org.eclipse.glsp.server.operations.PasteOperation;
import org.eclipse.glsp.server.operations.ReconnectEdgeOperation;
import org.eclipse.glsp.server.utils.ClientOptions;
import org.eclipse.glsp.server.utils.MapUtil;
import org.json.JSONException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

import io.javalin.Javalin;

public class EcoreModelSourceLoader implements ModelSourceLoader {

	private static Logger LOGGER = Logger.getLogger(EcoreModelSourceLoader.class);
	private static final String ROOT_ID = "sprotty";
	private static final String FORMAT_XMI = "xmi";

	@Inject
	private ModelServerClientProvider modelServerClientProvider;

	@Inject
	private ActionDispatcher actionDispatcher;

	@Inject
	protected ModelSubmissionHandler submissionHandler;
	
	/** Added by Mohammadreza **/
	private static GModelState mygModelState ; 
	public static EcoreModelState myModelState ; 
	public static EcoreModelServerSubscriptionListener mySubscriptionListener;
    private static String logFile = "" ;// "/home/ubuntu18/Downloads/eclipse-modeling-2021-12-R-linux-gtk-x86_64/eclipse1/ecore-glsp/client/workspace/SharbafTest/model/SharbafTesHistor.txt";  //"ecore-glsp/server/org.eclipse.emfcloud.ecore.glsp/src/main/resources/History.txt";


	public static RequestModelAction myAction; 
	private static boolean portStatus = true ; 

	@Override
	public void loadSourceModel(RequestModelAction action, GModelState gModelState) {
		
		/** Added by Mohammadreza **/ 
		int myPort = 6000 ; 
		
		
		System.out.println("[Eclipse1-->EcoreModelSourceLoader] loadSourceModel is started ...");
		this.mygModelState = gModelState ; 	
		this.myAction = action ;

		
		
		Javalin javalin = Javalin.create();  
		if(portStatus) {
			javalin.start(myPort);
			portStatus = false ; 
		}
		javalin.ws("/websocket", ws -> {

            ws.onConnect((ctx) -> {
            	System.out.println("[Eclipse1-->EcoreModelSourceLoader] The sever is Connected");
            	System.out.println("[Eclipse1-->EcoreModelSourceLoader] Wellcome to Client1: " + ctx.session.getRemoteAddress().toString()); 
            });

            ws.onMessage((ctx) -> {
            	if(ctx.message().isEmpty())
            		System.out.println("[Eclipse1-->EcoreModelSourceLoader] Message is Null");
            	
            	System.out.println("[Eclipse1-->EcoreModelSourceLoader] Received message in Client1 is: "+ ctx.message());
            	
        		 
        		if(ctx.message().charAt(0)=='I')
        			receiveClientInfo(ctx.message());
        		else if(ctx.message().charAt(0)=='R')
        			receiveClientResources(ctx.message());
        		else if(ctx.message().charAt(0)=='P')
        			receiveClientPropagation(ctx.message());
        		else if(ctx.message().charAt(0)=='{')
                   	receiveOnlineSocket(ctx.message());
        		else if(ctx.message().charAt(0)=='O') // OnClose and OnSchedule change handling
        		   	receiveOfflineSocket(ctx.message());
        		else if(ctx.message().charAt(0)=='C') {
        			System.out.println("Commit Change Offline ...");
                   	sendOfflineSocket();
        		}
     
            });
            
            ws.onClose(ctx -> System.out.println("[Eclipse1-->EcoreModelSourceLoader] Cloosed :" + ctx.session.getRemoteAddress().toString()));

            ws.onError(ctx -> System.out.println("[Eclipse1-->EcoreModelSourceLoader] Erroored"));
            
        });

		
		System.out.println("[Eclipse1-->EcoreModelSourceLoader] loadSourceModel after javalin ...");

		///////////////////////////////////////////////////////////

		
		EcoreModelState modelState = EcoreModelState.getModelState(gModelState);
		modelState.setClientOptions(action.getOptions());

		Optional<String> sourceURI = MapUtil.getValue(action.getOptions(), ClientOptions.SOURCE_URI);
		if (sourceURI.isEmpty()) {
			LOGGER.error("No source uri given to load model, return empty model.");
			modelState.setRoot(createEmptyRoot());
			return;
		}

		Optional<EcoreModelServerClient> modelServerClient = modelServerClientProvider.get();
		if (modelServerClient.isEmpty()) {
			LOGGER.error("Connection to modelserver has not been initialized, return empty model");
			modelState.setRoot(createEmptyRoot());
			return;
		}

		EcoreModelServerAccess modelServerAccess = new EcoreModelServerAccess(modelState.getModelUri(),
				modelServerClient.get());
		modelState.setModelServerAccess(modelServerAccess);
		
		EcoreModelServerSubscriptionListener SubscriptionListener = new EcoreModelServerSubscriptionListener(modelState, actionDispatcher, submissionHandler) ; 
		modelServerClient.get()
		.subscribe(sourceURI.get(), SubscriptionListener, FORMAT_XMI);
		
//		modelServerClient.get()
//				.subscribe(sourceURI.get(), new EcoreModelServerSubscriptionListener(modelState, actionDispatcher, submissionHandler), FORMAT_XMI);

		EcoreEditorContext editorContext = new EcoreEditorContext(modelState, modelServerAccess);
		modelState.setEditorContext(editorContext);

		EcoreFacade ecoreFacade = editorContext.getEcoreFacade();
		if (ecoreFacade == null) {
			LOGGER.error("EcoreFacade could not be found, return empty model");
			modelState.setRoot(createEmptyRoot());
			return;
		}
		

		
		/********* Added By Mohammadreza Sharbaf ********/
		this.myModelState = modelState ; 
		this.mySubscriptionListener = SubscriptionListener; 
		Diagram diagram = ecoreFacade.getDiagram();
		EcoreServerLauncher.changePropagation = new ChangePropagationHandler(modelState.getClientId());
		EcoreServerLauncher.elementInfoDao = new ElementInfoDao();
		try {
			EcoreServerLauncher.elementInfoDao.readElements();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logFile = modelState.getClientId().substring(19, modelState.getClientId().indexOf(".ecore")) + "History.txt";
		System.out.println("logFile is: " + logFile);
		
	}

	private static GModelRoot createEmptyRoot() {
		return new GGraphBuilder(DefaultTypes.GRAPH)//
				.id(ROOT_ID) //
				.build();
	}
	

	public void receiveClientInfo(String message) throws JSONException{
		int i = message.indexOf("Info");
		System.out.println("i is: " + i);
		message = message.substring(4);
		
		ClientInfo c = new Gson().fromJson(message, ClientInfo.class); 
		System.out.println("Info is: " + c.serverAddress);
		
		EcoreServerLauncher.changePropagation.setDefultPublishMethod(c.getdefaultPublish());
		EcoreServerLauncher.changePropagation.setDefultSubscribeMethod(c.getdefaultSubscribe());
		EcoreServerLauncher.changePropagation.setScheduleTime(c.getscheduleTime());
		EcoreServerLauncher.changePropagation.setAddress(c.getserverAddress().substring(0,c.getserverAddress().indexOf(':')));

		System.out.println("Address is: " + EcoreServerLauncher.changePropagation.getAddress());
		
		
		// Write History Log :)
		String st = "*************************** Client Info *************************** \n" ; 
		st = st + "You connected as: " + EcoreServerLauncher.changePropagation.getClientID() + "\n";
		st = st + "Your Address is: " + EcoreServerLauncher.changePropagation.getAddress() + "\n";
		st = st + "Your Default Publishing Method is: " + EcoreServerLauncher.changePropagation.getDefultPublishMethod() + "\n";
		if(EcoreServerLauncher.changePropagation.getDefultSubscribeMethod().equals("OnSchedule"))
			st = st + "Your Default Subscribing Method is: " + EcoreServerLauncher.changePropagation.getDefultSubscribeMethod() + " ("+ EcoreServerLauncher.changePropagation.getScheduleTime() +  ") \n";
		else 
			st = st + "Your Default Subscribing Method is: " + EcoreServerLauncher.changePropagation.getDefultSubscribeMethod() + "\n" ;
				
		try {
			initialHistory(st);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public void receiveClientResources(String message) throws JSONException{
		int i = message.indexOf("Resources");
		System.out.println("i is: " + i);
		message = message.substring(9);
		
		Resource[] resourceList = new Gson().fromJson(message, Resource[].class);
		System.out.println("Resource count is: " + resourceList.length);
		
		System.out.println("First Resource is: " + resourceList[0].resourceID);	
		
		
		// Write History Log :)
		String st = "Selected resources: ";
		Boolean flag = false ; 
		for(Resource r: resourceList) {
			EcoreServerLauncher.changePropagation.addResource(r);
			if(flag)
				st += ", " ;
			st = st + r.getResourceID() ;
			flag = true;
		}
		st += "\n";
		
		try {
			writeHistory(st);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	public void receiveClientPropagation(String message) throws JSONException{
		int i = message.indexOf("Propagate");
		System.out.println("i is: " + i);
		message = message.substring(9);
		
		Propagation[] propagationList = new Gson().fromJson(message, Propagation[].class);
		System.out.println("Propagation count is: " + propagationList.length);
		
		System.out.println("First Propagation is: " + propagationList[0].resourceID);	
		
				
		// Write History Log :)
		String st = "Your selected propagation strategies: \n";
		for(Propagation p: propagationList) {
			EcoreServerLauncher.changePropagation.addPropagation(p);
			if(p.getpublishStrategy()==null)
				st = st + "-" + p.getResourceID() + "=> Publish: ----- ";
			else
				st = st + "-" + p.getResourceID() + "=> Publish: " + p.getpublishStrategy() ;
			if(p.getsubscribeStrategy()==null)
				st = st + ", Subscribe: ----- \n";
			else if(p.getsubscribeStrategy().equals("OnSchedule"))
				st = st + ", Subscribe: " + p.getsubscribeStrategy() + " (" + p.getscheduleTime() + ") \n" ;
			else
				st = st + ", Subscribe: " + p.getsubscribeStrategy() + " \n";
		}
		st = st + "************************* Changes History ************************* \n" ;
		try {
			writeHistory(st);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/******************* Added by Mohammadreza Sharbaf to support collaborative modeling ****************/
	public void receiveOfflineSocket(String message) throws JSONException {
		int i = message.indexOf("OnClose");
		System.out.println("i is: " + i);
		message = message.substring(7);
		
		SocketPacket[] socketPacketList = new Gson().fromJson(message, SocketPacket[].class);
		System.out.println("socketPacketList count is: " + socketPacketList.length);
		
		
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocketOffline] " + socketPacketList.length + " new Change Operations received ...");
		for(SocketPacket sp: socketPacketList) {
			
			String json = new Gson().toJson(sp);	
			receiveOnlineSocket(json.toString());
			
		}
	}
	
	
	
	public void receiveOnlineSocket(String message) throws JSONException {

		// Avoid to execute information messages 
		if(message.charAt(0)!='{')
			return ; 

		
		SocketPacket sp = new Gson().fromJson(message, SocketPacket.class); 
		String rClientID = sp.getClientID();
		String rResourceID = sp.getResourceID();
		String rResourceAddress = sp.getResourceAddress(); 
		String rOperation = sp.getOperation();
		String rElementID = sp.getElementID();
		String rElementName = sp.getElementName();
		LocalTime rCTime = sp.getClientTime();
		int rSTime = sp.getServerTime(); 

		
		// Avoid to execute own-operation 
//		if(rClientID.equals("Client1")) {
		
		if(rClientID.equals(EcoreServerLauncher.changePropagation.getClientID())) {
			if(rOperation.contains("applyLabelEdit"))
				System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Re-execute own update operation (Client1)");
			else {
				System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Return for own operation (Client1)");
				return ;
			}
		}
//		if(rClientID.equals(EcoreServerLauncher.changePropagation.getClientID())) {
//		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Return for own operation (Client1)");
//		return ; 
//	}
		
		
		// Avoid to execute a received operation twice
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Add operation: " + rOperation); 
		EcoreOperationActionHandler.lastOperation.add(rOperation);
		EcoreOperationActionHandler.lastNewIDs.add(rElementID);
		
//		if(EcoreOperationActionHandler.lastOperation.contains(rOperation)) {
//			System.out.println("[Eclipse3-->EcoreModelSourceLoader-->ReceivedSocket] Remove operation: " + rOperation); 
//			EcoreOperationActionHandler.lastOperation.remove(rOperation);
//			return ; 
//		} 
//		else {
//			System.out.println("[Eclipse3-->EcoreModelSourceLoader-->ReceivedSocket] Add operation: " + rOperation); 
//			EcoreOperationActionHandler.lastOperation.add(rOperation);
//			EcoreOperationActionHandler.lastOperation.add(rOperation);
//		}
		
		
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Recieved clientID: " + rClientID);
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Recieved resourceID: " + rResourceID);
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Recieved resourceAddress: " + rResourceAddress);
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Recieved operation: " + rOperation);
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Recieved elementID: " + rElementID);
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Recieved ClientTime: " + rCTime);
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Recieved ServerTime: " + rSTime);
		
		
		//****************** Detect Operation *****************
        Operation nop = null ; 
        if(rOperation.contains("applyLabelEdit")) {
            nop = new Gson().fromJson(rOperation, ApplyLabelEditOperation.class); 
            System.out.println("applyLabelEdit Operation: " + nop.toString()); 
            
			// Write History Log :)
            String st = "";
            if(rClientID.equals(EcoreServerLauncher.changePropagation.getClientID()))
            	;
            else 
            {
            	if(rClientID.equals("Server"))
	            	st = " * Conflict => CoMPers undid your Update (or Delete) action for " + rElementName + " (ID: " + rElementID + ") in " + rResourceID + ", The executed operation was: " + rOperation + "\n";
	            else 
	            	st = " * " + rClientID + " Updated " + rElementName + " (ID: " + rElementID + ") in " + rResourceID + ", The executed operation was: " + rOperation + "\n";
				try {
					EcoreModelSourceLoader.writeHistory(st);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
        }
        else if(rOperation.contains("changeBounds")) {
            nop = new Gson().fromJson(rOperation, ChangeBoundsOperation.class); 
            System.out.println("changeBounds Operation: " + nop.toString()); 
            
        	String st = " * " + rClientID + " Changed Bounds of " + rElementName + " (ID: " + rElementID + ") in " + rResourceID + ", The executed operation was: " + rOperation + "\n";
        	try {
        		EcoreModelSourceLoader.writeHistory(st);
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	} 
        } 
        else if(rOperation.contains("changeContainer")) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(GPoint.class, new GPointImplInstanceTypeAdapter());
            Gson gson = builder.create();

        	nop = gson.fromJson(rOperation, ChangeContainerOperation.class); 
            System.out.println("changeContainer Operation: " + nop.toString()); 
            
        	String st = " * " + rClientID + " Changed Container of " + rElementName + " (ID: " + rElementID + ") in " + rResourceID + ", The executed operation was: " + rOperation + "\n";
        	try {
        		EcoreModelSourceLoader.writeHistory(st);
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	} 
        } 
        else if(rOperation.contains("changeRoutingPoints")) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(GPoint.class, new GPointImplInstanceTypeAdapter());
            Gson gson = builder.create();

            nop = gson.fromJson(rOperation, ChangeRoutingPointsOperation.class); 
            System.out.println("changeRoutingPoints Operation: " + nop.toString()); 
        } 
        else if(rOperation.contains("compound")) {
            nop = new Gson().fromJson(rOperation, CompoundOperation.class); 
            System.out.println("compound Operation: " + nop.toString()); 
        } 
        else if(rOperation.contains("createEdge")) {
            nop = new Gson().fromJson(rOperation, CreateEdgeOperation.class); 
            System.out.println("createEdge Operation: " + nop.toString()); 
            
            
			// Save Data for Conflict Detection
			ElementInfo element = new ElementInfo(rElementID, rElementName, rOperation, "");
			try {
				EcoreServerLauncher.elementInfoDao.addElement(element);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
			
			// Write History Log :)
            String st = "";
            if(rClientID.equals("Server"))
            	st = " * Conflict => CoMPers undid your Delete action for " + rElementName + " (ID: " + rElementID + ") in " + rResourceID + ", The executed operation was: " + rOperation + "\n";
            else 
            	st = " * " + rClientID + " Added " + rElementName + " (ID: " + rElementID + ") in " + rResourceID + ", The executed operation was: " + rOperation + "\n";
			try {
				EcoreModelSourceLoader.writeHistory(st);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        } 
        else if(rOperation.contains("createNode")) {      		        		
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(GPoint.class, new GPointImplInstanceTypeAdapter());
            Gson gson = builder.create();
    		
            nop = gson.fromJson(rOperation, CreateNodeOperation.class); 
            System.out.println("createNode Operation: " + nop.toString()); 
            
            String ts = gson.toJson(nop);
            System.out.println("new Operation is: " + ts); 
            
            
			// Save Data for Conflict Detection
			ElementInfo element = new ElementInfo(rElementID, rElementName, rOperation, "");
			try {
				EcoreServerLauncher.elementInfoDao.addElement(element);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
			// Write History Log :)
            String st = "";
            if(rClientID.equals("Server"))
            	st = " * Conflict => CoMPers undid your Delete action for " + rElementName + " (ID: " + rElementID + ") in " + rResourceID + ", The executed operation was: " + rOperation + "\n";
            else 
            	st = " * " + rClientID + " Added " + rElementName + " (ID: " + rElementID + ") in " + rResourceID + ", The executed operation was: " + rOperation + "\n";
			try {
				EcoreModelSourceLoader.writeHistory(st);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        } 
        else if(rOperation.contains("cut")) {
            nop = new Gson().fromJson(rOperation, CutOperation.class); 
            System.out.println("cut Operation: " + nop.toString()); 
        } 
        else if(rOperation.contains("deleteElement")) {
            nop = new Gson().fromJson(rOperation, DeleteOperation.class); 
            System.out.println("deleteElement Operation: " + nop.toString()); 
            
            
			for(ElementInfo EI : EcoreServerLauncher.elementInfoDao.getAllElements()) {
				if(EI.getCreateOp().contains(rElementID)) {
					
		        	String newOp = "{\"elementIds\":[\"" + EI.getElementID() + "\"],\"kind\":\"deleteElement\"}" ;
		            
//					Write History Log :)
		            String st = " * Inconsistency locally resolved when we deleted (ID: " + rElementID + ") in Resource4. To resolve, we executed this operation: " + newOp + "\n";
					try {
						EcoreModelSourceLoader.writeHistory(st);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
            
			// Write History Log :)
            String st = "";
            if(rClientID.equals("Server"))
            	st = " * Conflict => CoMPers undid your Add action for " + rElementName + " (ID: " + rElementID + ") in " + rResourceID + ", The executed operation was: " + rOperation + "\n";
            else 
            	st = " * " + rClientID + " Deleted " + rElementName + " (ID: " + rElementID + ") in " + rResourceID + ", The executed operation was: " + rOperation + "\n";
			try {
				EcoreModelSourceLoader.writeHistory(st);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        } 
        else if(rOperation.contains("layout")) {
            nop = new Gson().fromJson(rOperation, LayoutOperation.class); 
            System.out.println("layout Operation: " + nop.toString()); 
        } 
        else if(rOperation.contains("paste")) {
            nop = new Gson().fromJson(rOperation, PasteOperation.class); 
            System.out.println("paste Operation: " + nop.toString()); 
        } 
        else if(rOperation.contains("reconnectEdge")) {
            nop = new Gson().fromJson(rOperation, ReconnectEdgeOperation.class); 
            System.out.println("reconnectEdge Operation: " + nop.toString()); 
        } 

        
        //***************** Detect Conflict for Offline changes ***************
        checkOfflineConflict(rResourceID, rElementID, rOperation);
        
        
        //***************** Execute Operation ******************
	    String newrResourceAddress = rResourceAddress; //.replace("eclipse3", "eclipse1");
	    
//	    if(EcoreServerLauncher.changePropagation.getLocalTest()==true)
	    newrResourceAddress = rResourceAddress.replace("eclipse3", "eclipse1");
	    
//        rResourceID.replace("eclipse2", "eclipse1");
//        rResourceID.replace("eclipse3", "eclipse1");
//        rResourceID.replace("eclipse4", "eclipse1");
        
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Updated resourceID: " + newrResourceAddress);
        
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Before Dispatch execution ...");
		
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] mygModelStata ID is: " + mygModelState.getClientId());
		
		ActionMessage actionMessage = new ActionMessage(newrResourceAddress, nop);
		
//		EcoreEditorContext editorContext = new EcoreEditorContext(modelState, modelServerAccess);
//		modelState.setEditorContext(editorContext);
		
////		RequestModelAction action = new RequestModelAction();
//		loadSourceModel(myAction, mygModelState); 

		myModelState.loadSourceModels();
		
        actionDispatcher.dispatch(actionMessage);
		
		System.out.println("[Eclipse1-->EcoreModelSourceLoader-->ReceivedSocket] Dispatch executed successfully .... !!!!!");
		
	}
	
	
	public static void initialHistory(String st) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(logFile);
                OutputStreamWriter isr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {          
	        		isr.write(st);	            
	        		isr.close();
	        }
    }
	
    public static void writeHistory(String st) throws IOException {

    	System.out.println("Start writing History");
    	    	
    	
//        FileOutputStream writer = new FileOutputStream(logFile);
//        String st = "You connected as " + EcoreServerLauncher.changePropagation.getClientID() + "\n";
//        
//        st = st + "Your selected resources are: " + EcoreServerLauncher.changePropagation.getResourceList().toString();
//        writer.write((st).getBytes());
//        writer.close();
    	
        try (FileOutputStream fos = new FileOutputStream(logFile,true);
                OutputStreamWriter isr = new OutputStreamWriter(fos, 
                        StandardCharsets.UTF_8)) {
//        	
//            Gson gson = new Gson();
//            gson.toJson(this.mySemanticIndex, isr);
           
        	isr.append(st);
            
            isr.close();
            System.out.println("Finish writing History ...");
        }
    }

    
    public static void sendOfflineSocket() {
    	List<SocketPacket> spList = EcoreServerLauncher.changePropagation.getOfflineOperation();
    	if(spList.size()>0) {
    		new SendChangeOfflineThread(spList);
    	}
    	EcoreServerLauncher.changePropagation.clearOperationQueue();
    	
    }
    
    
    public static void checkOfflineConflict(String resourceID, String elementID, String op) {
    	
    	if(EcoreServerLauncher.changePropagation.getPublishMethod(resourceID).equals("Offline")) {
    		
	    	List<SocketPacket> spList = EcoreServerLauncher.changePropagation.getOfflineOperation();
	    	if(spList.size()>0) {
	    		
	    		if(op.contains("deleteElement")) {
	    			for(SocketPacket sp : spList) {
	    				if(sp.getElementID().equals(elementID)) {
	    					if(sp.getOperation().contains("applyLabelEdit")) {
	    						
	    						EcoreServerLauncher.changePropagation.removeOperationOfQueue(sp);
	    						
//	    						Write History Log :)
	    			            String st = " * Conflict => CoMPers removed your Update action for (ID: " + sp.getElementID() + ") based on the execution of new received operation: " + op + "\n";
	    			            
	    						try {
	    							EcoreModelSourceLoader.writeHistory(st);
	    						} catch (IOException e) {
	    							// TODO Auto-generated catch block
	    							e.printStackTrace();
	    						} 
	    						
	    					}
	    				}
	    				
	    				if(sp.getOperation().contains(elementID)) {
	    					if(sp.getOperation().contains("createNode")) {
	    						
	    						EcoreServerLauncher.changePropagation.removeOperationOfQueue(sp);
	    						
//	    						Write History Log :)
	    			            String st = " * Conflict => CoMPers removed your Add action for (ID: " + sp.getElementID() + ") based on the execution of new received operation: " + op + "\n";
	    			            
	    						try {
	    							EcoreModelSourceLoader.writeHistory(st);
	    						} catch (IOException e) {
	    							// TODO Auto-generated catch block
	    							e.printStackTrace();
	    						} 
	    						
	    					}
	    				}
	    					
	    			}
	    		}
	    	
	    		
	    	}
	    	
	    	
	    }	
    }
    
    
    
    
    
    
    
}
    
    

