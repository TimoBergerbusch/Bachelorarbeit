package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.Logger;

/**
 * The RPNNode is an abstract node in the binary RPNTree (reverse polish
 * notation tree). <br>
 * The subclasses are {@link RPNConstant}, {@link RPNVariable} and
 * {@link RPNFunctionSymbol}
 * 
 * @author Timo Bergerbusch
 * 
 * @see RPNConstant
 * @see RPNVariable
 * @see RPNFunctionSymbol
 */
public abstract class RPNNode {

    /**
     * This method derives the factors of the given variable within the tree
     * using the following recursive algorithm: <br>
     * <ol>
     * <li>checks whether it is a {@link RPNVariable} or a {@link RPNConstant},
     * which both have no following {@link RPNNode RPNNode's} and the value can
     * be obviously derived
     * <li>for a {@link RPNFunctionSymbol} it distinguishes between the three
     * different {@link ArithmeticSymbol ArithmeticSymbol's}:
     * <ul>
     * <li>{@link ArithmeticSymbol#TIMES}: derives the factor recursively within
     * the child {@link RPNNode} that does <u>not</u> contain the variable,
     * because if would not contain the factor
     * <li>
     * {@link ArithmeticSymbol#LESS_THAN}/{@link ArithmeticSymbol#GREATER_THAN}/{@link ArithmeticSymbol#PLUS}:
     * derives the factor within the child that contains the var and can neglect
     * the other child, because of syntax reasons (more below)
     * <li>{@link ArithmeticSymbol#MINUS}: same as for
     * {@link ArithmeticSymbol#PLUS}, but changes the result by negating the
     * factor, because of the -
     * <li>if non of the other hold's the method returns <code>0</code>
     * </ul>
     * </ol>
     * <br>
     * The assumtion the method makes is that the terms are linear and
     * flattened. So 2*(a+b) would be 2*a+2*b
     * 
     * @param varName
     *            the name of the variable of which the factor should be derived
     * @return the factor of the parameter variable or 0
     * 
     * @see #containsVar(String)
     */
    public int getFactorOfVar(String varName) {
	if (this instanceof RPNVariable) {
	    // bei der Variablen selbst in der Value immer 1
	    if (((RPNVariable) this).getValue().equals(varName))
		return 1;
	} else if (this instanceof RPNConstant) {
	    // Bei einem Konstanten Wert ist der eintrag immer 0
	    // return ((RPNConstant) this).getValue();
	    return 0;
	} else if (this instanceof RPNFunctionSymbol) {
	    RPNFunctionSymbol func = ((RPNFunctionSymbol) this);

	    if (func.getFunctionSymbol() == ArithmeticSymbol.TIMES) {
		/*
		 * Bei TIMES kann man den zweiten Teil, welcher die Var enthält
		 * vernachlässigen, da im andern Teilbaum der Faktor steht.
		 * 
		 * ANNAHME: sowas wie (a*b)*varName ist nicht gültig
		 */
		if (func.getLeft().containsVar(varName)) {
		    if (!(func.getRight() instanceof RPNConstant))
			Logger.getLog().writeln("ERROR: getFactorOfVar nicht nach dem Schema const*var");
		    else
			return ((RPNConstant) func.getRight()).getValue();
		} else if (func.getRight().containsVar(varName)) {
		    if (!(func.getLeft() instanceof RPNConstant))
			Logger.getLog().writeln("ERROR: getFactorOfVar nicht nach dem Schema const*var");
		    else
			return ((RPNConstant) func.getLeft()).getValue();
		}

	    } else if (func.getFunctionSymbol() == ArithmeticSymbol.LESS_THAN
		    || func.getFunctionSymbol() == ArithmeticSymbol.GREATER_THAN
		    || func.getFunctionSymbol() == ArithmeticSymbol.LESS_THAN_OR_EQUAL
		    || func.getFunctionSymbol() == ArithmeticSymbol.GREATER_THAN_OR_EQUAL) {
		if (func.getRight().containsVar(varName))
		    return func.getRight().getFactorOfVar(varName);
		else
		    return func.getLeft().getFactorOfVar(varName);
	    } else if (func.getFunctionSymbol() == ArithmeticSymbol.PLUS) {
		/*
		 * Bei PLUS oder MINUS kann man den zweiten Teil, welcher nicht
		 * die Var enthält vernachlässigen, da sie keinen Einfluss auf
		 * den Factor haben kann
		 * 
		 * flipvalue setzt den Wert bei z.B. x3 - x8 auf -1 statt 1
		 */
		int flipvalue = 1;

		if (func.getFunctionSymbol() == ArithmeticSymbol.MINUS)
		    flipvalue = -1;
		if (func.getLeft().containsVar(varName))
		    return func.getLeft().getFactorOfVar(varName) * flipvalue;
		else
		    return func.getRight().getFactorOfVar(varName) * flipvalue;
	    } else if (func.getFunctionSymbol() == ArithmeticSymbol.EQUALS) {
		// TEST
		return func.left.getFactorOfVar(varName) + func.right.getFactorOfVar(varName);
	    }
	}

	return 0;
    }

    /**
     * this method derives the constant term within a {@link RPNNode RPNTree}.
     * If two child nodes containt a constant (should <u>never</u> happen) they
     * get added.
     * 
     * @return the constant term( 0 by default)
     */
    public int getConstantTerm() {
	if (this instanceof RPNConstant)
	    return ((RPNConstant) this).getValue();
	else if (this instanceof RPNFunctionSymbol) {
	    RPNFunctionSymbol fs = ((RPNFunctionSymbol) this);
	    int flipped = 1;
	    if (fs.getFunctionSymbol().equals(ArithmeticSymbol.MINUS))
		flipped = -1;
	    if (!fs.getFunctionSymbol().equals(ArithmeticSymbol.TIMES)) {
		int left = fs.getLeft().getConstantTerm();
		int right = fs.getRight().getConstantTerm() * flipped;
		return left + right;
	    }
	}

	return 0;
    }

    /**
     * checks recursively whether the RPNTree contains a variable
     * 
     * @param varName
     *            the variables name
     * @return a boolean indicating if the variable is within the RPNTree
     */
    public boolean containsVar(String varName) {
	if (this instanceof RPNVariable) {
	    if (((RPNVariable) this).getValue().equals(varName))
		return true;
	} else if (this instanceof RPNFunctionSymbol)
	    return ((RPNFunctionSymbol) this).left.containsVar(varName)
		    || ((RPNFunctionSymbol) this).right.containsVar(varName);
	return false;
    }

    /**
     * uses the {@link #toInfixString()} to create a <code>String</code> of the
     * RPNTree
     * 
     * @return the RPNTree as a <code>String</code> in infix notation
     */
    public String toString() {
	return this.toInfixString();
    }

    /**
     * creates a <code>String</code> of the RPNTree using the infix notation
     * 
     * @return the RPNTree in infix notation
     */
    public String toInfixString() {

	if (this instanceof RPNConstant)
	    return ((RPNConstant) this).getValue() + "";
	else if (this instanceof RPNVariable)
	    return ((RPNVariable) this).getValue();
	else if (this instanceof RPNFunctionSymbol)
	    return ((RPNFunctionSymbol) this).getLeft().toInfixString() + " "
		    + ((RPNFunctionSymbol) this).getFunctionSymbol() + " "
		    + ((RPNFunctionSymbol) this).getRight().toInfixString();

	return "ERROR";
    }

    /**
     * creates a <code>String</code> of the RPNTree using the prefix notation
     * 
     * @return the RPNTree in prefix notation
     */
    public String toPrefixString() {

	if (this instanceof RPNConstant)
	    return ((RPNConstant) this).getValue() + "";
	else if (this instanceof RPNVariable)
	    return ((RPNVariable) this).getValue();
	else if (this instanceof RPNFunctionSymbol)
	    return ((RPNFunctionSymbol) this).getFunctionSymbol() + " "
		    + ((RPNFunctionSymbol) this).getLeft().toPrefixString() + " "
		    + ((RPNFunctionSymbol) this).getRight().toPrefixString();

	return "ERROR";
    }

    /**
     * creates a <code>String</code> of the RPNTree using the suffix notation
     * 
     * @return the RPNTree in suffix notation
     */
    public String toSuffixString() {

	if (this instanceof RPNConstant)
	    return ((RPNConstant) this).getValue() + "";
	else if (this instanceof RPNVariable)
	    return ((RPNVariable) this).getValue();
	else if (this instanceof RPNFunctionSymbol)
	    return ((RPNFunctionSymbol) this).getLeft().toSuffixString() + " "
		    + ((RPNFunctionSymbol) this).getRight().toSuffixString() + " "
		    + ((RPNFunctionSymbol) this).getFunctionSymbol();

	return "ERROR";
    }

    public boolean containsEquality() {
	if (this instanceof RPNFunctionSymbol) {
	    RPNFunctionSymbol s = ((RPNFunctionSymbol) this);
	    if (s.getFunctionSymbol() == ArithmeticSymbol.EQUALS)
		return true;
	    else if (s.getFunctionSymbol() == ArithmeticSymbol.LESS_THAN
		    || s.getFunctionSymbol() == ArithmeticSymbol.GREATER_THAN)
		return false;
	    else {
		return s.getLeft().containsEquality() || s.getRight().containsEquality();
	    }
	} else
	    return false;
    }

    public RPNNode normalize() {
	if (this instanceof RPNVariable || this instanceof RPNConstant)
	    return this;
	else if (this instanceof RPNFunctionSymbol) {
	    RPNFunctionSymbol f = (RPNFunctionSymbol) this;

	    if (f.getFunctionSymbol() == ArithmeticSymbol.LESS_THAN
		    || f.getFunctionSymbol() == ArithmeticSymbol.LESS_THAN_OR_EQUAL
		    || f.getFunctionSymbol() == ArithmeticSymbol.GREATER_THAN
		    || f.getFunctionSymbol() == ArithmeticSymbol.GREATER_THAN_OR_EQUAL) {

		if (f.getRight() instanceof RPNFunctionSymbol) {
		    f.setLeft(new RPNFunctionSymbol(ArithmeticSymbol.PLUS, f.getLeft(), f.getRight().negate()));
		    f.setRight(new RPNConstant(0));
		}

		// wenn rechts die const direkt steht
		if (f.getLeft().getConstantTerm() != 0 && f.getRight().getClass() == RPNConstant.class) {
		    RPNNode cons = f.getLeft().getConstantNode();
		    f.setLeft(f.getLeft().remove(cons));
		    ((RPNConstant) f.getRight())
			    .setValue(((RPNConstant) f.getRight()).getValue() - ((RPNConstant) cons).getValue());
		}

		//drehen von less_than zu greater_than
		if (f.getFunctionSymbol() == ArithmeticSymbol.GREATER_THAN
			|| f.getFunctionSymbol() == ArithmeticSymbol.GREATER_THAN_OR_EQUAL) {
		    if (f.getFunctionSymbol() == ArithmeticSymbol.GREATER_THAN)
			f.setFunctionSymbol(ArithmeticSymbol.LESS_THAN);
		    else
			f.setFunctionSymbol(ArithmeticSymbol.LESS_THAN_OR_EQUAL);

		    f.setLeft(f.getLeft().negate());
		    f.setRight(f.getRight().negate());
		}

		//bringen auf die greater_than_or_equal form
		if(f.getFunctionSymbol() == ArithmeticSymbol.LESS_THAN){
		    f.getRight().getConstantNode().setValue(f.getRight().getConstantNode().getValue()-1);
		    f.setFunctionSymbol(ArithmeticSymbol.LESS_THAN_OR_EQUAL);
		}
	    }

	}

	return this;
    }

    public RPNNode negate() {
	return this;
    }

    public RPNNode remove(RPNNode rem) {
	return this;
    }

    public RPNConstant getConstantNode() {
	return null;
    }

    public abstract RPNNode clone();
    
    public abstract RPNNode applySubstitution(RPNVariable var, RPNNode sub);
}