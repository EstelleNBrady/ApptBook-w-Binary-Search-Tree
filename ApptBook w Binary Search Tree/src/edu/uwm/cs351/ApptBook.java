// This is an assignment for students to complete after reading Chapter 3 of
// "Data Structures and Other Objects Using Java" by Michael Main.

package edu.uwm.cs351;

import java.util.function.Consumer;

import edu.uwm.cs.junit.LockedTestCase;
//Estelle Brady
//CS 351 - 401
//Collaborated with Miguel Garcia, Christian Ortega, Julian Morano and got help from tutor Matt
//used this link for help for clone/clonehelper https://interview.hackingnote.com/en/problems/clone-binary-tree
//this for countNodes https://www.youtube.com/watch?v=1YB9DzLfj_0&list=PLt4nG7RVVk1hXIW5w54uKEN25MqHNS7_A&index=5
//this for 

/******************************************************************************
 * This class is a homework assignment;
 * An ApptBook ("book" for short) is a sequence of Appointment objects in sorted order.
 * The book can have a special "current element," which is specified and 
 * accessed through four methods that are available in the this class 
 * (start, getCurrent, advance and isCurrent).
 ******************************************************************************/
public class ApptBook implements Cloneable {
	// TODO: Declare the private static Node class.
	// It should have a constructor but no methods.
	// The constructor should take an Appointment.
	// The fields of Node should have "default" access (neither public, nor private)
	// and should not start with underscores.

	// TODO: Declare the private fields of ApptBook needed for sequences
	// using a binary search tree.
	private int manyItems;
	private Node cursor;
	private Node root;

	private static class Node{
		Appointment data;
		Node left;
		Node right;
		
		Node(Appointment data){
			this.data = data;
			left = null;
			right = null;
		}
	}

	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };
	
	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	/**
	 * Return true if the given subtree has height no more than a given bound.
	 * In particular if the "tree" has a cycle, then false will be returned
	 * since it has unbounded height.
	 * @param r root of subtree to check, may be null
	 * @param max maximum permitted height (null has height 0)
	 * @return whether the subtree has at most this height
	 */
	//tutor Matt helped me with this method and Professor Boyland
	private boolean checkHeight(Node r, int max) {
		//r can be null as long as the max is 0 or greater
		if(r == null && max >= 0)	return true;
		//max cannot be equal to 0 if r is not null
		if(max < 0) return false;
		
		
		return checkHeight(r.left, max-1) && checkHeight(r.right, max-1);
	}
	
	/**
	 * Return the number of nodes in a subtree that has no cycles.
	 * @param r root of the subtree to count nodes in, may be null
	 * @return number of nodes in subtree
	 */
	//referenced this video
	//https://www.youtube.com/watch?v=1YB9DzLfj_0&list=PLt4nG7RVVk1hXIW5w54uKEN25MqHNS7_A&index=5
	private int countNodes(Node r) {
		if(r == null)
			manyItems = 0;
		else
			manyItems = countNodes(r.left)+countNodes(r.right)+1;
		return manyItems;
	}
	
	/**
	 * Return whether all the nodes in the subtree are in the given range,
	 * and also in their respective subranges.
	 * @param r root of subtree to check, may be null
	 * @param lo inclusive lower bound, may be null (no lower bound)
	 * @param hi exclusive upper bound, may be null (no upper bound)
	 * @return
	 */
	//helped by TA Anjali
	private boolean allInRange(Node r, Appointment lo, Appointment hi) {
		if(r == null)	return true;
		
		//if the data is null, throw a report
		if(r.data == null)
			return report("data cannot be null");
		
		//if the high is not infinite
		//and the data is equal to or greater than the high
		if(hi!=null)
			if(r.data.compareTo(hi) >= 0)
				return report("low cannot come before high");
		
		//if the low is not infinite
		//and the data is less than the low
		if(lo!=null)
			if(r.data.compareTo(lo) <0)
				return report("high cannot come before low");
		
		//if both statements are true then return 
		return allInRange(r.left, lo, r.data) && allInRange(r.right, r.data, hi);
		}
	
	/**
	 * Return whether the cursor was found in the tree.
	 * If the cursor is null, it should always be found since 
	 * a binary search tree has many null pointers in it.
	 * This method doesn't examine any of the data elements;
	 * it works fine even on trees that are badly structured, as long as
	 * they are height-bounded.
	 * @param r subtree to check, may be null, but must have bounded height
	 * @return true if the cursor was found in the subtree
	 */
	private boolean foundCursor(Node r) {
		//means cursor is found
		if(r == cursor)
			return true;
		
		if(r==null) 
			return false;
		
		//if the cursor is found on either side
		return foundCursor(r.right)||foundCursor(r.left);	
	}

	
	private boolean wellFormed() {
		// Check the invariant.
		// Invariant:
		// 1. The tree must have height bounded by the number of items
		if(checkHeight(root, manyItems) == false)
			return report("The tree must have height bounded by the number of items");
		// 2. The number of nodes must match manyItems
		if (manyItems != countNodes(root))
			return report("The number of nodes must match manyItems");
		// 3. Every node's data must not be null and be in range.
		if(allInRange(root, null, null) == false)
			return false;
		// 4. The cursor must be null or in the tree.
		if(foundCursor(root) == false)
			return report("cursor not found");
		
		
		// Implementation:
		// Do multiple checks: each time returning false if a problem is found.
		// (Use "report" to give a descriptive report while returning false.)
		// TODO: Use helper methods to do all the work.
		
		// If no problems found, then return true:
		return true;
		
	}

	// This is only for testing the invariant.  Do not change!
	private ApptBook(boolean testInvariant) { }

	/**
	 * Initialize an empty book. 
	 **/   
	public ApptBook( )
	{
		manyItems = 0;
		root = null;
		cursor = root;
		assert wellFormed() : "invariant failed at end of constructor";
	}

	/**
	 * Determine the number of elements in this book.
	 * @return
	 *   the number of elements in this book
	 **/ 
	public int size()
	{

		assert wellFormed() : "invariant failed at start of size";
		return manyItems;
	}

	/**
	 * Return the first node in a non-empty subtree.
	 * It doesn't examine the data in teh nodes; 
	 * it just uses the structure.
	 * @param r subtree, must not be null
	 * @return first node in the subtree
	 */
	private Node firstInTree(Node r) {
		if(r!= null) {
			while(r.left!=null) {
				r = r.left;
			}
			
		//null means we are at the end of the loop
		if(r.left == null)
			return r;
		}
		return null; // TODO: non-recursive is fine
	}
	
	/**
	 * Set the current element at the front of this book.
	 * @postcondition
	 *   The front element of this book is now the current element (but 
	 *   if this book has no elements at all, then there is no current 
	 *   element).
	 **/ 
	public void start( )
	{
		assert wellFormed() : "invariant failed at start of start";
		cursor = firstInTree(root);
		assert wellFormed() : "invariant failed at end of start";
	}

	/**
	 * Accessor method to determine whether this book has a specified 
	 * current element that can be retrieved with the 
	 * getCurrent method. 
	 * @return
	 *   true (there is a current element) or false (there is no current element at the moment)
	 **/
	public boolean isCurrent( )
	{
		assert wellFormed() : "invariant failed at start of isCurrent";
		return cursor != null;
	}

	/**
	 * Accessor method to get the current element of this book. 
	 * @precondition
	 *   isCurrent() returns true.
	 * @return
	 *   the current element of this book
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   getCurrent may not be called.
	 **/
	public Appointment getCurrent( )
	{
		assert wellFormed() : "invariant failed at start of getCurrent";
		if(this.isCurrent() == false)
			throw new IllegalStateException("There is no current element");
		return cursor.data;
	}

	/**
	 * Find the node that has the appt (if acceptEquivalent) or the first thing
	 * after it.  Return that node.  Return the alternate if everything in the subtree
	 * comes before the given appt.
	 * @param r subtree to look into, may be null
	 * @param appt appointment to look for, must not be null
	 * @param acceptEquivalent whether we accept something equivalent.  Otherwise, only
	 * appointments after the appt are accepted.
	 * @param alt what to return if no node in subtree is acceptable.
	 * @return node that has the first element equal (if acceptEquivalent) or after
	 * the appt.
	 */
	private Node nextInTree(Node r, Appointment appt, boolean acceptEquivalent, Node alt) {
		// TODO: recursion not required, but is simpler
        
        if(r != null) {
            // if appt is equal to r
            if(appt.compareTo(r.data) == 0) {
                if(acceptEquivalent) return r;
                else {
                    // find the successor
                    // if r has a right child then get the left most of it
                    // consider about duplicate elements
                    return nextInTree(r.right, appt, acceptEquivalent, alt);
                }
            } 
            // if appt comes before r
            else if(appt.compareTo(r.data) < 0) {
                // whenever we go to the left we know that the root is our next element
                // unless there is a right child
                // go to the left node
                return nextInTree(r.left, appt, acceptEquivalent, r);
            } 
            // if appt comes after r
            else {
                // go to the right node
                return nextInTree(r.right, appt, acceptEquivalent, alt);
            } 
        }
        return alt;
	}

	/**
	 * Move forward, so that the current element will be the next element in
	 * this book.
	 * @precondition
	 *   isCurrent() returns true. 
	 * @postcondition
	 *   If the current element was already the end element of this book 
	 *   (with nothing after it), then there is no longer any current element. 
	 *   Otherwise, the new element is the element immediately after the 
	 *   original current element.
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   advance may not be called.
	 **/
	
	//collaborated with Miguel Garcia for this method
	public void advance( )
	{
		assert wellFormed() : "invariant failed at start of advance";
		if (isCurrent() == false)
			throw new IllegalStateException("there is no current element in advance");
		
		//if the cursor is not null, then we can go right
		//and then traverse to the beginning
		if(cursor.right !=null) {
		cursor=	firstInTree(cursor.right);
		}
		
		//otherwise, we go next in tree
		else {
		cursor = nextInTree(root, cursor.data, false, null);
		}

		// TODO: See homework description.
		// firstInTree and nextInTree are useful.
		assert wellFormed() : "invariant failed at end of advance";
	}

	/**
	 * Remove the current element from this book.
	 * NB: Not supported in Homework #8
	 **/
	public void removeCurrent( )
	{
		assert wellFormed() : "invariant failed at start of removeCurrent";
		throw new UnsupportedOperationException("remove is not implemented");
	}
	
	/**
	 * Set the current element to the first element that is equal
	 * or greater than the guide.  This operation will be efficient
	 * if the tree is balanced.
	 * @param guide element to compare against, must not be null.
	 */
	public void setCurrent(Appointment guide) {
		assert wellFormed() : "invariant failed at start of setCurrent";
		if (guide == null) throw new NullPointerException("guide cannot be null");
		
		//set the cursor to the specific element's location
		cursor = nextInTree(root, guide, true, null);
	
		assert wellFormed() : "invariant failed at end of setCurrent";
	}

	// OPTIONAL: You may define a helper method for insert.  The solution does
	/**
	 * Add a new element to this book, in order.  If an equal appointment is already
	 * in the book, it is inserted after the last of these. 
	 * The current element (if any) is not affected.
	 * @param element
	 *   the new element that is being added, must not be null
	 * @param Node r 
	 * 	the node that we are using
	 * @postcondition
	 *   A new copy of the element has been added to this book. The current
	 *   element (whether or not is exists) is not changed.l
	 *  @return r
	 **/
	
	//taken from notes in class
	private Node addHelper(Node r, Appointment Helement) {
		if(r == null) {
			return new Node(Helement);
		}
		if(Helement.compareTo(r.data) < 0) {
			r.left = addHelper(r.left, Helement);
		}else {
			r.right = addHelper(r.right, Helement);
		}
		return r;
	}
	
	/**
	 * Add a new element to this book, in order.  If an equal appointment is already
	 * in the book, it is inserted after the last of these. 
	 * The current element (if any) is not affected.
	 * @param element
	 *   the new element that is being added, must not be null
	 * @postcondition
	 *   A new copy of the element has been added to this book. The current
	 *   element (whether or not is exists) is not changed.
	 * @exception IllegalArgumentException
	 *   indicates the parameter is null
	 **/
	//taken from notes in class
	public void insert(Appointment element)
	{
		assert wellFormed() : "invariant failed at start of insert";
		
		if(element == null)	throw new IllegalArgumentException("element cannot be null in insert");
		
		root = addHelper(root, element);
		//increment manyItems
		++manyItems;
		
		assert wellFormed() : "invariant failed at end of insert";
	}

	// TODO: recursive helper method for insertAll.  
	// - Must be recursive.
	// - Must add in "pre-order"
	
	/**
	 * Place all the appointments of another book (which may be the
	 * same book as this!) into this book in order as in {@link #insert}.
	 * The elements should added one by one.
	 * @param Node r
	 *   a book whose contents will be placed into this book
	 * @precondition
	 *   The parameter, addend, is not null. 
	 * @postcondition
	 *   The elements from addend have been placed into
	 *   this book. The current el;ement (if any) is
	 *   unchanged.
	 **/

	//helped by tutor Matt
	private void insertAllHelper(Node r) {
		if(r == null) 
			return;
		insert(r.data);
		insertAllHelper(r.left);
		insertAllHelper(r.right);
		}
	
	/**
	 * Place all the appointments of another book (which may be the
	 * same book as this!) into this book in order as in {@link #insert}.
	 * The elements should added one by one.
	 * @param addend
	 *   a book whose contents will be placed into this book
	 * @precondition
	 *   The parameter, addend, is not null. 
	 * @postcondition
	 *   The elements from addend have been placed into
	 *   this book. The current el;ement (if any) is
	 *   unchanged.
	 **/
	public void insertAll(ApptBook addend)
	{
		assert wellFormed() : "invariant failed at start of insertAll";
		// TODO: Implemented by student.
		// Watch out for the this==addend case!
		// Cloning the addend is an easy way to avoid problems.
		if(addend == null)	throw new NullPointerException("this is null");
		if(addend == this)	addend = addend.clone();
		
		insertAllHelper(addend.root);
		
		assert wellFormed() : "invariant failed at end of insertAll";
		assert addend.wellFormed() : "invariant of addend broken in insertAll";
	}

	// TODO: private recursive helper method for clone.
	// - Must be recursive
	// - Take the answer as a parameter so you can set the cloned cursor
	
	/**
	 * Generate a copy of this book.
	 * @return
	 *   The return value is a copy of this book. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 **/ 
	
	//used this link to help me with clone/clone helper
	//https://interview.hackingnote.com/en/problems/clone-binary-tree
	//got help from Matt
	private Node cloneHelper(Node r, ApptBook answer) {
	    if (r == null)
	        return null;
	      Node newNode = new Node(r.data); //must create a new node
	      //traverse to the left and right
	      newNode.left = cloneHelper(r.left, answer);
	      newNode.right = cloneHelper(r.right, answer);
	      //set cursor
	      if(r == cursor)
	    	 answer.cursor = newNode;
	      return newNode;
	}
	
	/**
	 * Generate a copy of this book.
	 * @return
	 *   The return value is a copy of this book. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 **/ 
	public ApptBook clone( ) { 
		assert wellFormed() : "invariant failed at start of clone";
		ApptBook answer;
	
		try
		{
			answer = (ApptBook) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  // This exception should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			throw new RuntimeException
			("This class does not implement Cloneable");
		}
		//set manyItems
		answer.manyItems = manyItems;
		//use the helper
		answer.root = cloneHelper(this.root, answer);
	
		assert wellFormed() : "invariant failed at end of clone";
		assert answer.wellFormed() : "invariant on answer failed at end of clone";
		return answer;
	}

	// don't change this nested class:
	public static class TestInvariantChecker extends LockedTestCase {
		protected ApptBook self;

		protected Consumer<String> getReporter() {
			return reporter;
		}
		
		protected void setReporter(Consumer<String> c) {
			reporter = c;
		}
		
		private static Appointment a = new Appointment(new Period(new Time(), Duration.HOUR), "default");
		
		protected class Node extends ApptBook.Node {
			public Node(Appointment d, Node n1, Node n2) {
				super(a);
				data = d;
				left = n1;
				right = n2;
			}
			public void setLeft(Node l) {
				left = l;
			}
			public void setRight(Node r) {
				right = r;
			}
		}
		
		protected Node newNode(Appointment a, Node l, Node r) {
			return new Node(a, l, r);
		}
		
		protected void setRoot(Node n) {
			self.root = n;
		}
		
		protected void setManyItems(int mi) {
			self.manyItems = mi;
		}
		
		protected void setCursor(Node n) {
			self.cursor = n;
		}
		
		protected void setUp() {
			self = new ApptBook(false);
			self.root = self.cursor = null;
			self.manyItems = 0;
		}
		
		
		/// relay methods for helper methods:
		
		protected boolean checkHeight(Node r, int max) {
			return self.checkHeight(r, max);
		}
		
		protected int countNodes(Node r) {
			return self.countNodes(r);
		}
		
		protected boolean allInRange(Node r, Appointment lo, Appointment hi) {
			return self.allInRange(r, lo, hi);
		}
		
		protected boolean foundCursor(Node r) {
			return self.foundCursor(r);
		}
		
		protected boolean wellFormed() {
			return self.wellFormed();
		}
		
		protected Node firstInTree(Node r) {
			return (Node)self.firstInTree(r);
		}
		
		protected Node nextInTree(Node r, Appointment a, boolean acceptEquiv, Node alt) {
			return (Node)self.nextInTree(r, a, acceptEquiv, alt);
		}
		
		
		/// Prevent this test suite from running by itself
		
		public void test() {
			assertFalse("DOn't attempt to run this test", true);
		}
	}
}

