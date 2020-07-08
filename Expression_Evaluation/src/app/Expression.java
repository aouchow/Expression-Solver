package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	
    	String temp = new String ();
    	for (int i = 0; i < expr.length(); i++) {
    		Character c = expr.charAt(i);
    		if (Character.isLetter(c)) {
    			temp += c;
    		}
    		else {
    			if (temp.isEmpty()) {
    				continue;
    			}
    			if (c == '[') {
    				Array array = new Array (temp);
    				if (!arrays.contains(array)) {
    					arrays.add(array);
    				}
    			}
    			else {
    				Variable variable = new Variable (temp);
    				if (!vars.contains(variable)) {
    					vars.add(variable);
    				}
    			}
    			temp = "";
    		}
    	}
    	if (!temp.isEmpty()) {
			Variable variable = new Variable (temp);
			if (!vars.contains(variable)) {
				vars.add(variable);
			}
    	}
//    	for (int i = 0; i < arrays.size(); i++) {
//    		System.out.println("arrays =" + arrays.get(i).name);
//    	}
//    	
//    	for (int i = 0; i < vars.size(); i++) {
//    		System.out.println("vars =" + vars.get(i).name);
//    	}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	
    	//remove all spaces and tabs
    	expr = expr.replaceAll(" ", "");
    	expr = expr.replaceAll("\t", "");

    	Queue <Character> operators = new LinkedList <Character>();
    	Queue <Float> variableValues = new LinkedList <Float>();
    	float result = 0;
    
    	//add variables and operators in the string to the queues
    	int var = 0;
    	int op = 0;
    	for (op = 0; op < expr.length(); op++) {
    		if (expr.charAt(op) == '(') {
    			int closingIndex = findClosingParenthesesIndex (expr, op);
//    			System.out.println("substring inside parentheses =" + expr.substring(op + 1, closingIndex));
    			float answer = evaluate (expr.substring(op + 1, closingIndex), vars, arrays);
//    			System.out.println("parentheses answer =" + answer);
    			variableValues.add(answer);
    			op = closingIndex + 1;
    			if (op < expr.length()) {
    				operators.add(expr.charAt(op));
    			}
    			var = op + 1;
    		}
    		else if (expr.charAt(op) == '[') {
    			int closingIndex = findClosingBracketIndex (expr, op);
//    			System.out.println("openingIndexArray=" + op);
//    			System.out.println("closingIndexArray=" + closingIndex);
    			
    			//evaluate returns float, cast float to int for array index
    			int arrayindex = (int)evaluate(expr.substring(op + 1, closingIndex), vars, arrays);
    			
    			//get the array value from array list
    			Array array = new Array (expr.substring(var, op));
    			float arrayValue = (float)arrays.get(arrays.indexOf(array)).values[arrayindex];
    			variableValues.add(arrayValue);
    			op = closingIndex + 1;
    			if (op < expr.length()) {
    				operators.add(expr.charAt(op));
    			}
    			var = op + 1;
    		}
    		else if (expr.charAt(op) == '+' || expr.charAt(op) == '-' || expr.charAt(op) == '*' || expr.charAt(op) == '/') {
    			Float varValue = null;
    			if (op == var) {
    				continue;
    			}
    			if (isNumeric(expr.substring(var, op))) {
    				varValue = Float.parseFloat(expr.substring(var, op));
    			}
    			else {
    				varValue = (float)vars.get(vars.indexOf(new Variable (expr.substring(var, op)))).value;
    			}
    			variableValues.add(varValue);
    			operators.add(expr.charAt(op));
    			var = op + 1;
    		}
    	}
//    	System.out.println("var pointer=" + var);
//    	System.out.println("op pointer=" + op);
    	// to get load the last variable/number into the queue
    	if (var < expr.length()) {
			Float varValue = null;
			if (isNumeric(expr.substring(var, expr.length()))) {
				varValue = Float.parseFloat(expr.substring(var, expr.length())); 
			}
			else {
				varValue = (float)vars.get(vars.indexOf(new Variable (expr.substring(var, expr.length())))).value;
			}
			variableValues.add(varValue);		
    	}
    	
//    	while (!variableValues.isEmpty()) {
//    		System.out.println("variables stack =" + variableValues.remove());
//    	}
//    	while (!operators.isEmpty()) {
//    		System.out.println("operators stack =" + operators.remove());
//    	}
		
		//order of operations
    	Queue <Character> addSubtractOperators = new LinkedList <Character>();
    	Queue <Float> newVariables = new LinkedList <Float>();
    	Float previous = variableValues.remove();
		while (!operators.isEmpty()) {
			Character c = operators.remove();
			if (c == '+' || c == '-') {
				addSubtractOperators.add(c);
				newVariables.add(previous);
				previous = variableValues.remove();
			}
			else {
				previous = evalPair(previous, variableValues.remove(), c);
//				System.out.println("previous =" + previous);
			}
		}
		newVariables.add(previous);
		
		//update the old queue to equal the new queues
		operators = addSubtractOperators;
		variableValues = newVariables;
		
//		while (!operators.isEmpty()) {
//			System.out.println("operators queue=" + operators.remove());
//		}
//		while (!variables.isEmpty()) {
//			System.out.println("variables queue=" + variables.remove());
//		}
		
		//do pair-wise evaluation and add result back to queue until everything has been evaluated
		result = variableValues.remove();
		while (!operators.isEmpty()) {
	    	char operator = operators.remove();
//	    	System.out.println("operator = " + operator);
	    	result = evalPair (result, variableValues.remove(), operator);
//	    	System.out.println("evalPair result =" + result);
    	}
    	return result;
    }
    
    private static float evalPair(float val1, float val2, char operator) {
    	float value = 0;
    	switch (operator) {
    		case '+':
    			return value = val1 + val2;
    		case '-':
    			return value = val1 - val2;
    		case '*':
    			return value = val1 * val2;
    		case '/':
    			return value = val1 / val2;
    	}
//    	System.out.println("error with operator");
		return value;
    }
    
    private static boolean isNumeric(String str) { 
    	  try {  
    	    Float.parseFloat(str);  
    	    return true;
    	  } catch(NumberFormatException e){  
    	    return false;  
    	  }  
    	}
    
    private static int findClosingParenthesesIndex (String expr, int openingIndex) {
    	int count = 1;
    	int closingIndex = 0;
    	for (int i = openingIndex + 1; i < expr.length(); i++) {
    		if (expr.charAt(i) == '(') {
    			count++;
    		}
    		else if (expr.charAt(i) == ')') {
    			count--;
    		}
    		if (count == 0) {
    			closingIndex = i;
    			break;
    		}
    	}
    	return closingIndex;
    }
    
    private static int findClosingBracketIndex (String expr, int openingIndex) {
    	int count = 1;
    	int closingIndex = 0;
    	for (int i = openingIndex + 1; i < expr.length(); i++) {
    		if (expr.charAt(i) == '[') {
    			count++;
    		}
    		else if (expr.charAt(i) == ']') {
    			count--;
    		}
    		if (count == 0) {
    			closingIndex = i;
    			break;
    		}
    	}
    	return closingIndex;
    }
}
