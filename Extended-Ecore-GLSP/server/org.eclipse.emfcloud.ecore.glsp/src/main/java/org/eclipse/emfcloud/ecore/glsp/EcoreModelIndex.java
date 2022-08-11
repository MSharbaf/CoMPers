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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.ecore.enotation.Diagram;
import org.eclipse.emfcloud.ecore.enotation.Edge;
import org.eclipse.emfcloud.ecore.enotation.NotationElement;
import org.eclipse.emfcloud.ecore.glsp.util.EcoreConfig.Types;
import org.eclipse.emfcloud.ecore.glsp.util.EcoreEdgeUtil;
import org.eclipse.emfcloud.ecore.glsp.util.SemanticIndex;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.impl.GModelIndexImpl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EcoreModelIndex extends GModelIndexImpl {

	/***Added By Mohammadreza Sharbaf ***/
    private final String semanticIndexFile = "ecore-glsp/server/org.eclipse.emfcloud.ecore.glsp/src/main/resources/semanticIndex.json";
    public List<SemanticIndex> mySemanticIndex = new ArrayList<SemanticIndex>();

    //    private final String semanticIndexFile = "src\\main\\resources\\semanticIndex.json";
    //	private final String semanticIndexFile = "semanticIndex.txt";
//	public BiMap<String, String> mySemanticIndex;
//	public BiMap<String, EObject> semanticIndex;
	
	
	private BiMap<String, EObject> semanticIndex;
	private BiMap<EObject, NotationElement> notationIndex;
	private Set<String> bidirectionalReferences;
	private BiMap<String, Edge> inheritanceEdges;
	

	private EcoreModelIndex(EObject target) {
		super(target);
		semanticIndex = HashBiMap.create();
		notationIndex = HashBiMap.create();
		bidirectionalReferences = new HashSet<>();
		inheritanceEdges = HashBiMap.create();
		
//		mySemanticIndex = new ArrayList<SemanticIndex>();
		try {
			readMySemanticIndex();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static EcoreModelIndex get(GModelElement element) {
		EObject root = EcoreUtil.getRootContainer(element);
		EcoreModelIndex existingIndex = (EcoreModelIndex) EcoreUtil.getExistingAdapter(root, EcoreModelIndex.class);
		return Optional.ofNullable(existingIndex).orElseGet(() -> (create(element)));
	}

	public static EcoreModelIndex create(GModelElement element) {
		return new EcoreModelIndex(EcoreUtil.getRootContainer(element));
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return super.isAdapterForType(type) || EcoreModelIndex.class.equals(type);
	}

	public void indexSemantic(String id, EObject semanticElement) {

		semanticIndex.putIfAbsent(id, semanticElement);

//		System.out.println("[Eclipse1-->EcoreModelIndex-->indexSemantic] before flag for: " + semanticElement.toString());
		
		//		/*** Added by Mohammadreza Sharbaf***/
		boolean flag = true ; 
		String semElement = semanticElement.toString(); 
		for(SemanticIndex s : mySemanticIndex) {
			if(semElement.contains(s.element)) {
//				id = s.id ;
				flag = false ; 
				break; 
			}
		}
		
		//Original function only includes this operation
//		semanticIndex.putIfAbsent(id, semanticElement);
		
		/*** Added by Mohammadreza Sharbaf***/
		if(flag) {
			int index = semanticElement.toString().indexOf( '(' );
			String element = semanticElement.toString().substring(index);
			
			SemanticIndex SI = new SemanticIndex(id, semanticElement.getClass().toString(), element);
			mySemanticIndex.add(SI);
			
//			System.out.println("[Eclipse1-->EcoreModelIndex-->indexSemantic] semanticElement is: " + semanticElement.toString());
			
			try {
				writeMySemanticIndex();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void indexNotation(NotationElement notationElement) {
		
//		System.out.println("[Eclipse1-->EcoreModelIndex] NotationElement is: " + notationElement.toString());
		if (notationElement.getSemanticElement() != null) {
			EObject semanticElement = notationElement.getSemanticElement().getResolvedElement();
			if(semanticElement != null) {
				notationIndex.put(semanticElement, notationElement);
				
				/*****Check if semanticElement is added by other user to the semanticIndex list, so used that id, else we need to randomUUID and update server file****/
	//			semanticIndex.inverse().putIfAbsent(semanticElement, UUID.randomUUID().toString());
				
	//			System.out.println("[Eclipse1-->EcoreModelIndex-->indexNotation] before flag");
	
	//			/*** Added by Mohammadreza Sharbaf***/
				boolean flag = true ; 
				String id = UUID.randomUUID().toString();
				String semElement = semanticElement.toString(); 
				for(SemanticIndex s : mySemanticIndex) {
					if(semElement.contains(s.element)) {
						id = s.id ;
						flag = false ; 
						break ; 
					}
				}
	
	//			semanticIndex.inverse().putIfAbsent(semanticElement, id);
				
				/*** Added by Mohammadreza Sharbaf ***/
				if(flag) {
					semanticIndex.inverse().putIfAbsent(semanticElement, id);
					
					int index = semanticElement.toString().indexOf( '(' );
					String element = semanticElement.toString().substring(index);
					
					SemanticIndex SI = new SemanticIndex(id, semanticElement.getClass().toString(), element);
					mySemanticIndex.add(SI);
					
	//				System.out.println("[Eclipse1-->EcoreModelIndex-->indexNotation] semanticElement is: " + semanticElement.toString());
					
					try {
						writeMySemanticIndex();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else if (notationElement.getType() != null && notationElement.getType().equals(Types.INHERITANCE)) {
			indexInheritanceEdge((Edge) notationElement);
		}

		if (notationElement instanceof Diagram) {
			((Diagram) notationElement).getElements().forEach(this::indexNotation);
		}
	}

	public Optional<EObject> getSemantic(String id) {
		return Optional.ofNullable(semanticIndex.get(id));
	}

	/**** Override it for String ***/
	public Optional<String> getSemanticId(EObject semanticElement) {
		return Optional.ofNullable(semanticIndex.inverse().get(semanticElement));
	}

	public <T extends EObject> Optional<T> getSemantic(String id, Class<T> clazz) {
		return safeCast(Optional.ofNullable(semanticIndex.get(id)), clazz);
	}

	public Optional<EObject> getSemantic(GModelElement gModelElement) {
		return getSemantic(gModelElement.getId());
	}

	public <T extends EObject> Optional<T> getSemantic(GModelElement gModelElement, Class<T> clazz) {
		return getSemantic(gModelElement.getId(), clazz);
	}

	public Optional<NotationElement> getNotation(EObject semanticElement) {
		return Optional.ofNullable(notationIndex.get(semanticElement));
	}

	public <T extends NotationElement> Optional<T> getNotation(EObject semanticElement, Class<T> clazz) {
		return safeCast(getNotation(semanticElement), clazz);
	}

	public Optional<NotationElement> getNotation(String id) {
		return getSemantic(id).flatMap(this::getNotation);
	}

	public <T extends NotationElement> Optional<T> getNotation(String id, Class<T> clazz) {
		return safeCast(getNotation(id), clazz);
	}

	public Optional<NotationElement> getNotation(GModelElement gModelElement) {
		return getNotation(gModelElement.getId());
	}

	public <T extends NotationElement> Optional<T> getNotation(GModelElement element, Class<T> clazz) {
		return safeCast(getNotation(element), clazz);
	}

	private <T> Optional<T> safeCast(Optional<?> toCast, Class<T> clazz) {
		return toCast.filter(clazz::isInstance).map(clazz::cast);
	}

	public String add(EObject eObject) {
		if (eObject instanceof GModelElement) {
			return ((GModelElement) eObject).getId();
		}
		String id = null;
		if (eObject instanceof NotationElement) {
			EObject semanticElement = ((NotationElement) eObject).getSemanticElement().getResolvedElement();
			id = add(semanticElement);
			notationIndex.putIfAbsent(semanticElement, (NotationElement) eObject);
		} else {
			id = getSemanticId(eObject).orElse(null);
			if (id == null) {
				
//				id = UUID.randomUUID().toString();
//				
//				/*** Added by Mohammadreza Sharbaf***/
//				String semElement = eObject.toString(); 
//				for(SemanticIndex s : EcoreModelIndex.mySemanticIndex) {
//					if(semElement.contains(s.element)) {
//						id = s.id ;
//						indexSemantic(id, eObject, true);
//						return id ; 
//					}
//				}
			
				/*** Added by Mohammadreza Sharbaf***/
				id = computeId(eObject);
				
				indexSemantic(id, eObject);
				
//				/*** Added by Mohammadreza Sharbaf ***/
//				int index = eObject.toString().indexOf( '(' );
//				String element = eObject.toString().substring(index);
//				
//				SemanticIndex SI = new SemanticIndex(id, eObject.getClass().toString(), element);
//				mySemanticIndex.add(SI);
//				try {
//					writeMySemanticIndex();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
		return id;
	}

	public void remove(EObject eObject) {
		
		System.out.println("Remove started ....");
		
		if (eObject instanceof NotationElement) {
			EObject semanticElement = ((NotationElement) eObject).getSemanticElement().getResolvedElement();
			notationIndex.remove(semanticElement);
			remove(semanticElement);
			return;
		} else if (eObject instanceof GModelElement) {
			// do nothing;
			return;
		}
		semanticIndex.inverse().remove(eObject);
		
		/*** Added by Mohammadreza Sharbaf***/
		String semElement = eObject.toString(); 
		for(SemanticIndex s : mySemanticIndex) {
			if(semElement.contains(s.element)) {
				mySemanticIndex.remove(s);
				
//				System.out.println("[Eclipse1-->EcoreModelIndex-->remove] semanticElement is: " + semElement.toString());
				
				try {
					writeMySemanticIndex();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return ; 
			}
		}
	}
	
	public Set<String> getBidirectionalReferences() {
		return bidirectionalReferences;
	}

	public void indexInheritanceEdge(Edge inheritanceEdge) {
		Optional<String> sourceId = getElementId(inheritanceEdge.getSource());
		Optional<String> targetId = getElementId(inheritanceEdge.getTarget());
		String inheritanceEdgeId = EcoreEdgeUtil.getInheritanceEdgeId(sourceId.get(), targetId.get());
		inheritanceEdges.putIfAbsent(inheritanceEdgeId, inheritanceEdge);
	}

	public Optional<Edge> getInheritanceEdge(String elementId) {
		return Optional.ofNullable(inheritanceEdges.get(elementId));
	}

	public Optional<Edge> getInheritanceEdge(EClass eClass, EClass eSuperType) {
		String sourceId = semanticIndex.inverse().get(eClass);
		String targetId = semanticIndex.inverse().get(eSuperType);
		String inheritanceEdgeId = EcoreEdgeUtil.getInheritanceEdgeId(sourceId, targetId);
		return Optional.ofNullable(inheritanceEdges.get(inheritanceEdgeId));
	}

	protected Optional<String> getElementId(NotationElement notationElement) {
		EObject semantic = notationIndex.inverse().get(notationElement);
		return Optional.of(semanticIndex.inverse().get(semantic));
	}

    public void writeMySemanticIndex() throws IOException {

    	System.out.println("Start writing, semanticIndex size is: " + this.semanticIndex.size());
    	System.out.println("Start writing, mySemanticIndex size is: " + this.mySemanticIndex.size());
    	    	
        try (FileOutputStream fos = new FileOutputStream(semanticIndexFile);
                OutputStreamWriter isr = new OutputStreamWriter(fos, 
                        StandardCharsets.UTF_8)) {
        	
            Gson gson = new Gson();
            gson.toJson(this.mySemanticIndex, isr);
           
            
            isr.close();
//            System.out.println("Finish writing ...");
        }
    }
    
    public void readMySemanticIndex() throws IOException {
        Gson gson = new GsonBuilder().create();
        Path path = new File(semanticIndexFile).toPath();
        
        try (Reader reader = Files.newBufferedReader(path, 
                StandardCharsets.UTF_8)) {
            
        	
            SemanticIndex[] semanticIndexList = gson.fromJson(reader, SemanticIndex[].class);
            
        	if(semanticIndexList!=null) {
	            Arrays.stream(semanticIndexList).forEach( s -> {
	            	this.mySemanticIndex.add(s);
	            });
        	}
        }
    }
    
    public String computeId(EObject eObject) {
    	String id = UUID.randomUUID().toString();
		String semElement = eObject.toString(); 
		for(SemanticIndex s : this.mySemanticIndex) {
			if(semElement.contains(s.element)) {
				id = s.id ;
				break ; 
			}
		}
		return id ; 
    }
    
    public String getLastMySemanticIndex() {
    	return mySemanticIndex.get(mySemanticIndex.size()-1).getID();
    }
    
    public String getLastMySemanticName() {
    	String name = "";
    	
    	String element = mySemanticIndex.get(mySemanticIndex.size()-1).getElement();
    	int endIndex = element.indexOf(')');
    	name = element.substring(7, endIndex);
    	
    	return name; 
    }
    
    public String getMySemanticName(String id) {
    	String name = "";
	  	for(SemanticIndex s: mySemanticIndex) {
			if(s.id.equals(id)) {
				String element = s.getElement();
				int endIndex = element.indexOf(')');
				name = element.substring(7, endIndex);
				return name; 
			}
	  	}	
    	return name; 
    }
    
    public void setLastMySemanticIndex(String newID) {
    	mySemanticIndex.get(mySemanticIndex.size()-1).setID(newID);
    }
    

    public void addBothSemanticIndecies(String serverID, String newID) {
    	EObject ob = semanticIndex.get(newID);
		
    	// Remove newID from semanticIndex and mySemanticIndex
    	semanticIndex.remove(newID);
		removeMySemanticIndexId(newID);
		
		//Add object with serverID in semanticIndex and mySemanticIndex
		semanticIndex.inverse().putIfAbsent(ob, serverID);
		
		int index = ob.toString().indexOf( '(' );
		String element = ob.toString().substring(index);
		
		SemanticIndex SI = new SemanticIndex(serverID, ob.getClass().toString(), element);
		mySemanticIndex.add(SI);
    }

    
    public void updateBothSemanticIndecies(String oldID, String newID) {
    	EObject ob = semanticIndex.get(newID);
		
    	// Remove newID from semanticIndex and mySemanticIndex
    	semanticIndex.remove(newID);
		removeMySemanticIndexId(newID);
		
//		EObject semanticElement = ((NotationElement) ob).getSemanticElement().getResolvedElement();
//		notationIndex.remove(semanticElement);
		
		//Update object for oldID in semanticIndex and mySemanticIndex
		updateSemanticIndexObject(ob, oldID);
		updateMySemanticIndexObject(ob, oldID);
		
//		indexSemantic(oldID, ob);
//		onFullUpdate(ob);
    	
    }
    
    public boolean updateMySemanticIndexObject(EObject newObj, String id) {
    	
		int index = newObj.toString().indexOf( '(' );
		String element = newObj.toString().substring(index);
    	
    	for(SemanticIndex s: mySemanticIndex) {
			if(s.id.equals(id)) {
				s.setElement(element);
				return true;
			}
		}
    	return false ; 
    }
    
    public void updateSemanticIndexObject(EObject newObj, String id) {
		semanticIndex.remove(id);
		semanticIndex.putIfAbsent(id, newObj);
//		System.out.println("new Object is: " + newObj.toString() + " , Old id is: " + id.toString());
    }
    
    public boolean removeMySemanticIndexId(String id) {
    	int index = -1; 
    	for(int i=0; i<mySemanticIndex.size(); i++) {
    		if(mySemanticIndex.get(i).id.equals(id)) {
    			index = i; 
    			break;
    		}	
    	}
    	if(index!=-1) {
    		mySemanticIndex.remove(index);
    		return true ; 
    	}else {
    		return false ; 
    	}
    	
    	
//    	for(SemanticIndex s: mySemanticIndex) {
//			if(s.id.equals(id)) {
//				mySemanticIndex.remove(s);
//				return true; 
//			}
//		}
//    	return false; 
    }
    
    public void removeSemanticIndexId(String id) {
		semanticIndex.remove(id);
    }
    
    public void removeBothSemanticIndex(String id){
    	EObject object = semanticIndex.get(id);
    	remove(notationIndex.get(object));
    }
    
    public int getSemanticIndexSize() {
    	return semanticIndex.size(); 
    }
    

}
