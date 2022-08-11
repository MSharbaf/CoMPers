/********************************************************************************
 * Copyright (c) 2019-2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.ecore.glsp;

import org.eclipse.emfcloud.ecore.glsp.gmodel.GModelFactory;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelServerAccess;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelState;

public class EcoreEditorContext {
	private final ResourceManager resourceManager;
	private final GModelFactory gModelFactory;
	private final EcoreModelState modelState;

	public EcoreEditorContext(EcoreModelState modelState, EcoreModelServerAccess modelServerAccess) {
//		System.out.println("Start Ecore Eidtor Context");
		this.modelState = modelState;
//		System.out.println("model State clientID is: " + modelState.getClientId());
		gModelFactory = new GModelFactory(modelState);
		resourceManager = new ResourceManager(modelState, modelServerAccess); //*** uri
//		System.out.println("Finish Ecore Eidtor Context");
	}

	public EcoreFacade getEcoreFacade() {
		return resourceManager.getEcoreFacade();
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public GModelFactory getGModelFactory() {
		return gModelFactory;
	}

	public EcoreModelState getModelState() {
		return modelState;
	}

}
