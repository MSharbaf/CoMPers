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
package org.eclipse.emfcloud.ecore.glsp.gmodel;

import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.ecore.glsp.EcoreModelIndex;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelState;
import org.eclipse.emfcloud.ecore.glsp.util.SemanticIndex;
import org.eclipse.glsp.graph.GModelElement;

public abstract class AbstractGModelFactory<T extends EObject, E extends GModelElement> {

	protected EcoreModelState modelState;

	public AbstractGModelFactory(EcoreModelState modelState) {
		this.modelState = modelState;
	}

	public abstract E create(T semanticElement);

	public <U extends E> Optional<U> create(T semanticElement, Class<U> clazz) {
		return Optional.ofNullable(create(semanticElement)).filter(clazz::isInstance).map(clazz::cast);
	}

	protected String toId(EObject semanticElement) {
		/*** A new getSemanticId without object number ****/
		String id = modelState.getIndex().getSemanticId(semanticElement).orElse(null);
		if (id == null) {

//			id = UUID.randomUUID().toString();
			
//			/*** Added by Mohammadreza Sharbaf***/
//			String semElement = semanticElement.toString(); 
//			for(SemanticIndex s : EcoreModelIndex.mySemanticIndex) {
//				if(semElement.contains(s.element)) {
//					id = s.id ;
//					modelState.getIndex().indexSemantic(id, semanticElement, true);
//					return id ; 
//				}
//			}
			
			/*** Added by Mohammadreza Sharbaf***/
			id = modelState.getIndex().computeId(semanticElement);
			
			modelState.getIndex().indexSemantic(id, semanticElement);
		}
		return id;

	}
}
