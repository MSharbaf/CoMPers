/********************************************************************************
 * Copyright (c) 2019-2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.ecore.glsp;

import java.util.Optional;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.elk.alg.layered.options.LayeredMetaDataProvider;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.change.ChangePackage;
import org.eclipse.emfcloud.ecore.enotation.EnotationPackage;
import org.eclipse.emfcloud.ecore.glsp.handler.ChangePropagationHandler;
import org.eclipse.emfcloud.ecore.glsp.handler.EcoreOperationActionHandler;
import org.eclipse.emfcloud.ecore.glsp.util.ElementInfoDao;
import org.eclipse.emfcloud.ecore.modelserver.EcoreModelServerClient;
import org.eclipse.emfcloud.ecore.modelserver.GPointImplInstanceTypeAdapter;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.layout.ElkLayoutEngine;
import org.eclipse.glsp.server.actions.ActionMessage;
import org.eclipse.glsp.server.features.directediting.ApplyLabelEditOperation;
import org.eclipse.glsp.server.launch.DefaultGLSPServerLauncher;
import org.eclipse.glsp.server.launch.GLSPServerLauncher;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class EcoreServerLauncher {

	private static final Logger LOG = Logger.getLogger(EcoreServerLauncher.class);

	private static final int DEFAULT_PORT = 5007;
	
	public static ChangePropagationHandler changePropagation ; 
    public static ElementInfoDao elementInfoDao;

	
//	public static Javalin javalin; 

	public static void main(String[] args) {
		
//		System.out.println("main is started ...");
//		
//		javalin = Javalin.create().start(6001);
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
////            	EcoreOperationActionHandler.receiveSocket(ctx.message()); 
//            });
//            
//            ws.onClose(ctx -> System.out.println("Cloosed :" + ctx.session.getRemoteAddress().toString()));
//
//            ws.onError(ctx -> System.out.println("Erroored"));
//            
//        });
//		
//		System.out.println("main after javalin ...");
		
		int port = getPort(args);
		configureLogger();
		registerEPackages();
		ElkLayoutEngine.initialize(new LayeredMetaDataProvider());
		GLSPServerLauncher launcher = new DefaultGLSPServerLauncher(new EcoreGLSPModule()); //getClientSessionManager
		
//		changePropagation = new ChangePropagationHandler();
		
		launcher.start("localhost", port);
		
		

		
	}

	private static int getPort(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if ("--port".contentEquals(args[i])) {
				return Integer.parseInt(args[i + 1]);
			}
		}
		LOG.info("The server port was not specified; using default port 5007");
		return DEFAULT_PORT;
	}

	public static void configureLogger() {
		Logger root = Logger.getRootLogger();
		if (!root.getAllAppenders().hasMoreElements()) {
			root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
		}
		root.setLevel(Level.INFO);
	}

	public static void registerEPackages() {
		EcorePackage.eINSTANCE.eClass();
		EnotationPackage.eINSTANCE.eClass();
		ChangePackage.eINSTANCE.eClass();
	}
	

}
