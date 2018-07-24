package localGen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestLocalGen {

	public static void main(String[] args) {
		
		ArrayList<String> paths = new ArrayList<String>();
		
		// File containing the paths to the configuration lists to be tested
		
		String path = "data/fork-insight/files.txt";

		try {
			
			// Retrieves the paths
			
			Files.lines(Paths.get(path))
			.map(line -> line.split("\n")) 		
			.flatMap(Arrays::stream) 					
			.distinct() 	
			.forEach(paths::add);
		
		
			for (String file : paths) {
				
				// Creates a formal context
				
				FormalContext formalContext= new FormalContext("fork-insight/" + file);
	
				long currentTime = java.lang.System.currentTimeMillis();
				
				/****************** DEFINITION OF CONCEPTS FOR TESTING *****************/
				
				Concept currentConcept = new Concept(); 
				
				/****************** TESTING *****************/		
				
				int cn = 0;
				
				int k = 0;
				
				for (int i = 0 ; i < 100 ; i++) {
					
					// Takes a random object
					
					k = (int) (1 + (Math.random() * (formalContext.getNumberOfObjects() - 1)));
	
					// Retrieves its introducer
					currentConcept.setObjIntroducer(Integer.toString(k),formalContext);
					
					// Computes its conceptual neighbourhood
					// Of all concepts except the top-concept
					
					if (!currentConcept.getIntent().contains("") && !currentConcept.getIntent().isEmpty()) {
						cn += upperCover(currentConcept, formalContext) + lowerCover(currentConcept, formalContext) + 1;
					} else {
						System.out.println("TOP, dodged.");
					}
				}
				
				long timeExec = java.lang.System.currentTimeMillis() - currentTime;
				
				System.out.println("Name:\t\t\t\t\t" + file);
				System.out.println("Number of objects:\t\t\t" + formalContext.getNumberOfObjects());
				System.out.println("Number of attributes:\t\t\t" + formalContext.getAttributes().size());
				System.out.println("Number of generated concepts:\t\t" + (cn+1));
				System.out.println("Average time of computation / step:\t" + (timeExec / 100) + " ms");

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	
	
	/**
	 * This function computes the upper-cover of a concept in the AOC-poset.
	 * 
	 * @param currentConcept the current concept
	 * @param formalContext the formal context from which is computed the upper neighbours
	 * @return the size of the upper cover of the current concept
	 */
	public static int upperCover(Concept currentConcept, FormalContext formalContext){
		
		ConceptualCover cover = new ConceptualCover(formalContext);
		
		/*************************************************/
		/************ COMPUTING CANDIDATES AC ************/
		/*************************************************/
		
		// candidateAttributeConcepts receives the attributes introduced in potential AC candidates
		// Potential AC candidates are the attribute-concepts which introduce the attributes present in the intent of the current concept
		
		ArrayList<String> candidateAttributeConcepts = new ArrayList<String>();		
		candidateAttributeConcepts.addAll(currentConcept.getIntent()); 

		// Will store attributes introduced in final candidates AC
		
		ArrayList<String> attributeConcepts = new ArrayList<String>();			
		
		// If an attribute is introduced in the current concept, it cannot be a candidate attribute concept
		// These attributes are removed from the potential attribute concept candidates
		
		candidateAttributeConcepts.removeIf(a -> formalContext.isAttIntroducedIn(a, currentConcept));	
		
		// Adds the remaining candidates into the set of attribute concepts ...
	
		attributeConcepts.addAll(candidateAttributeConcepts);
		
		// ... and removes from the set of attribute concepts the attributes which do not correspond to a direct upper attribute concept
		
		ArrayList<String> F = new ArrayList<String>();

		// For each candidate attribute
		
		for (String a : candidateAttributeConcepts) {
			
			// If a is not removed from the candidates
			
			if (attributeConcepts.contains(a)) {
				
				// F receives the closure of a, i.e., the intent of the concept introducing a
				
				F.clear();
				F.addAll(formalContext.setObjClosure(formalContext.attClosure(a)));
				
				// a is removed from F, and F removed from AC
				F.remove(a);
				attributeConcepts.removeAll(F);
			}
		}
		
		// attributeConcepts kept the attributes introduced in the lowest upper attributes-concepts
				
		// Adds them in the upper cover
		
		for (String a : attributeConcepts) {
			cover.addCandidateAC(a);
		}

			
		/*************************************************/
		/************ COMPUTING CANDIDATES OC ************/
		/*************************************************/
		
		// R <- {{a}'' | a in AC}
		// R receives the set of intents of candidates attribute-concepts
			
		ArrayList<ArrayList<String>> R = new ArrayList<ArrayList<String>>();
		R = cover.getListOfIntents();		
		
				
		// Some objects 
		
		ArrayList<String> O = new ArrayList<String>(); 		
		
		// Stores all objects introduced in potential object-concepts
		
		Set<String> objectConcepts = new HashSet<String>();				
		
		// For each attribute-concept intents in R
		// Computes their closure
		// And remove the current concept extent to find introduced objects being candidate object-concepts
		
		for(ArrayList<String> I : R) {
			
			O.clear();
			O.addAll(formalContext.setAttClosure(I));
			O.removeAll(currentConcept.getExtent());
			objectConcepts.addAll(O);
		}
		
		// Keeps only the objects introduced in the super concept of the current one
		
		ArrayList<String> OC2 = new ArrayList<String>();

		for (String o : objectConcepts) {
			if (currentConcept.getIntent().containsAll(formalContext.objClosure(o))) {
				OC2.add(o);
			}
		}
				
		// Removes the candidates which are not the lowest ones
		
		ArrayList<String> OC3 = new ArrayList<String>();
		
		OC3.addAll(OC2);
		
		boolean isLowest;
		
		for (String o1 : OC2){
			
			isLowest = true;
			
			for (String o2 : OC2) {
				if (!o1.equals(o2) && formalContext.objClosure(o2).containsAll(formalContext.objClosure(o1)) && isLowest) {
					isLowest = false;
				}
			}
			if (!isLowest) {
				OC3.remove(o1);
			}
		}
				
		
		/************************************************/
		/********* MERGING CANDIDATES AC AND OC *********/
		/************************************************/
		
		
		
		ArrayList<ArrayList<String>> T = new ArrayList<ArrayList<String>>();
		
		// For each objects introduced in candidates object-concepts
		
		for (String o : OC3) {
			
			// Test "if introduced in super concept of current concept" removed
			
			T.clear();
			
			// Retrieves in T all attribute-concepts having o in their extent 
			// i.e., attribute concept having object concepts between them and the current concept
			
			for (ArrayList<String> S : R) {
				if (formalContext.setAttClosure(S).contains(o)) {
					T.add(S);
				}
			}
			
			if (T.size() > 0) {
				
				// T possesses attribute-concepts which have introducer of o between them and the current concept
				// Removes them from the cover
				
				for (ArrayList<String> t : T) {

					cover.removeConceptByIntent(t);
				}
				
				// Adds introducer of o as object-concepts of the upper cover
				
				cover.addCandidateOC(o);
			}
		}
		
	//	System.out.println("[" + cover.getNumberOfConcepts() + "] Upper cover:\n" + cover);
		
		return cover.getNumberOfConcepts();
	
	}
	
	/**
	 * This function computes the lower cover  of the current concept specified in parameter
	 * 
	 * @param currentConcept the current concept for which we want to compute the lower cover
	 * @param formalContext the formal context
	 * @return the size of the lower cover
	 */
	public static int lowerCover(Concept currentConcept, FormalContext formalContext){
		
		ConceptualCover cover = new ConceptualCover(formalContext);
		
		/*************************************************/
		/************ COMPUTING CANDIDATES OC ************/
		/*************************************************/
		
		// candiateObjectConcepts receives the objects potentially introduced in candidate object-concepts
		
		// Stores objects which can be candidates, i.e., the ones in the extent of the current concept
		
		ArrayList<String> candidateObjectConcepts = new ArrayList<String>();		
		candidateObjectConcepts.addAll(currentConcept.getExtent()); 
		
		// If an object is introduced in the current concept, it cannot be a candidate
		
		candidateObjectConcepts.removeIf(o -> formalContext.isObjIntroducedIn(o, currentConcept));		
	
		// Will store objects introduced in final candidates OC

		ArrayList<String> objectConcepts = new ArrayList<String>();			
		objectConcepts.addAll(candidateObjectConcepts);
		
		
		// for each object candidate
		
		ArrayList<String> F = new ArrayList<String>();
		
		for (String o : candidateObjectConcepts) {
			
			// If o is not removed from the candidates
			
			if (objectConcepts.contains(o)) {
				
				// F receives the concept introducing o
				
				F.clear();
				F.addAll(formalContext.setAttClosure(formalContext.objClosure(o)));
				
				// o is removed from F, and F removed from object-concepts
				
				F.remove(o);
				objectConcepts.removeAll(F);
			}
		}
		// objectConcepts kept the objects introduced in the greatest lower object-concepts
		// Thus, candidates concept in the lower cover of the current concept
				
		// Adds them in conceptual neighbourhood
		for (String o : objectConcepts) {
			cover.addCandidateOC(o);
		}
		
		
		/*************************************************/
		/************ COMPUTING CANDIDATES AC ************/
		/*************************************************/
		
		
		// R <- {{o}'' | o in OC}
		// R receives the set of extents of candidates object concepts
			
		ArrayList<ArrayList<String>> R = new ArrayList<ArrayList<String>>();
		R = cover.getListOfExtents();
	
		// Some attributes introduced in potential attribute-concepts
		
		ArrayList<String> A = new ArrayList<String>(); 		
		
		// Stock all attributes introduced in potential attributeConcepts
		
		Set<String> attributeConcepts = new HashSet<String>();				
		
		// For each object-concept extents in R
		// Computes their closure
		// And removes the current concept intent to find introduced attributes being candidate attribute-concepts
				
		
		for (ArrayList<String> E : R) {
			A.clear();
			A.addAll(formalContext.setObjClosure(E));
			A.removeAll(currentConcept.getIntent());
			attributeConcepts.addAll(A);
		}

		// Keep only the attributes introduced in the sub concept of the current one
		
		ArrayList<String> AC2 = new ArrayList<String>();
		
		for (String a : attributeConcepts) {
			if (currentConcept.getExtent().containsAll(formalContext.attClosure(a))) {
				AC2.add(a);
			}
		}
				
		// Remove the candidates which are not the greatest ones
		
		ArrayList<String> AC3 = new ArrayList<String>();
		AC3.addAll(AC2);
		
		boolean isGreatest = true;
		for (String a : AC2) {
			isGreatest = true;
			for (String a2 : AC2) {
				if (!a.equals(a2) && formalContext.attClosure(a2).containsAll(formalContext.attClosure(a))&& isGreatest) {
					isGreatest = false;
				}
			}
			if (!isGreatest) {
				AC3.remove(a);
			}
		}
		
		
		
		/************************************************/
		/********* MERGING CANDIDATES AC AND OC *********/
		/************************************************/
		
		
		
		ArrayList<ArrayList<String>> T = new ArrayList<ArrayList<String>>();
		
		// For each attribute introduced in candidate attribute concepts
		
		for (String a : AC3) {
			
			// if introduced in sub concept of current concept
//			if(cc.getExtent().containsAll(conf.attClosure(a))){
	
			T.clear();
				
			// Retrieves in T all object-concepts having a in their intent
				
			for (ArrayList<String> S : R) {
				if (formalContext.setObjClosure(S).contains(a)) {
						T.add(S);
				}
			}	
			
			if(T.size() > 0){
				
				// T possesses object-concepts which have an attribute concept between them and the current concept
				// Removes them from the lower cover
				
				for (ArrayList<String> t : T) {
				
					cover.removeConceptByExtent(t);
				}
					
				// Adds attribute concepts in the lower cover
				cover.addCandidateAC(a);
			}
				
			
//				
//			} 
//			else{
////					System.out.println("Delete candidate AC "+a);
//			}
		}
		
	//	System.out.println("[" + cover.getNumberOfConcepts() + "] Lower cover: \n" + cover);
		
		return cover.getNumberOfConcepts();
	
	}


	
	

}
