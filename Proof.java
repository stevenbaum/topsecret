import java.util.*;


public class Proof {
	
	public static LineNumber lastLineNumber;
	LinkedList<Object> myLinkedList;
	TheoremSet mytheorems;
	LineNumber valueholder = new LineNumber("1");
	
	public HashMap<LineNumber, LinkedList> myLineNumbers = new HashMap<LineNumber, LinkedList>();

	public Proof (TheoremSet theorems) {
		mytheorems = theorems;
	}

	public LineNumber nextLineNumber ( ) {
		lastLineNumber = valueholder;
		LineNumber valueholder = LineNumber.nextlinenumber_helper((String)myLineNumbers.get(lastLineNumber).getFirst(), (Expression)myLineNumbers.get(lastLineNumber).getLast());
		return valueholder;
	}

	public void extendProof (String x) throws IllegalLineException, IllegalInferenceException {
		//add the parsed expression to the hashmap at the key of "lastLineNumber"
		LinkedList<Object> templist = new LinkedList<Object>();
		try{
			 templist = inputstringparser(x);
		}catch (IllegalLineException e){
			System.err.println(e.getMessage());
		}catch (IllegalInferenceException a){
			System.err.println(a.getMessage());
		}
		
		try{
			MethodCaller(templist);
		}catch (IllegalLineException e){
			System.err.println(e.getMessage());
		}catch (IllegalInferenceException a){
			System.err.println(a.getMessage());
		}
		
		myLineNumbers.put(lastLineNumber, myLinkedList);
	}

	public String toString ( ) {
		return "";
	}

	public boolean isComplete ( ) {
		return true;
	}
	
	public LinkedList<Object> inputstringparser(String x) throws IllegalLineException, IllegalInferenceException{
		
		//Might not need this. Attempt to prevent person from inputting an empty line.
		if(x == null){
			throw new IllegalLineException("You must input something.");
		}
		
		String[] myValues = x.split("\\s"); //Splits the input string based on white space.
		
		//Test if the user entered too many inputs for a single line.
		if (myValues.length > 4){
			throw new IllegalLineException("Too many arguments inputted. Max possible is 4.");
		}
		
		for (int i = 0; i < myValues.length; i++){ //Adds each element of the split up input string 
			myLinkedList.add(myValues[i]);         //into a LinkedList in sequential order. 
		}										   //ex. "assume (a=>b)" is put in as "assume", "(a=>b)"
		
		String test = (String)myLinkedList.getFirst(); //The string representation of the operator.
		
		//Test checking if the first argument of the input line is anything other than an approved operator.
		//If it is not approved, then it throws an illegal line operator. 
		if (test != "show" || test != "assume" || test != "repeat" || test != "print"  
				|| test != "mp" || test != "ic" || test != "mt" || test != "co" || !mytheorems.myThms.containsKey(test)){
			throw new IllegalLineException("Invalid operator.");
		}
		
		//Test if wrong number of arguments are entered for show, assume, or a theorem.
		if ((test == "show" || test == "assume" || mytheorems.myThms.containsKey(test)) && myLinkedList.size() != 2){
			throw new IllegalLineException("Wrong number of arguments supplied for " + test + "statement. 2 needed.");
		}
		
		//Test if wrong number of arguments are entered for repeat, mc, mt, or co.
		if ((test == "repeat" || test == "mc" || test == "mt" || test == "co") && myLinkedList.size() != 4){
			throw new IllegalLineException("Wrong number of arguments for " + test + "statement. 4 needed.");
		}
		
		//Test if wrong number of arguments are entered for print.
		if (test == "print" && myLinkedList.size() != 1){
			throw new IllegalLineException("Wrong number of arguments for " + test + "statement. 1 needed.");
		}
		
		//Test if wrong number of arguments entered for ic
		if (test == "ic" && myLinkedList.size() != 3){
			throw new IllegalLineException("Wrong number of arguments for " + test + "statement. 3 needed.");
		}
		
		//The string representation of the expression in the Linked List.
		String Expr = (String)myLinkedList.getLast(); 
		
		Expression newExpr;
		
		//Attempts to create an expression object from the last element of the input. If error occurs,
		//throws it right along until it is dealt with in extend proof.
		try{	
			 newExpr = new Expression(Expr);
		}catch(IllegalLineException e){
			throw e;
		}
		
		//Removing the string object from the end of the LinkedList(which is holding the input string)
		//and then adding the correct expression object to the end of the linked list. 
		int removal_index = myLinkedList.size()-1;
		myLinkedList.remove(removal_index);
		myLinkedList.addLast(newExpr);
		return myLinkedList; //returning the correctly syntaxed LinkedList to then have methods called upon it.
		}
		
	public void MethodCaller(LinkedList fun) throws IllegalLineException, IllegalInferenceException{
		Expression exp1;
		Expression exp2;
		Expression test;
		
		String op = (String)fun.getFirst();
		if (op == "show"){
			try{
				Logic.show((Expression)fun.get(1), lastLineNumber);
			}catch (IllegalInferenceException a){
				throw a;
			}
		}else if(op == "assume"){
			try{
				Logic.assume((Expression)fun.get(1), lastLineNumber);
			}catch (IllegalInferenceException a){
				throw a;
			}
		}else if(op == "repeat"){
			
		}else if(op == "print"){
			this.toString();
		}else if(op == "mp" || op == "mt" || op == "co"){
			
			//Testing what "op" is entered and then testing the line numbers if they correctly reference a line number
			//in our hashmap.
			LineNumber linetest1 = new LineNumber((String)fun.get(1));
			LineNumber linetest2 = new LineNumber((String)fun.get(2));
			if(!myLineNumbers.containsKey(linetest1) || !myLineNumbers.containsKey(linetest2)){
				throw new IllegalLineException("Bad line reference");
			}else{
				exp1 = (Expression)myLineNumbers.get(linetest1).getLast();
				exp2 = (Expression)myLineNumbers.get(linetest2).getLast();	
				test = (Expression)fun.getLast();
			
			//Calling the particular logic functions based on which "op" is entered.
			if(op == "mp"){
				try{
					Logic.moduspolens(exp1, exp2, test);
				}catch(IllegalInferenceException a){
					throw a;
				}
			}
			if(op == "mt"){
				try{
					Logic.modustollens(exp1, exp2, test);
				}catch (IllegalInferenceException a){
					throw a;
				}
			}
			if(op == "co"){
				try{
					Logic.contradiction(exp1, exp2, test);
				} catch (IllegalInferenceException a){
					throw a;
				}
			}
			}
			
		}else if(op == "ic"){
			LineNumber linetest = new LineNumber((String)fun.get(1));
			if(!myLineNumbers.containsKey(linetest)){
				throw new IllegalLineException("Bad line reference");
			}else{
				exp1 = (Expression)myLineNumbers.get(linetest).getLast();
			}
			try{
				Logic.construction(exp1, (Expression)fun.getLast());
			}catch (IllegalInferenceException a){
				throw a;
			}
			
		}else{
			
		}
	
	}
}
