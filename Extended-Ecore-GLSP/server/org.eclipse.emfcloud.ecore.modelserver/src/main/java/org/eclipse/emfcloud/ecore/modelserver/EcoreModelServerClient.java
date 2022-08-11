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

import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.modelserver.client.ModelServerClient;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.glsp.server.actions.ActionDispatcher;
import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.model.ModelStateProvider;

import okhttp3.Request;

public class EcoreModelServerClient extends ModelServerClient {

//	public static GModelState loadedModelState ;
	
//	/*********** Added By Mohammadreza Sharbaf *************/
//	public static ModelStateProvider myModelStateProvider;
//	public static ActionDispatcher myActionDispatcher;
	
	public EcoreModelServerClient(final String baseUrl) throws MalformedURLException {
		super(baseUrl);
	}
	
//	/*********** Added By Mohammadreza Sharbaf *************/
//	public EcoreModelServerClient(final String baseUrl, ModelStateProvider modelStateProvider, ActionDispatcher actionDispatcher) throws MalformedURLException {
//		super(baseUrl);
//		this.myModelStateProvider = modelStateProvider; 
//		this.myActionDispatcher = actionDispatcher;
//	}

	public CompletableFuture<Response<EObject>> createEcoreNotation(final String modelUri, final String format) {
		final Request request = new Request.Builder()
				.url(createHttpUrlBuilder(baseUrl + EcoreModelServerPaths.ENOTATION_CREATE)
						.addQueryParameter(ModelServerPathParametersV1.MODEL_URI, modelUri)
						.addQueryParameter(ModelServerPathParametersV1.FORMAT, checkedFormat(format)).build())
				.build();

		return makeCallAndParseDataField(request)
				.thenApply(resp -> resp.mapBody(body -> body.flatMap(b -> decode(b, checkedFormat(format)))))
				.thenApply(this::getBodyOrThrow);
	}


}
