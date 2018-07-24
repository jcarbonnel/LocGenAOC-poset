package localGen;

import java.util.ArrayList;

/**
 * This class represents a conceptual cover, i.e., the super-concepts or the sub concept of a current concept.
 * The duality of FCA conceptual structures  allows to use this class for upper and lower cover.
 * 
 * @author Jessie Carbonnel
 *
 */
public class ConceptualCover {

	/************************* ATTRIBUTES *************************/

	
	/**
	 * List of direct super-concepts or sub-concepts
	 */
	private ArrayList<Concept> neighbours = new ArrayList<Concept>();
	
	/**
	 * Formal Context in which the neighbours are computed
	 */
	private FormalContext formalContext;
	
	
	/************************* CONSTRUCTORS *************************/

	/**
	 * Creates a new instance of ConceptualNeighbours
	 * 
	 * @param formalContext a formal context in which the neighbours are computed
	 */
	public ConceptualCover(FormalContext formalContext) {
		this.formalContext = formalContext;
	}
	
	/************************* GETTERS AND SETTERS *************************/

	/**
	 * Returns the size of the neighbourhood
	 * @return
	 */
	public int getNumberOfConcepts(){
		return neighbours.size();
	}
	
	/**
	 * Removes the concept having the attribute set i for intent from the neighbours
	 * 
	 * @param i an attribute set
	 */
	public void removeConceptByIntent(ArrayList<String> i) {
		
		// Retrieves the concept from the neighbour set
		
		Concept c = getConceptByIntent(i);
	
		// Removes the concept if it exists
		
		if (c != null) {
			neighbours.remove(getConceptByIntent(i));
		}
	}
	
	/**
	 * Removes the concept having the object set e for extent from the neighbours
	 * 
	 * @param e an object set
	 */
	public void removeConceptByExtent(ArrayList<String> e) {
		
		// Retrieves the concept from the neighbour set
		
		Concept c = getConceptByIntent(e);
		
		// Removes the concept if it exists

		if(c != null) {
			neighbours.remove(getConceptByExtent(e));
		}
	}	

	/**
	 * Adds a candidate attribute-concept to the neighbours.
	 * If the attribute is present in one of the current neighbours,
	 * then its attribute-concept is already present in the neighbours;
	 * 
	 * @param att the attribute introduced in the candidate attribute-concept
	 */
	public void addCandidateAC(String att) {
		
		// Checks if the attribute is not already corresponding to an attribute-concept
		
		boolean isCooccurrent = false;
		
		for (Concept c : neighbours) {
			if (c.getIntent().contains(att)) {
				isCooccurrent = true;
			}
		}
		
		// If the corresponding attribute-concept is not already in the neighbours, it is added.
		
		if (!isCooccurrent) {
			
			ArrayList<String> attClosure = new ArrayList<String>();
			attClosure.addAll(formalContext.attClosure(att));
			neighbours.add(new Concept(formalContext.setObjClosure(attClosure), attClosure));
			
		}
	}
	
	/**
	 * Adds a candidate object-concept to the neighbours.
	 * If the object is present in one of the current neighbours,
	 * then its object-concept is already present in the neighbours;
	 * 
	 * @param obj the object introduced in the candidate object-concept
	 */
	public void addCandidateOC(String obj) {
		
		// Checks if the object is not already corresponding to an object-concept
		
		boolean isCooccurent = false;
		
		for(Concept c : neighbours) {
		
			if (c.getExtent().contains(obj)) {
				isCooccurent = true;
			}
		}
		
		if (!isCooccurent) {
			
			ArrayList<String> objClosure = new ArrayList<String>();
			objClosure.addAll(formalContext.objClosure(obj));
			neighbours.add(new Concept(objClosure, formalContext.setAttClosure(objClosure)));
			
		}
	}
	
	/**
	 * Returns the concept in the list of neighbours having its intent corresponding to the attribute set i
	 * 
	 * @param i an attribute set
	 * @return the concept having i for intent, else null
	 */
	public Concept getConceptByIntent(ArrayList<String> i) {
		for (Concept c : neighbours) {
			if (c.getIntent().containsAll(i) && i.containsAll(c.getIntent())) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Returns the concept in the list of neighbours having its extent corresponding to the object set e
	 * 
	 * @param e an object set
	 * @return the concept having e for extent, else null
	 */
	public Concept getConceptByExtent(ArrayList<String> e) {
		for (Concept c : neighbours) {
			if (c.getExtent().containsAll(e) && e.containsAll(c.getExtent())) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * toString
	 */
	public String toString(){
		String s = "";
		for (Concept c : neighbours) {
			s += c.toString() + "\n";
		}
		return s;
	}
	
	/**
	 * Retrieves the list of intents of all neighbours
	 * 
	 * @return a list of list of attributes
	 */
	public ArrayList<ArrayList<String>> getListOfIntents() {
		
		ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
		
		for(int i = 0 ; i < neighbours.size() ; i++) {
			res.add(new ArrayList<String>());
			res.get(i).addAll(neighbours.get(i).getIntent());
		}
		return res;
	}
	
	/**
	 * Retrieves the list of extents of all neighbours
	 * 
	 * @return a list of list of objects represented by their index
	 */
	public ArrayList<ArrayList<String>> getListOfExtents() {
		
		ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
		
		for(int i = 0 ; i < neighbours.size() ; i++) {
			res.add(new ArrayList<String>());
			res.get(i).addAll(neighbours.get(i).getExtent());
		}
		return res;
	}
	
}
