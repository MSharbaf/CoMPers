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
package org.eclipse.emfcloud.ecore.glsp.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emfcloud.ecore.glsp.EcoreServerLauncher;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelSourceLoader;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelState;
import org.eclipse.emfcloud.ecore.glsp.operationhandler.ModelserverAwareOperationHandler;
import org.eclipse.emfcloud.ecore.glsp.util.ElementInfo;
import org.eclipse.emfcloud.ecore.glsp.util.Resource;
import org.eclipse.emfcloud.ecore.glsp.util.SocketPacket;
import org.eclipse.glsp.server.actions.Action;
import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.operations.DeleteOperation;
import org.eclipse.glsp.server.operations.Operation;
import org.eclipse.glsp.server.operations.OperationActionHandler;
import org.eclipse.glsp.server.operations.OperationHandler;
import org.eclipse.glsp.server.operations.gmodel.CompoundOperationHandler;
import org.eclipse.glsp.server.protocol.GLSPServerException;

import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.time.LocalTime;
import com.google.gson.Gson;

public class EcoreOperationActionHandler extends OperationActionHandler {
	
//	@Inject
//	protected ModelSourceLoader sourceModelLoader;

	public static List<String> lastOperation = new ArrayList<String>() ; 
	public static List<String> lastNewIDs = new ArrayList<String>() ; 
	
	public static List<String> deleteConflict = new ArrayList<String>();
	
	@Override
	public List<Action> executeAction(Operation operation, GModelState modelState) {
		 
		System.out.println("[Eclipse1-->EcoreOperationActionHandler] Start operation execution ...");
		// Disable the special handling for CreateOperation, as we don't register
		// 1 handler per element type to create.
		Optional<? extends OperationHandler> operationHandler = operationHandlerRegistry.get(operation);
		if (operationHandler.isPresent()) {			
			
//			System.out.println("mySemanticIndex size before execution is: " + EcoreModelIndex.mySemanticIndex.size()) ;
			long startTime = System.currentTimeMillis();
			
			List<Action> ls = null ; 
			GModelState delModelState = modelState;
			EcoreModelState ecoreModelState = EcoreModelState.getModelState(modelState);
			System.out.println("[Eclipse1-->EcoreOperationACtionHandler: SemanticIndex is: " + ecoreModelState.getIndex().getSemanticIndexSize());
			System.out.println("mySemanticIndex is: " + ecoreModelState.getIndex().mySemanticIndex.size());

			
			System.out.println("Time is: " + (System.currentTimeMillis()-startTime) );


//			String op = new Gson().toJson(operation);
//			System.out.println("[Eclipse3-->EcoreOperationACtionHandler-->executeAction] Execute operation: " + op);

//			List<Action> ls = executeHandler(operation, operationHandler.get(), modelState);
//			System.out.println("Action list is: " + ls.toString());
			

			
//			System.out.println("mySemanticIndex size after execution is: " + EcoreModelIndex.mySemanticIndex.size()) ;

			
			/*********** Added by Mohammadreza Sharbaf to support change propagation ************/
//			boolean flag = false ; 
			String op = new Gson().toJson(operation);
			System.out.println("[Eclipse1-->EcoreOperationACtionHandler: operation is: " + op) ;
			String kind = operation.getKind().toString() ;
			System.out.println("[Eclipse1-->EcoreOperationACtionHandler: operation kind is: " + kind) ;
			if(!kind.equals("requestModel")) {
				
//				String newID = "";
//				String status = "";  //Add, Update, Delete
				
				//Conflict management in this client 
//				localConflictManagement(operation, modelState);
				
				ls = executeHandler(operation, operationHandler.get(), modelState);
				
				
				System.out.println("Time after execution is: " + (System.currentTimeMillis()-startTime) );

				
				try {
					TimeUnit.MILLISECONDS.sleep(350);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("Time after sleep is: " + (System.currentTimeMillis()-startTime) );

				
				//Received from Server
				if(lastOperation.contains(op)) {
					int opIndex = lastOperation.indexOf(op);
					String newElementID = lastNewIDs.get(opIndex);
					lastOperation.remove(op);
					lastNewIDs.remove(opIndex);
					
					if(kind.equals("deleteElement")) {
						int size = ecoreModelState.getIndex().mySemanticIndex.size();
						int idIndex = op.indexOf("Id");
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler] mySemanticIndex size is: " + size + ", and Id Index is" + idIndex);
						String opElementID = op.substring(16, 52);
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler] opElementId is" + opElementID);
////						for(SemanticIndex s: ecoreModelState.getIndex().mySemanticIndex) {
////							if(s.id.equals(opElementID)) {
////								ecoreModelState.getIndex().mySemanticIndex.remove(s);
////								break; 
////							}
////						}
////						ecoreModelState.getIndex().removeSemanticIndexId(opElementID);
//						
//						
////						String oldID = op.substring(16, 52);
////						newID = oldID;
////						System.out.println("[Eclipse3-->EcoreOperationACtionHandler] opElementId is " + oldID);
//						
//						ecoreModelState.getIndex().removeMySemanticIndexId(opElementID);
//						ecoreModelState.getIndex().removeSemanticIndexId(opElementID);
	
						ecoreModelState.getIndex().removeBothSemanticIndex(opElementID);
						
						modelState = delModelState; 
						System.out.println("Time after delete is: " + (System.currentTimeMillis()-startTime) );
						
//						ecoreModelState.getModelServerAccess().getNotationModel().notify();
						
//					    sourceModelLoader.loadSourceModel(EcoreModelSourceLoader.myAction, modelState);
						
//						ecoreModelState.loadSourceModels();
						
					} else if(kind.equals("applyLabelEdit") || kind.equals("compound")) {
						int size = ecoreModelState.getIndex().mySemanticIndex.size();
						int idIndex = op.indexOf("Id");
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler] mySemanticIndex size is: " + size + ", and Id Index is " + idIndex);
						
						String oldID = "";
						if(kind.equals("compound")) {
							int i = op.indexOf("Id");
							oldID = op.substring(i+4, i+40);
						}else {
							oldID = op.substring(12, 48);
						}
						
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler] (oldID) opElementId is " + oldID);
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler] ID from server is " + newElementID);
						
//						try {
//							TimeUnit.MILLISECONDS.sleep(350);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}				
						String generatedID = ecoreModelState.getIndex().getLastMySemanticIndex();

						
						//Update old Object with new Object
						if(!oldID.equals(generatedID))
							ecoreModelState.getIndex().updateBothSemanticIndecies(oldID, generatedID);
						
//						for(SemanticIndex s: ecoreModelState.getIndex().mySemanticIndex) {
//							if(s.id.equals(oldID)) {
//								ecoreModelState.getIndex().mySemanticIndex.remove(s);
//								break; 
//							}
//						}						
						
						// Update Id of new added item in mySemanticIndex and semanticIndex
//						ecoreModelState.getIndex().setLastMySemanticIndex(newElementID);
//						ecoreModelState.getIndex().updateSemanticIndexId(opElementID, newElementID);
//						for(SemanticIndex s: ecoreModelState.getIndex().mySemanticIndex) {
//							if(s.id.equals(opElementID)) {
//								ecoreModelState.getIndex().mySemanticIndex.remove(s);
//								break; 
//							}
//						}
						
						ecoreModelState.getModelServerAccess().save();
						System.out.println("Time after update is: " + (System.currentTimeMillis()-startTime) );

					}
					else if(kind.equals("createNode")||kind.equals("createEdge")) {
						//Nothing, the operation execution automatically added it to mySemanticIndex 
//						 Update Id of new added item in mySemanticIndex and semanticIndex
//						try {
//							TimeUnit.MILLISECONDS.sleep(350);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}				
						String generatedID = ecoreModelState.getIndex().getLastMySemanticIndex();
						ecoreModelState.getIndex().addBothSemanticIndecies(newElementID, generatedID);
						
//						ecoreModelState.getIndex().setLastMySemanticIndex(newElementID);
//						ecoreModelState.getIndex().updateSemanticIndexId(opElementID, newElementID);
						
						ecoreModelState.getModelServerAccess().save();
						System.out.println("Time after add is : " + (System.currentTimeMillis()-startTime) );
						
					}
					
					try {
						ecoreModelState.getIndex().writeMySemanticIndex();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println("Time after wirte is: " + (System.currentTimeMillis()-startTime) );

//					ecoreModelState.getModelServerAccess().getModel();
//					ecoreModelState.getModelServerAccess().getNotationModel();
//					ecoreModelState.getModelServerAccess().save();
//					ecoreModelState.getModelServerAccess().setLayout(ecoreModelState, modelState.getRoot());
					
					System.out.println("Time after save is: " + (System.currentTimeMillis()-startTime) );

					
					System.out.println("[Eclipse1-->EcoreOperationACtionHandler: Execute refresh of subscription listener ...");
					EcoreModelSourceLoader.myModelState.getEditorContext().getEcoreFacade().getDiagram();
					EcoreModelSourceLoader.mySubscriptionListener.refresh();
//					EcoreModelSourceLoader.mySubscriptionListener.myRefresh();


//					ecoreModelState.getModelServerAccess().save();
//					modelServerAccess.save();
					
					System.out.println("Time after model refresh is: " + (System.currentTimeMillis()-startTime) );

					
				}
				// Executed by this client
				else {
					String newID = "";
					String newName = "";
					
			    	String resourceID = "";
			    	for(Resource r : EcoreServerLauncher.changePropagation.getResourceList()){
			    		if(modelState.getClientId().contains(r.getecoreAddress()) && modelState.getClientId().contains(r.getprojectName())) {
			    			resourceID = r.getResourceID() ; 
			    			break; 
			    		}
			    	}
					
//					Add operations
					if(kind.equals("createNode")||kind.equals("createEdge")) {//||kind.equals("")||kind.equals("")||kind.equals("")) {	
//						try {
////							TimeUnit.SECONDS.sleep(1);
//							TimeUnit.MILLISECONDS.sleep(350);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler: mySemanticIndex is: " + ecoreModelState.getIndex().mySemanticIndex.size());
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler: Added Element newId is: " + ecoreModelState.getIndex().getLastMySemanticIndex());
						newID = ecoreModelState.getIndex().getLastMySemanticIndex();
						newName = ecoreModelState.getIndex().getLastMySemanticName();
						
						
						// Save Data for Conflict Detection
						ElementInfo element = new ElementInfo(newID, newName, op, "");
						try {
							EcoreServerLauncher.elementInfoDao.addElement(element);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						// Write History Log :)
						String st = " * You Added " + newName + " (ID: " + newID + ") in " + resourceID + ", The executed operation was: " + op + "\n";
						try {
							EcoreModelSourceLoader.writeHistory(st);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
						ecoreModelState.getModelServerAccess().save();
						System.out.println("Time after create is: " + (System.currentTimeMillis()-startTime) );

					}
					
//					Update Operations
					else if(kind.equals("applyLabelEdit") || kind.equals("compound")) {//||kind.equals("")||kind.equals("")||kind.equals("")) {
//						try {
////							TimeUnit.SECONDS.sleep(1);
//							TimeUnit.MILLISECONDS.sleep(350);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						String oldID = "";
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler: mySemanticIndex is: " + ecoreModelState.getIndex().mySemanticIndex.size());
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler: Updated Element  newId is: " + ecoreModelState.getIndex().getLastMySemanticIndex());
						if(kind.equals("compound")) {
							int i = op.indexOf("Id");
							oldID = op.substring(i+4, i+40);
							newID = ecoreModelState.getIndex().getLastMySemanticIndex();
							newName = ecoreModelState.getIndex().getLastMySemanticName();
						}else {
							newID = ecoreModelState.getIndex().getLastMySemanticIndex();
							newName = ecoreModelState.getIndex().getLastMySemanticName();
							oldID = op.substring(12, 48);
						}
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler] oldID (from Operation) is " + oldID);
						
						//Update old Object with new Object
						if(!oldID.equals(newID))
							ecoreModelState.getIndex().updateBothSemanticIndecies(oldID, newID);	
						newID = oldID ; 
						
						// Write History Log :)
						String st = " * You Updated " + newName + " (ID: " + newID + ") in " + resourceID + ", The executed operation was: " + op + "\n";
						try {
							EcoreModelSourceLoader.writeHistory(st);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
						ecoreModelState.getModelServerAccess().save();
						System.out.println("Time after update is: " + (System.currentTimeMillis()-startTime) );

					}
					
//					Delete Operations
					else if(kind.equals("deleteElement")) {//||kind.equals("")||kind.equals("")||kind.equals("")||kind.equals("")) {

						String oldID = op.substring(16, 52);
						newID = oldID;
						System.out.println("[Eclipse1-->EcoreOperationACtionHandler] opElementId is " + oldID);
//						ecoreModelState.getIndex().removeMySemanticIndexId(oldID);
//						ecoreModelState.getIndex().removeSemanticIndexId(oldID);
						
						newName = ecoreModelState.getIndex().getMySemanticName(oldID);
						ecoreModelState.getIndex().removeBothSemanticIndex(oldID);
						
						
						for(ElementInfo EI : EcoreServerLauncher.elementInfoDao.getAllElements()) {
							if(EI.getCreateOp().contains(oldID)) {
								
					        	String newOp = "{\"elementIds\":[\"" + EI.getElementID() + "\"],\"kind\":\"deleteElement\"}" ;
					        	
					            
//								Write History Log :)
					            String st = " * Inconsistency locally resolved when we deleted (ID: " + oldID + ") in Resource4. To resolve, we executed this operation: " + newOp + "\n";
								try {
									EcoreModelSourceLoader.writeHistory(st);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
							}
						}
						
						
						
						if(deleteConflict.contains(op)) {
//							Write History Log :)
							deleteConflict.remove(op);
				            String st = " * Conflict locally detected => we deleted " + newName + " (ID: " + newID + ") in " + resourceID + ", to resolve the conflict. The executed operation was: " + op + "\n";
							try {
								EcoreModelSourceLoader.writeHistory(st);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
						}  
						
						else {
							// Write History Log :)
							String st = " * You Deleted " + newName + " (ID: " + newID + ") in " + resourceID + ", The executed operation was: " + op + "\n";
							try {
								EcoreModelSourceLoader.writeHistory(st);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
						}
						
						
//						newName = "";
						
						System.out.println("Time after delete is: " + (System.currentTimeMillis()-startTime) );
						
//					    modelState = modelStateProvider.getModelState(modelState.getClientId());
					    
//						ecoreModelState.getModelServerAccess().removeEReference(ecoreModelState, null);
//						ecoreModelState.loadSourceModels();
//					    sourceModelLoader.loadSourceModel(EcoreModelSourceLoader.myAction, modelState);

//					    modelState.saveIsDone();


					}
					
					try {
						ecoreModelState.getIndex().writeMySemanticIndex();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println("Time after wirte is: " + (System.currentTimeMillis()-startTime) );

//					ecoreModelState.getModelServerAccess().getModel();
//					ecoreModelState.getModelServerAccess().getNotationModel();
//					ecoreModelState.getModelServerAccess().save();
//					ecoreModelState.getModelServerAccess().setLayout(ecoreModelState, modelState.getRoot());

					
					System.out.println("Time after model save is: " + (System.currentTimeMillis()-startTime) );

					
					System.out.println("[Eclipse1-->EcoreOperationACtionHandler: Execute refresh of subscription listener ...");
					EcoreModelSourceLoader.myModelState.getEditorContext().getEcoreFacade().getDiagram();
					EcoreModelSourceLoader.mySubscriptionListener.refresh();
//					EcoreModelSourceLoader.mySubscriptionListener.myRefresh();
					
					System.out.println("Time after refresh is: " + (System.currentTimeMillis()-startTime) );

					
					

					
			        SocketPacket sp = new SocketPacket(EcoreServerLauncher.changePropagation.getClientID(), resourceID, modelState.getClientId(), newID, newName, op, LocalTime.now(), 0);
			        
			        EcoreServerLauncher.changePropagation.addOperationToQueue(sp);
			        					
//					sendSocket("Client1", operation, modelState);
//					sendSocket("Client1", newID, op, modelState);
					
					System.out.println("Time after send socket is: " + (System.currentTimeMillis()-startTime) );

				}
				
				ecoreModelState.getModelServerAccess().save();
//				System.out.println("[Eclipse1-->EcoreOperationACtionHandler: Execute refresh of subscription listener ...");
//				EcoreModelSourceLoader.mySubscriptionListener.refresh();
			}
			else {
				ls = executeHandler(operation, operationHandler.get(), modelState);
			}
			
//			try {
//				ecoreModelState.getIndex().writeMySemanticIndex();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			// reload models
//			ecoreModelState.loadSourceModels();
//			System.out.println("[Eclipse1-->EcoreOperationACtionHandler: Execute refresh of subscription listener ...");
//			EcoreModelSourceLoader.mySubscriptionListener.refresh();
			
//			Diagram diagram = ecoreFacade.getDiagram();
			
//			ecoreModelState.getEcoreFacade().getDiagram();
			
			return ls ; 
			
		}	
		return none();
	}
	

	private void localConflictManagement(Operation operation, GModelState modelState) {
		String op = new Gson().toJson(operation);
		String kind = operation.getKind().toString() ;
		
		if(kind.equals("deleteElement")) {
//			int idIndex = op.indexOf("Id");
			String opElementID = op.substring(16, 52);
			System.out.println("[Eclipse1-->ConflictManagement] opElementId is" + opElementID);
//			EcoreModelState ecoreModelState = EcoreModelState.getModelState(modelState);

			for(ElementInfo EI : EcoreServerLauncher.elementInfoDao.getAllElements()) {
				if(EI.getCreateOp().contains(opElementID)) {
					
		        	String newOp = "{\"elementIds\":[\"" + EI.getElementID() + "\"],\"kind\":\"deleteElement\"}" ;
		            Operation newOperation = new Gson().fromJson(newOp, DeleteOperation.class); 
		            deleteConflict.add(newOp);
					
					executeAction(newOperation, modelState);
					
					/////////////////////////////////////////////////////
//		            EcoreModelState ecoreModelState = EcoreModelState.getModelState(modelState);
//					String newName = ecoreModelState.getIndex().getMySemanticName(EI.getElementID());
//					ecoreModelState.getIndex().removeBothSemanticIndex(EI.getElementID());
//					
////						Write History Log :)
//						deleteConflict.remove(newOp);
//			            String st = " * Inconsistency locally resolved when we deleted " + newName + " (ID: " + EI.getElementID() + ") in Resource4. To resolve, we executed this operation: " + newOp + "\n";
//						try {
//							EcoreModelSourceLoader.writeHistory(st);
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} 
					
					
				}
			}

		}

//		  // Save Data for Undoing Operations
//		  if(sp.getOperation().contains("createNode") || sp.getOperation().contains("createEdge")) { 
//			  ElementInfo element = new ElementInfo(sp.getElementID(), sp.getElementName(), sp.getOperation(), "");
//			  try {
//				  Main.elementInfoDao.addElement(element);
//			  } catch (IOException e) {
//				  // TODO Auto-generated catch block
//				  e.printStackTrace();
//			  }
//		  } else if(sp.getOperation().contains("applyLabelEdit")) {
//			  ///////////////////////////
//			  System.out.println("ConflictChecking: applylabelEdit" + sp.getOperation());
//			  Main.elementInfoDao.getElementByID(sp.getElementID()).setElementName(sp.getElementName());
//			  Main.elementInfoDao.getElementByID(sp.getElementID()).setLastEditOp(sp.getOperation());
//		  } else if(sp.getOperation().contains("deleteElement")) {
//			  Main.elementInfoDao.getElementByID(sp.getElementID()).setElementName(sp.getElementName());
//		  }
		
		
		
	}

	@Override
	protected List<Action> executeHandler(Operation operation, OperationHandler handler, GModelState gModelState) {
		if (handler instanceof ModelserverAwareOperationHandler || handler instanceof CompoundOperationHandler) {
			handler.execute(operation, gModelState);
		}
		return none();
	}
	

}