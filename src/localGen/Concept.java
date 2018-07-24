package localGen;

import java.util.ArrayList;

/**
 * This class represents a formal concept.
 * 
 * It is composed of a list of attributes (i.e., the intent) and a list of objects (i.e., the extent).
 * 
 * 
 * @author Jessie Carbonnel
 *
 */
public class Concept {

	/************************* ATTRIBUTES *************************/

	
	/**
	 * Intent of the concept (attribute set)
	 */
	private ArrayList<String> intent = new ArrayList<String>();
	
	/**
	 * Extent of the concept (object set)
	 */
	private ArrayList<String> extent = new ArrayList<String>();
	
	
	/************************* CONSTRUCTORS *************************/
	
	
	/**
	 * Empty constructor
	 */
	public Concept() {

	}
	
	/**
	 * Creates a concept with i for intent and e for extent.
	 * 
	 * @param i a list of attributes
	 * @param e a list of objects
	 */
	public Concept(ArrayList<String> i, ArrayList<String> e) {
		if (i != null) {
			intent.addAll(i);
		}
		if (e != null) {
			extent.addAll(e);
		}
	}

	
	/************************* GETTERS AND SETTERS *************************/

	
//	public void addInExtent(String e){
//		extent.add(e);
//	}
//	
//	public void setExtent(Collection<String> e){
//		extent.clear();
//		extent.addAll(e);
//	}
//	
//	public void addInIntent(String i){
//		intent.add(i);
//	}
//	
//	public void setIntent(Collection<String> i){
//		intent.clear();
//		intent.addAll(i);
//	}
//	
	
	/**
	 * Returns the intent of the concept.
	 * 
	 * @return a list of attributes representing the intent 
	 */
	public ArrayList<String> getIntent() {
		return intent;
	}
	
	/**
	 * Returns the extent of the concept.
	 * @return a list of objects representing the extent
	 */
	public ArrayList<String> getExtent() {
		 return extent;
	}
	
	/**
	 * Returns a String documenting the concept.
	 */
	public String toString(){
		return "("+intent+", "+extent+")";
	}
	
	/************************* PUBLIC METHODS *************************/

	
	/**
	 * Transforms the current concept into the attribute-concept of the formal context fc introducing att.
	 * 
	 * @param att an attribute of the formal context fc
	 * @param fc a formal context
	 */
	public void setAttIntroducer(String att, FormalContext fc) {
		
		// Clears intent and extent
		
		this.intent.clear();
		this.extent.clear();
		
		// Adds the closure of att as extent
		
		this.extent.addAll(fc.attClosure(att));
		
		// Adds the closure of the extent as intent
		
		this.intent.addAll(fc.setObjClosure(this.extent));
	}
	
	/**
	 * Transforms the current concept into the object-concept of the formal context fc introducing obj.
	 * 
	 * @param obj an object of the formal context fc
	 * @param fc a formal context
	 */
	public void setObjIntroducer(String obj, FormalContext fc){
		
		// Clears intent and extent
		
		this.intent.clear();
		this.extent.clear();
		
		// Adds the closure of obj as intent
		
		this.intent.addAll(fc.objClosure(obj));
		
		// Adds the closure of the intent as the extent
		
		this.extent.addAll(fc.setAttClosure(intent));
	}
	
}
