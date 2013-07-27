import java.util.*;

public class LineNumber {

  private static String myNumber;
  private static Stack<Expression> exprStack;

	public LineNumber(String num) {
		myNumber = num;
		exprStack = new Stack<Expression>();
	}
	
	
	public String getNum(){
		return myNumber;
	}
	
	
	
	
	public static LineNumber nextlinenumber_helper(String op, Expression provenExpr){

		int index = 0; // Used for finding '.' in old LineNumber

		// @line is the next proof line, read from InputSource
		// This will return myNumber + 1 if line does not include "show"
		// or the line does not prove a previous "show"s expression
		// If it is a show, concatenate ".1" to the current line number

		//Scanner lineScanner = new Scanner(line); // line = "show (~~p=>p)"
		//String op = lineScanner.next( ); // op = "show"
		//provenExpr = lineScanner.next( ); // provenExpr = "(~~p=>p)"

		// Changed by VB, cause the line we are using to determine the next line has already been
		// added to our potential hashmap. Meaning we just need to input the correct op and exp from the 
		// hashmap when determining the next line number.

		// if the operation is "print", do nothing
		if (op.equals("print")) {
			return new LineNumber(myNumber);
		}

		// if the operation is "show", then we are entering a subproof
		if (op.equals("show")) {
			exprStack.push(provenExpr); //changed to add the expr to the stack rather than the op
			myNumber = myNumber + ".1"; // 1 becomes 1.1
			return new LineNumber(myNumber);
		}
		// if the operation proves the expression we are trying to show, exit the subproof
		if (op.equals("ic") || op.equals("mp") || op.equals("co") || op.equals("mt")) {
			// If the last expression to be shown is proven in this line, end the subproof
			if (exprStack.pop().isproven) {

				// Find the rightmost decimal and remove it and all that follows it
				// i.e. 3.2.1 --> 3.2
				for (int i = myNumber.length(); i >= 0; i--) {  // 3.2.3
					if (myNumber.charAt(i) == '.') {
						index = i;
						break;
					}
				}
				myNumber = myNumber.substring(0, index); // 3.2
				// increment the number following the rightmost decimal
				// 3.2 --> 3.3
				// Find the new rightmost decimal
				for (int i = myNumber.length(); i >= 0; i--) {
					if (myNumber.charAt(i) == '.') {
						index = i;
						break;
					}
				}
				// Convert the string to the right of the rightmost decimal to an integer
				// increment it, convert it back to a string and concatenate to myNumber
				int newRight = Integer.parseInt(myNumber.substring(index + 1, myNumber.length())) + 1; // newRight = "3"
				myNumber = myNumber.substring(0, index + 1) + newRight + ""; // myNumber.substring(0, index + 1) = "3."
                // myNumber = "3.3"

			}
		}
		else {
			// Increment the digit(s) following the last decimal of myNumber if it has one
			if (myNumber.contains(".")) {
				for (int i = myNumber.length(); i >= 0; i--) {
						if (myNumber.charAt(i) == '.') {
							index = i;
							break;
						}
					}
					// Convert the string to the right of the rightmost decimal to an integer
					// increment it, convert it back to a string and concatenate to myNumber
					int newRight = Integer.parseInt(myNumber.substring(index + 1, myNumber.length())) + 1;
					myNumber += newRight + "";
			}
			// If myNumber contains no decimals, simply increment it by 1
			else {
				myNumber = (Integer.parseInt(myNumber) + 1) + "";
			}
		}
		return new LineNumber(myNumber);

	}




	/*1	show (~~p=>p)
	2	assume ~~p
	3	show p
	3.1	assume ~p
	3.2	co 2 3.1 p
	4	ic 3 (~~p=>p)*/


	/*
	1 show (p=>((p=>q)=>q))
	2 assume p
	3 show ((p=>q)=>q)
	3.1 assume (p=>q)
	3.2 show q
	3.2.1 mp 2 3.1 q
	3.3 ic 3.2 ((p=>q)=>q)
	4 ic 3 (p=>((p=>q)=>q)) */

	//Show called on expression, save expression, if not proven, when it is true, then jump out of line number back to parent number


}
