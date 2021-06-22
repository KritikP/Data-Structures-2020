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
    	StringTokenizer tk = new StringTokenizer(expr, delims);
    	while(tk.hasMoreTokens()) {
    		String tokenItem = tk.nextToken().trim();
    		int i = 0;
    		String newExpr = expr.trim();
    		while(i < newExpr.length()) {
    			newExpr = newExpr.trim();
    			newExpr = " " + newExpr + " ";
    			if(Character.isDigit(tokenItem.charAt(0)))
    				break;
    			else {
    				int j = newExpr.indexOf(tokenItem);
    				if(!Character.isLetter(newExpr.charAt(j - 1)) && !Character.isLetter(newExpr.charAt(j + tokenItem.length()))) {
    					if(newExpr.charAt(j + tokenItem.length()) == '[') {
    						arrays.add(new Array(tokenItem));
    						i = newExpr.length();
    					}
    					else {
    						vars.add(new Variable(tokenItem));
    						i = newExpr.length();
    					}
    				}
    				else {
    					newExpr = newExpr.substring(j);
    				}
    					
    			}
    		}
    	}
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
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	expr = expr.replaceAll(" ", "");
    	if(expr.isEmpty())
    		return 0;
    	StringTokenizer tk = new StringTokenizer(expr, delims);  // Tokenize the expression
    	Stack<String> values = new Stack<String>();  // Stack for the values (arrays, variables, and straight constants)
    	Stack<Integer> valueIndexes = new Stack<Integer>();  //Stack for value indexes
    	Stack<String> operators = new Stack<String>();  //Stack for operators
    	Stack<Integer> operatorIndexes= new Stack<Integer>();  //Stack for operator indexes
    	
    	for(int i = 0; i < expr.length(); i++) {
    		char ch = expr.charAt(i);
    		if(Character.isDigit(ch) || Character.isLetter(ch)) {
    			String tokenItem = tk.nextToken();
    			values.push(tokenItem);
    			valueIndexes.push(i);
    			i += tokenItem.length() - 1;
    		}
    		
    		else if(ch == '(' || ch == '[') {
    			operators.push(Character.toString(ch));
    			operatorIndexes.push(i);
    		}
    		
    		else if(ch == ')') {
    			int index = 0;
    			String op = "";
    			while(!op.equals("(")) {
    				op = operators.pop();
    				index = operatorIndexes.pop();
    			}
    			int valIndex = i;
    			while(valIndex > index) {
    				if(!values.isEmpty()) {
    					values.pop();
    					valIndex = valueIndexes.pop();
    				}
    				else
    					valIndex = index;
    			}
    			
    			String evalParen = Float.toString(evaluate(expr.substring(index + 1, i), vars, arrays));
    			expr = expr.substring(0, index) + evalParen + expr.substring(i + 1);
    			return evaluate(expr, vars, arrays);
    		}
    		
    		else if(ch == ']') {
    			String op = "";
    			while(!op.equals("[")) {
    				values.pop();
    				valueIndexes.pop();
    				op = operators.pop();
    				operatorIndexes.pop();
    			}
    			
    			String arr = values.pop().trim();
    			int index = valueIndexes.pop();
    			int arrVal = 0;
    			int arrIndex = 0;
    			for(int j = 0; j < arrays.size(); j++) {
    				if(arrays.get(j).name.equals(arr)) {
    					arrIndex = (int)evaluate(expr.substring(index + arr.length() + 1, i), vars, arrays);
    					arrVal = arrays.get(j).values[(int)arrIndex];
    					return evaluate(expr.substring(0, index) + arrVal + expr.substring(i + 1), vars, arrays);
    				}
    				
    			}
    			
    		}
    		
    		else if(ch == '+' || ch == '-' || ch == '*' || ch == '/') {
    			operators.push(Character.toString(ch));
    			operatorIndexes.push(i);
    		}
    	}
    	
    	String val = values.pop().trim();
    	valueIndexes.pop();
    	if(operators.isEmpty()) {
    		if(Character.isDigit(val.charAt(0))) {
    			return Float.parseFloat(val);
    		}
    		else {
    			for(int i = 0; i < vars.size(); i++) {
    				if(val.equals(vars.get(i).name)) {
    					return vars.get(i).value;
    				}
    			}
    		}
    		
    	}
    	
    	else{
    		String op = operators.pop();
    		int opIndex = operatorIndexes.pop();
    		if(op.equals("-")) {
    			if(opIndex == 0)
    				return 0 - evaluate(expr.substring(1), vars, arrays);
    			
    			if(Character.isDigit(expr.charAt(opIndex - 1)) || Character.isLetter(expr.charAt(opIndex - 1)))
    				return evaluate(expr.substring(0, opIndex), vars, arrays)
    					- evaluate(expr.substring(opIndex + 1), vars, arrays);
    			
    			if(expr.charAt(opIndex - 1) == '-')
    				return evaluate(expr.substring(0, opIndex - 1) + "+"
    						+ expr.substring(opIndex + 1), vars, arrays);
    			
    			val = "-" + evaluate(val, vars, arrays);
    			op = operators.pop();
    		}
    		
    		if(op.equals("+")) {
    			return evaluate(expr.substring(0, opIndex), vars, arrays)
    					+ evaluate(expr.substring(opIndex + 1), vars, arrays);
    		}
    		
    		String val2 = values.pop();
			int val2index = valueIndexes.pop();
    		if(op.equals("*")) {    			
    			if(operators.isEmpty())
    				return evaluate(expr.substring(0, val2index)
    		    		+ (evaluate(val, vars, arrays) * evaluate(val2, vars, arrays)), vars, arrays);
    			else {
    				op = operators.pop();
    				opIndex = operatorIndexes.pop();
    				if(op.equals("/")) {
    					return evaluate(expr.substring(0, val2index + val2.length()), vars, arrays) * evaluate(val, vars, arrays);
    				}
					else {
						return evaluate(expr.substring(0, val2index)
							+ (evaluate(val, vars, arrays) * evaluate(val2, vars, arrays)), vars, arrays);
    				}
    			}
    		}
    		
    		if(op.equals("/")) {
    			if(operators.isEmpty())
    				return evaluate(expr.substring(0, val2index)
    						+ (evaluate(val2, vars, arrays) / evaluate(val, vars, arrays)), vars, arrays);
    			else {
    				op = operators.pop();
    				opIndex = operatorIndexes.pop();
    				if(op.equals("/")) {
    					return evaluate(expr.substring(0, val2index + val2.length()), vars, arrays) / evaluate(val, vars, arrays);
    				}
					else {
    					return evaluate(expr.substring(0, val2index)
        						+ (evaluate(val2, vars, arrays) / evaluate(val, vars, arrays)), vars, arrays);
    				}
    			}
    		}
    	}
    	
    	return 0;
    }
}
