package localGen;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This class represents a formal context.
 * It is composed of a set of objects and a set of attributes that may described these objects.
 * 
 * An object is here represented by its attribute set.
 * Each object is manipulated through a unique id, corresponding to its index in the list of objects.
 * 
 * @author Jessie Carbonnel
 *
 */
public class FormalContext {

	
	/************************* ATTRIBUTES *************************/
	
	
	/**
	 * List of objects.
	 * Each object is represented by a String representing an attribute set, in which attributes are split by semicolons: A;B;C.
	 */
	private ArrayList<String> objects;
	
	/**
	 * List of attributes.
	 */
	private ArrayList<String> attributes;
	
	
	/************************* CONSTRUCTORS *************************/
	
	
	/**
	 * Creates a new formal context based on the text file specified in parameter.
	 * The text file must contains a list of objects defined by their attribute set.
	 * The file is of the form {{A;B;C},{A;C},{A,B,D}} to represent the 3 attribute sets (and thus the 3 objects) ABC, AC and ABD.
	 * 
	 * @param path the path to the text file containing an the attribute sets.
	 */
	public FormalContext(String path) {
		
		// Class attribute initialisation
		
		attributes = new ArrayList<String>();
		
		objects = new ArrayList<String>();

		// The specified text file is retrieved from the repository "data/"
		
		path = "data/"+path;
		
		// Adds all attribute set from the file in the list objects
		
		try {
			
			// Read the file
			// Split the attribute sets
			// Clean each attribute set
			// Add each attribute set in the list
			
			Files.lines(Paths.get(path))
            .map(line -> line.split("\\};\\{")) 		
            .flatMap(Arrays::stream) 					
            .distinct() 								
            .map(o -> o.replaceAll("\\{|\\}", ""))
            .forEach(o -> addObject(o));
	
		} catch(Exception e) {
			System.out.println(e.toString());
		}
		
		
		// Computes the distinct attributes of the retrieved objects
		
		this.computeDomain();
	}
	
	
	/************************* GETTERS AND SETTERS *************************/

	/**
	 * Retrieves the distinct attributes of the formal context.
	 * 
	 * @return a list of the attributes of the formal context.
	 */
	public ArrayList<String> getAttributes(){
		return attributes;
	}
	
	/**
	 * Returns the attribute sets representing the objects.
	 * 
	 * @return a list of attribute sets.
	 */
	public ArrayList<String> getObjects(){
		return objects;
	}
	
	
	/************************* PRIVATE METHODS *************************/
	
	
	/**
	 * Adds an object in the form of an attribute set to the list objects.
	 * Checks if the attribute set is in the good format (i.e., attributes split by semi-colons)
	 * Raises an error if not.
	 * 
	 * @param o the objects to be checked and added.
	 */
	private void addObject(String o) {
		
		// Verifies if the object o is in the good format
		
		if (o.matches("([a-zA-Z0-9 ]|;)*")) {
			
			this.objects.add(o);
			
		} else {
			
			// Raises an error 
			
			System.err.println("Attribute set in bad format : " + o);
		}
	}
	
	/**
	 * Computes the distinct set of attributes of the formal context, based on the attribute sets representing the objects.
	 * O(m*n)
	 */
	private void computeDomain() {
		
		HashSet<String> dom = new HashSet<String>();
		
		for(String s : objects){
			dom.addAll(Arrays.asList(s.split(";")));
		}
		
		//System.out.println("Domain: " + dom);
		
		attributes.addAll(dom);
	}
	
	
	/************************* PUBLIC METHODS *************************/

	/**
	 * Returns the number of objects of the formal context.
	 * 
	 * @return the number of objects.
	 */
	public int getNumberOfObjects() {
		return objects.size();
	}
	
	/**
	 * Returns the attribute set representing an object at the specified index in the list objects
	 * 
	 * @param index the index of the attribute set to be returned
	 * @return the attribute set corresponding to the index specified in parameter
	 */
	public String getObjectAtIndex(int index) {
		
		if (index > objects.size()) {
			System.err.println("Try to reach non existing object: ask " + index + " but only " + objects.size() + " objects.");
			return null;
		} else {
			return objects.get(index);
		}
	}
	
	/**
	 * Returns a String representing the objects.
	 */
	public String toString() {
		String s = "";
		for (String o : objects) {
			s += "o" + (objects.indexOf(o) + 1) + " : [" + o;
			s+= "]\n";	
		}
		return s;
	}
	
	/**
	 * Returns true if the concept c introduces the attribute att, else returns false.
	 * To know if a concept introduces an attribute, it's extent should be equal to the closure of the attribute.
	 * 
	 * @param att an attribute of the formal context
	 * @param c a concept
	 * @return true if the concept c introduces the attribute att, else false
	 */
	public boolean isAttIntroducedIn(String att, Concept c) {
		return c.getExtent().containsAll(attClosure(att)) && attClosure(att).containsAll(c.getExtent());
	}
	
	/**
	 * Returns true if the concept c introduces the object o, else returns false.
	 * To know if a concept introduces an object, it's intent should be equal to the closure of the object.
	 * .
	 * @param obj an object of the formal context
	 * @param c a concept
	 * @return true if the concept c introduces the object o, else false
	 */
	public boolean isObjIntroducedIn(String obj, Concept c){
		return c.getIntent().containsAll(objClosure(obj)) && objClosure(obj).containsAll(c.getIntent());
	}
	
	/**
	 * Computes the closure of an attribute in the formal context.
	 * A closure of an attribute corresponds to all objects having this attribute.
	 * 
	 * @param att an attribute of the formal context.
	 * @return a list objects' indexes corresponding to the closure of the attribute.
	 */
	public ArrayList<String> attClosure(String att) {
		
		// TODO Faire une simple boucle avec indices pour éviter le indexof ?
		
		ArrayList<String> closure = new ArrayList<String>();
				
		this.objects.stream()
		.filter(s -> s.matches("([a-zA-Z0-9 ]|;)*" + att + "(;|$)+([a-zA-Z0-9 ]|;)*"))
		.map(x -> Integer.toString(objects.indexOf(x) + 1))
		.forEach(closure::add);				 
		
		return closure;
	}

	/**
	 * Computes the closure of an object obj.
	 * The closure of an object corresponds to its attribute set.
	 * 
	 * @param obj the index of an object
	 * @return the attribute set describing the object
	 */
	public ArrayList<String> objClosure(String obj) {

		ArrayList<String> closure = new ArrayList<String>();
		
		Arrays	
		.stream(this.getObjectAtIndex(Integer.parseInt(obj) - 1).split(";"))
		.forEach(closure::add);
		
		return closure;
	}

	/** 
	 * Computes the closure of a subset of attributes.
	 * It corresponds to the set of objects having this subset of attributes.
	 * 
	 * @param att a subset of attributes
	 * @return
	 */
	public ArrayList<String> setAttClosure(ArrayList<String> att){
		
		// Will contain the list of objects' indexes in the closure of att
		
		ArrayList<String> closure = new ArrayList<String>();
		
		// Tests each object
		// ownsAll stays at true if the attribute set corresponding to the object includes all the attributes of the subset att
		
		for (int i = 0; i < getNumberOfObjects(); i++) {
		
			boolean ownsAll = true;
			
			for (String a : att) {
				if (!getObjectAtIndex(i).matches("([a-zA-Z0-9 ]|;)*" + a + "(;|$)+([a-zA-Z0-9 ]|;)*")) {
					ownsAll = false;
				}
			}
			
			if (ownsAll) {
				closure.add("" + ( i+1));
			}
		}
				
		return closure;
	}

	/**
	 * Computes the closure of a subset of objects.
	 * It corresponds to the attributes shared by all the objects of the subset.
	 * 
	 * @param obj a set of indexes corresponding to objects of the formal context.
	 * @return the attribute set included in all objects specified in parameter.
	 */
	public ArrayList<String> setObjClosure(ArrayList<String> obj){
		
		// Will contain the attribute sets representing the objects of the indexes obj
		
		ArrayList<String> temp = new ArrayList<String>();
		
		// Will contain the attributes of the closure
		// Implementation : Previously a set
		
		ArrayList<String> cl = new ArrayList<String>();
		cl.addAll(this.attributes);
		
		// Retrieves the attribute set corresponding to the objects specified in parameter.
		
		getObjects()
		.stream()
		.filter(s -> obj.contains("" + ( 1 + this.objects.indexOf(s))))
		.forEach(temp::add);
		
		// Keeps only the attributes present in each object
	
		for (String o : temp) {
			cl.retainAll(Arrays.asList(o.split(";")));
		}

		return cl;
		
	}

}
