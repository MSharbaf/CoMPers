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
package org.eclipse.emfcloud.ecore.modelserver;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponse;
import org.eclipse.emfcloud.modelserver.emf.common.ModelController;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelServerRoutingV1;
import org.eclipse.emfcloud.modelserver.emf.common.SchemaController;
import org.eclipse.emfcloud.modelserver.emf.common.ServerController;
import org.eclipse.emfcloud.modelserver.emf.common.SessionController;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.CodecsManager;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.javalin.Javalin;
import io.javalin.http.Context;

import com.google.inject.Inject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.server.operations.*;
import org.eclipse.glsp.server.actions.ActionDispatcher;
import org.eclipse.glsp.server.actions.ActionHandler;
import org.eclipse.glsp.server.actions.ActionMessage;
import org.eclipse.glsp.server.features.directediting.ApplyLabelEditOperation;
import org.eclipse.glsp.server.internal.action.DefaultActionDispatcher;
import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.model.ModelStateProvider;
import org.eclipse.glsp.server.operations.Operation; 

public class EcoreModelServerRouting extends ModelServerRoutingV1 {
	

	protected final CodecsManager codecsManager;

	@Inject
	public EcoreModelServerRouting(final Javalin javalin, final ModelResourceManager resourceManager,
			final ModelController modelController, final SchemaController schemaController,
			final ServerController serverController, final SessionController sessionController,
			final CodecsManager codecsManager) {
		super(javalin, resourceManager, modelController, schemaController, serverController, sessionController);
		this.codecsManager = codecsManager;
		
		
//		javalin.ws("/websocket", ws -> {
//
//            ws.onConnect((ctx) -> {
//            	System.out.println("The sever is Connected");
//            	System.out.println("Wellcome Server: " + ctx.session.getRemoteAddress().toString()); 
//            });
//
//            ws.onMessage((ctx) -> {
//            	if(ctx.message().isEmpty())
//            		System.out.println("Message is Null");
//            	
//            	System.out.println("Received message in Client2 is: "+ ctx.message());
//            	receiveSocket(ctx.message()); 
//            });
//            
//            ws.onClose(ctx -> System.out.println("Cloosed :" + ctx.session.getRemoteAddress().toString()));
//
//            ws.onError(ctx -> System.out.println("Erroored"));
//            
//        });
	}

	protected void createEcoreResources(final Context ctx) {
		getResolvedFileUri(ctx, ModelServerPathParametersV1.MODEL_URI).ifPresent(modelUri -> {
			String nsUri = "";
			if (ctx.queryParamMap().containsKey(EcoreModelServerPathsParameters.NS_URI)) {
				nsUri = ctx.queryParamMap().get(EcoreModelServerPathsParameters.NS_URI).get(0);
			}
			String nsPrefix = "";
			if (ctx.queryParamMap().containsKey(EcoreModelServerPathsParameters.NS_PREFIX)) {
				nsPrefix = ctx.queryParamMap().get(EcoreModelServerPathsParameters.NS_PREFIX).get(0);
			}
			boolean result = ((EcoreModelResourceManager) resourceManager).addNewEcoreResources(modelUri, nsUri,
					nsPrefix);
			ctx.json(result ? JsonResponse.success() : JsonResponse.error());
		});
	}

	protected void createEcoreNotation(final Context ctx) {
		getResolvedFileUri(ctx, ModelServerPathParametersV1.MODEL_URI).ifPresent(modelUri -> {
			EObject result = ((EcoreModelResourceManager) resourceManager).addEnotationResource(modelUri);
			try {
				ctx.json(result != null ? JsonResponse.success(JsonCodec.encode(codecsManager.encode(ctx, result)))
						: JsonResponse.error());
			} catch (EncodingException e) {
				e.printStackTrace();
			}
		});
	}

	protected void deleteEcoreResources(final Context ctx) {
		getResolvedFileUri(ctx, ModelServerPathParametersV1.MODEL_URI).ifPresent(modelUri -> {
			try {
				((EcoreModelResourceManager) resourceManager).deleteEcoreResources(modelUri);
				ctx.json(JsonResponse.success());
			} catch (IOException e) {
				ctx.json(JsonResponse.error());
			}
		});
	}

	protected void deleteEnotationResource(final Context ctx) {
		getResolvedFileUri(ctx, ModelServerPathParametersV1.MODEL_URI).ifPresent(modelUri -> {
			try {
				((EcoreModelResourceManager) resourceManager).deleteEnotationResource(modelUri);
				ctx.json(JsonResponse.success());
			} catch (IOException e) {
				ctx.json(JsonResponse.error());
			}
		});
	}

	@Override
	public void bindRoutes() {
		javalin.routes(this::endpoints);
	}

	private void endpoints() {
		path(ModelServerPathsV1.BASE_PATH, this::apiEndpoints);
	}

	private void apiEndpoints() {
		get(EcoreModelServerPaths.ECORE_CREATE, this::createEcoreResources);
		get(EcoreModelServerPaths.ENOTATION_CREATE, this::createEcoreNotation);
		delete(EcoreModelServerPaths.ECORE_DELETE, this::deleteEcoreResources);
		delete(EcoreModelServerPaths.ENOTATION_DELETE, this::deleteEnotationResource);
	}
	
	
	
//	/******************* Added by Mohammadreza Sharbaf to support collaborative modeling ****************/
//	public void receiveSocket(String message) throws JSONException {
//
//		// Avoid to execute information message 
//		if(message.charAt(0)!='{')
//			return ; 
//		
//		
//		JSONObject json = new JSONObject(message.toString());
//				
//    	String rClientID = (String) json.get("clientID");
//		String rResourceID = (String) json.get("resourceID"); 
//		String rOperation = (String) json.get("operation");
//		
//		
//		// Avoid to execute own-operation 
//		if(rClientID.equals("Client1"))
//			return ; 
//		
//		
//		
//		System.out.println("Recieved clientID: " + rClientID);
//		System.out.println("Recieved resourceID: " + rResourceID);
//		System.out.println("Recieved Operation: " + rOperation);
//		
//		
//		
//		//****************** Detect Operation *****************
//        Operation nop = null ; 
//        if(rOperation.contains("applyLabelEdit")) {
//            nop = new Gson().fromJson(rOperation, ApplyLabelEditOperation.class); 
//            System.out.println("applyLabelEdit Operation: " + nop.toString()); 
//        }
//        else if(rOperation.contains("changeBounds")) {
//            nop = new Gson().fromJson(rOperation, ChangeBoundsOperation.class); 
//            System.out.println("changeBounds Operation: " + nop.toString()); 
//        } 
//        else if(rOperation.contains("changeContainer")) {
//            GsonBuilder builder = new GsonBuilder();
//            builder.registerTypeAdapter(GPoint.class, new GPointImplInstanceTypeAdapter());
//            Gson gson = builder.create();
//
//        	nop = gson.fromJson(rOperation, ChangeContainerOperation.class); 
//            System.out.println("changeContainer Operation: " + nop.toString()); 
//        } 
//        else if(rOperation.contains("changeRoutingPoints")) {
//            GsonBuilder builder = new GsonBuilder();
//            builder.registerTypeAdapter(GPoint.class, new GPointImplInstanceTypeAdapter());
//            Gson gson = builder.create();
//
//            nop = gson.fromJson(rOperation, ChangeRoutingPointsOperation.class); 
//            System.out.println("changeRoutingPoints Operation: " + nop.toString()); 
//        } 
//        else if(rOperation.contains("compound")) {
//            nop = new Gson().fromJson(rOperation, CompoundOperation.class); 
//            System.out.println("compound Operation: " + nop.toString()); 
//        } 
//        else if(rOperation.contains("createEdge")) {
//            nop = new Gson().fromJson(rOperation, CreateEdgeOperation.class); 
//            System.out.println("createEdge Operation: " + nop.toString()); 
//        } 
//        else if(rOperation.contains("createNode")) {      		        		
//            GsonBuilder builder = new GsonBuilder();
//            builder.registerTypeAdapter(GPoint.class, new GPointImplInstanceTypeAdapter());
//            Gson gson = builder.create();
//    		
//            nop = gson.fromJson(rOperation, CreateNodeOperation.class); //******
//            System.out.println("createNode Operation: " + nop.toString()); 
//            
//            String ts = gson.toJson(nop);
//            System.out.println("new Operation is: " + ts); 
//        } 
//        else if(rOperation.contains("cut")) {
//            nop = new Gson().fromJson(rOperation, CutOperation.class); 
//            System.out.println("cut Operation: " + nop.toString()); 
//        } 
//        else if(rOperation.contains("deleteElement")) {
//            nop = new Gson().fromJson(rOperation, DeleteOperation.class); 
//            System.out.println("deleteElement Operation: " + nop.toString()); 
//        } 
//        else if(rOperation.contains("layout")) {
//            nop = new Gson().fromJson(rOperation, LayoutOperation.class); 
//            System.out.println("layout Operation: " + nop.toString()); 
//        } 
//        else if(rOperation.contains("paste")) {
//            nop = new Gson().fromJson(rOperation, PasteOperation.class); 
//            System.out.println("paste Operation: " + nop.toString()); 
//        } 
//        else if(rOperation.contains("reconnectEdge")) {
//            nop = new Gson().fromJson(rOperation, ReconnectEdgeOperation.class); 
//            System.out.println("reconnectEdge Operation: " + nop.toString()); 
//        } 
//
//        
//        //***************** Execute Operation ******************
//	    String newResourceID = rResourceID.replace("eclipse3", "eclipse1");
//	    
////        rResourceID.replace("eclipse2", "eclipse1");
////        rResourceID.replace("eclipse3", "eclipse1");
////        rResourceID.replace("eclipse4", "eclipse1");
//        
//		System.out.println("Updated resourceID: " + newResourceID);
//        
//		ActionMessage actionMessage = new ActionMessage(newResourceID, nop);
//      
////		DefaultActionDispatcher actionDispatcher = new DefaultActionDispatcher(); 
////		ActionDispatcher actionDispatcher ; 
////        actionDispatcher.dispatch(actionMessage);
//        
//		System.out.println("Before Dispatch execution ...");
//        
//
////		GModelState modelState = modelStateProvider.create(params.getClientSessionId());
//		
////        final DefaultActionDispatcher defaultActionDispatcher;
////        defaultActionDispatcher.dispatch(actionMessage);
//		Optional<GModelState> modelState = EcoreModelServerClient.myModelStateProvider.getModelState(newResourceID);
//        
//		EcoreModelServerClient.myActionDispatcher.dispatch(actionMessage);
//		
//		System.out.println("Dispatch executed successfully .... !!!!!");
//        
//        //***************** Execute Operation ******************
////		ActionHandler actionHandler ;
//		
////		actionHandler.execute(nop, modelState.get());
////		EcoreOperationActionHandler().executeAction(nop, modelState);
//        
//        
//		//*********************** Check *************************
//        
////	    if (nop instanceof CreateOperation) {
////	    	CreateOperation oop = (CreateOperation) nop ;
////			System.out.println("Create Operation: Element Type ID is " + oop.getElementTypeId());
////	    }
////
////	    if (nop instanceof ApplyLabelEditOperation) {
////	    	ApplyLabelEditOperation oop = (ApplyLabelEditOperation) nop ;
////	    	System.out.println("ApplyLabelEditOperation: Text is " + oop.getText());
////	    	System.out.println("ApplyLabelEditOperation: Id.tostring is " + oop.ID.toString());
////	    } 	
//	}
	

}
