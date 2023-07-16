package com.shpp.p2p.cs.ykryvoruchko.assignment11;

import java.util.*;

public class Assignment11Part1 {
    // //

    //expr : plusminus* END;

    //plusminus: multdiv ( ( '+' | '-' ) multdiv )* ;

    //multdiv : involution ( ( '*' | '/' ) involution )* ;

    //involution : number ( '^' ) number)*;

    //number : NUMBER | '(' expr ')' ;

    /** Elements which may include expression*/
    public enum ExpressionType {
        LBRACKET, RBRACKET,
        DIVISION, MULTIPLI, INVOLUTION,
        PLUS, MINUS,
        NUMBER,
        TRIG,
        END
    }

    /** Expression which include String value and type of expression - element */
    public static class Expression {
        ExpressionType type;
        String formulaPart;

        public Expression(ExpressionType type, String formulaPart){
            this.type = type;
            this.formulaPart = formulaPart;
        }

        public Expression(ExpressionType type, Character formulaPart){
            this.type = type;
            this.formulaPart = formulaPart.toString();
        }
    }

    /**Class which help us to operate with already parsed expression(List<Expression>) */
    public static class ExpressionAction {
        // variable to control position
        private int id;

        //parsed expression
        public List<Expression> expressions;

        public ExpressionAction(List<Expression> expressions) {
            this.expressions = expressions;
        }

        //get next element from list
        public Expression next() {
            return expressions.get(id++);
        }

        //set position variable back
        public void back() {
            id--;
        }

        //get current position in list
        public int getPos() {
            return id;
        }
    }

    /**parse our String formula to list of expressions and put variable value against char
     * if char equals special case it add to list
     * all chars will be checked
     * @param formula string with formula to parse
     * @param variable variables-values pairs
     * @param trig trigonometry tokens
     * @return list with parsed expressions
     * */
    public static List<Expression> expressionParse(String formula, HashMap<String, String> variable, ArrayList<String> trig){
        ArrayList<Expression> expressions = new ArrayList<>();
        int id = 0;
        while(id < formula.length()){
            char ch = formula.charAt(id);
            switch (ch) {
                case '(':
                    expressions.add(new Expression(ExpressionType.LBRACKET, ch));
                    id++;
                    continue;
                case ')':
                    expressions.add(new Expression(ExpressionType.RBRACKET, ch));
                    id++;
                    continue;
                case '*':
                    expressions.add(new Expression(ExpressionType.MULTIPLI, ch));
                    id++;
                    continue;
                case '/':
                    expressions.add(new Expression(ExpressionType.DIVISION, ch));
                    id++;
                    continue;
                case '^':
                    expressions.add(new Expression(ExpressionType.INVOLUTION, ch));
                    id++;
                    continue;
                case '+':
                    expressions.add(new Expression(ExpressionType.PLUS, ch));
                    id++;
                    continue;
                case '-':
                    if (id != 0 && !isMinusNumber(expressions)) {
                        expressions.add(new Expression(ExpressionType.MINUS, ch));
                        id++;
                        continue;
                    }

                    //if char is num, '.' or '-' which used to negative nums, it will concatenate nums
                    // if it no other chars between and if it letter it return value of our variable.
                    // it skips other chars.
                default:
                    if (ch <= '9' && ch >= '0' || ch == '-') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(ch);
                            id++;
                            if (id >= formula.length()) {
                                break;
                            }
                            ch = formula.charAt(id);
                        } while (ch <= '9' && ch >= '0' || ch == '.');
                        expressions.add(new Expression(ExpressionType.NUMBER, sb.toString()));
                    } else if (Character.isLetter(ch)) {
                        StringBuilder s = new StringBuilder();
                        do {
                            s.append(ch);
                            id++;
                            if (id >= formula.length()) {
                                break;
                            }
                            ch = formula.charAt(id);
                        } while (Character.isLetter(ch) || s.toString().equals("log") && ch == '2');
                        if(!variable.isEmpty() && variable.get(s.toString()) != null) {
                            expressions.add(new Expression(ExpressionType.NUMBER, variable.get(s.toString())));
                        }else if(trig.contains(s.toString())){
                            expressions.add(new Expression(ExpressionType.TRIG, s.toString()));
                        }else{
                            throw new RuntimeException("Unexpected variable: " + s);
                        }
                    } else {
                        if (ch != ' ') {
                            throw new RuntimeException("Unexpected character: " + ch);
                        }
                        id++;
                    }
            }
        }
        expressions.add(new Expression(ExpressionType.END, ""));
        return expressions;
    }


    /** if last element of list is left bracket it seems that in brackets will be a
     * (-2) or smth and we should concatenate it as nums
     * @param expressions expressions list
     * @return if it lbracket the last element of expressions list
     * */
    public static boolean isMinusNumber(ArrayList<Expression> expressions){
        return expressions.size() >= 1 && expressions.get(expressions.size() - 1).type == ExpressionType.LBRACKET;
    }

    /** it returns 0 or result of user entered String formula
     * @param expressions object to operate with expressions list
     * @return operation result
     * */
    public static double expression(ExpressionAction expressions) {
        Expression expression = expressions.next();
        if (expression.type == ExpressionType.END){
            return  0;
        }else {
            expressions.back();
            return plusmin(expressions);
        }

    }

    /** It is involute number expressions( two nums or two expressions or num and expression
     * @param expressions object to operate with expressions list
     * @return operation result
     * */
    public static double involution(ExpressionAction expressions) {
        double value = number(expressions);
        while(true) {
            Expression expression = expressions.next();
            if (expression.type == ExpressionType.INVOLUTION) {
                value = Math.pow(value, number(expressions));
            } else {
                expressions.back();
                return value;
            }
        }

    }

    /**Calculate expression in brackets using all priority levels
     * @param expressions object to operate with expressions list
     * @return operation result
     * */
    public static double number(ExpressionAction expressions) {
        Expression expression = expressions.next();
        double value = 0;
        switch (expression.type) {
            case NUMBER:
                return Double.parseDouble(expression.formulaPart);
            case TRIG:
                if(expression.formulaPart.equals("sin"))  value = Math.sin(plusmin(expressions));
                if(expression.formulaPart.equals("cos")) value = Math.cos(plusmin(expressions));
                if(expression.formulaPart.equals("tan")) value = Math.tan(plusmin(expressions));
                if(expression.formulaPart.equals("atan")) value = Math.atan(plusmin(expressions));
                if(expression.formulaPart.equals("log")) value = Math.log(plusmin(expressions)) / Math.log(10);
                if(expression.formulaPart.equals("sqrt")) value = Math.sqrt(plusmin(expressions));
                if(expression.formulaPart.equals("log2")) value = Math.log(plusmin(expressions)) / Math.log(2);
                return value;
            case LBRACKET:
                value = plusmin(expressions);
                expression = expressions.next();
                if (expression.type != ExpressionType.RBRACKET) {
                    throw new RuntimeException("Unexpected token: " + expression.formulaPart
                            + " at position: " + expressions.getPos());
                }
                return value;

            default:
                throw new RuntimeException("Unexpected token: " + expression.formulaPart
                        + " at position: " + expressions.getPos());
        }

    }

    /**Multiply or division result expressions with higher level priority
     * @param expressions object to operate with expressions list
     * @return operation result
     * */
    public static double muldiv(ExpressionAction expressions) {
        double value = involution(expressions);
        while(true) {
            Expression expression = expressions.next();
            switch (expression.type) {
                case MULTIPLI -> value *= involution(expressions);
                case DIVISION -> {
                    value /= involution(expressions);
                    if (Double.isInfinite(value)) {
                        throw new RuntimeException("Division to zero");
                    }
                }
                default -> {
                    expressions.back();
                    return value;
                }
            }
        }

    }

    /**
     * Plus or minus operations with result higher level expressions values
     * @param expressions object to operate with expressions list
     * @return operation result
     * */
    public static double plusmin(ExpressionAction expressions) {
        double value = muldiv(expressions);
        while(true) {
            Expression expression = expressions.next();
            switch (expression.type) {
                case PLUS -> value += muldiv(expressions);
                case MINUS -> value -= muldiv(expressions);
                default -> {
                    expressions.back();
                    return value;
                }
            }
        }

    }

    /**
     * Array list with trigonometry cases
     * @return list with trigonometry cases
     * */
    private static ArrayList<String> trigonometry(){
        ArrayList<String> list = new ArrayList<>();
        list.add("sin");
        list.add("cos");
        list.add("tan");
        list.add("atan");
        list.add("log");
        list.add("sqrt");
        list.add("log2");
        return list;
    }


    /**
     * Method which build strings that consist variable names and their values
     * @param args input strings with formula and variables
     * @return variables map variable-value pair
     * */
    private static HashMap<String, String> parse(String[] args) {
        HashMap<String, String> variables = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            StringBuilder letter = new StringBuilder();
            StringBuilder num = new StringBuilder();
            for (int j = 0; j < args[i].length(); j++) {
                char c = args[i].charAt(j);
                if (Character.isLetter(c)) {
                    do {
                        letter.append(c);
                        j++;
                        if (j >= args[i].length()) {
                            break;
                        }
                        c = args[i].charAt(j);
                    } while (Character.isLetter(c));
                } else if (Character.isDigit(c) || c == '-') {
                    do {
                        num.append(c);
                        j++;
                        if (j >= args[i].length()) {
                            break;
                        }
                        c = args[i].charAt(j);
                    } while (Character.isDigit(c) || c == '.');
                }
            }
            variables.put(String.valueOf(letter), String.valueOf(num));
        }
        return variables;
    }


    /**
     * Method which return string formula from args
     * @param args input strings with formula and variables
     * @return formula from args
     * */
    private static String isFormula(String[] args){
        try{
            String expression = args[0];
            expression = expression.replaceAll("," , ".");
            return expression;
        }catch (RuntimeException e){
            System.out.println("Formula and variables exists");
        }
        return "";
    }

    public static void main(String[] args) {
        HashMap<String, String> variables = parse(args);
        ArrayList<String> trig = trigonometry();
        String formula = isFormula(args);
        List<Expression> expressions = expressionParse(formula, variables, trig);
        ExpressionAction expressionAction = new ExpressionAction(expressions);
        System.out.println(expression(expressionAction));
    }
}