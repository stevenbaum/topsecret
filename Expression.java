import java.util.*;
import java.util.regex.Pattern;

public class Expression {
    
    public String myname;
    public boolean isproven;
    public static String alphabet = "abcdefghijklmnopqrstuvwxyz";

    
    /**Expression Tree  */
    public ExprTree exprtree;
	
    /** Expression constructor. Assumes expression is valid. */
    public Expression (String name) throws IllegalLineException {
        myname = name;
        isproven = false;
        try {
        exprtree = new ExprTree(name);
        } catch (IllegalLineException e) {
            throw e;
        }
    }
    
    public boolean equals(Object obj) {
        Expression second;
        try {
            second = (Expression) obj;
        } catch (IllegalArgumentException e) {
            throw e;
        }
        return (myname.equals(second.myname)) && isproven == second.isproven
                && treeEquals(exprtree, second.exprtree);
    }
        

	
    /**Binary Tree split along implications or logical operators (&, |, ~) */
    static class ExprTree {

        ExprTreeNode myRoot;
        
        ExprTree (String s) throws IllegalLineException {
            if (s != null) {
                myRoot = treehelper(s);
            }
            throw new IllegalLineException("Input null string for expression");
        }
        
        /** Parses a string to create an expression tree that forks based on
         * operators (=> & | ~) and has leaves of single variables.
         * Throws exceptions if syntax is incorrect.
         * If string isn't surrounded by parentheses, it should be ~~~a or some form.
         * If surrounded by parentheses, should be properly nested (equal amount left/right) */
        ExprTreeNode treehelper (String s) throws IllegalLineException {
            // Initialize node w/ dummy string & no children (always replaced)
            ExprTreeNode node = new ExprTreeNode("To Be Replaced");
            String first = s.substring(0, 1);
            String last = s.substring(s.length()-1, s.length());
            
            // If first element is (, last must be ) as well
            if (first.equals("(") && !last.equals(")")) {
                throw new IllegalLineException("Expressions starting w/ ( must end w/ )");
            }
            
            // One-element string? Just make it a node(leaf)
            // However, that element MUST be a letter - throw exception otherwise
            if (s.length() == 1) {
                if (!alphabet.contains(s)) {
                    throw new IllegalArgumentException("Branched to single element, wasn't variable.");
                }
                node.myItem = s;
                return node;
            }
            
            // String wasn't one-element, therefore it should start with only ~ or (
            if (first != "~" || first != "(") {
                throw new IllegalArgumentException("Branched to substring of length > 1,"
                        + "didn't start with ( or ~ (expression must either be wrapped() or ~");
            }
            String rest = s.substring(1);
            
            // Negation symbols are single nodes; child (rest of expression)
            // is always to its right
            if (first == "~") {
                node.myItem = ("~");
                node.myrightchild = treehelper(rest);
            }
            
            // Keep track of parentheses as expression nests ((a & b) => c) => (a | b))
            // Occurrences of ) decrement parencount, occurrences of ( increment
            // If parencount is ever negative, then there are more ) than (
            // If parencount is non-0 and i has indexed the whole string,
            // then there aren't equal amounts of ( ) - throw exception
            String operands = "=> & |";
            int parencount = 0;
            for(int i = 0; i < rest.length(); i ++) {
                if (parencount < 0) {
                    throw new IllegalArgumentException("More ) than (");
                }
                if (rest.charAt(i) == '(') {
                    parencount ++;
                } else if (rest.charAt(i) == ')') {
                    parencount --;
                } else if (rest.charAt(i) == '>' && rest.charAt(i-1) == '=') {
                    node.myItem = "=>";
                    String preop = s.substring(1, i-1);
                    node.myleftchild = treehelper(preop);
                    String postop = s.substring(i+1, s.length());
                    node.myrightchild = treehelper(postop);
                    return node;
                }
                
            }
            return node;
        }
        
        
        /**Nodes of the expression tree. Contain only Strings */
        static class ExprTreeNode {
            
            /** The item held at this node. */
            private String myItem;
            private ExprTreeNode myleftchild;
            private ExprTreeNode myrightchild;
            
            ExprTreeNode(String s) {
                myItem = s;
                myleftchild = myrightchild = null;
            }
            
            /** Make a "full" node with left and right children. */
            ExprTreeNode(String node, String left, String right) {
                myItem = node;
                myleftchild = new ExprTreeNode(left);
                myrightchild = new ExprTreeNode(right);
            }
            
            /** Get node's item (always a string) */
            public String get() {
                return myItem;
            }
            
            /** Return left child, a Node. */
            public ExprTreeNode getleft() {
                return myleftchild;
            }
            
            /** Return right child, a Node. */
            public ExprTreeNode getright() {
                return myrightchild;
            }
        }
        
    }
	
}
