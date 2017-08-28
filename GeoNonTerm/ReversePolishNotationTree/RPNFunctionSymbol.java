package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

import aprove.DPFramework.BasicStructures.TRSCompoundTerm;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.Logger;

/**
 * The RPNFunctionSymbol represents a {@link TRSCompoundTerm} within the RPNTree
 * (reverse polish notation tree). <br>
 * Since it contains arithmetic operators it contains an <code>enum</code> with
 * the supported arithmetical operators and contains child {@link RPNNode
 * RPNNode's}. <br>
 * The {@link ArithmeticSymbol} are all binary operators and therefore the
 * {@link RPNFunctionSymbol} has two child nodes.
 * 
 * @author Timo Bergerbusch
 *
 * @see RPNNode
 * @see ArithmeticSymbol
 * @see UnsupportetArithmeticSymbolException
 */
public class RPNFunctionSymbol extends RPNNode {

    /**
     * the left child node of this {@link ArithmeticSymbol}. <br>
     * It's a {@link RPNNode} because of the possible sub-classes.
     */
    protected RPNNode left;

    /**
     * the right child node of this {@link ArithmeticSymbol}. <br>
     * It's a {@link RPNNode} because of the possible sub-classes.
     */
    protected RPNNode right;

    /**
     * the {@link ArithmeticSymbol} connecting the {@link #left} and
     * {@link #right} children.
     */
    private ArithmeticSymbol arithmeticSymbol;

    /**
     * creates a new {@link RPNFunctionSymbol} with two children.
     * 
     * @param symbol
     *            the {@link #arithmeticSymbol}
     * @param left
     *            the {@link #left}-child
     * @param right
     *            the {@link #right}-child
     */
    public RPNFunctionSymbol(ArithmeticSymbol symbol, RPNNode left, RPNNode right) {
	this.arithmeticSymbol = symbol;
	this.left = left;
	this.right = right;
    }

    /**
     * @return the functionSymbol
     */
    public ArithmeticSymbol getFunctionSymbol() {
	return arithmeticSymbol;
    }

    /**
     * @param functionSymbol
     *            the functionSymbol to set
     */
    public void setFunctionSymbol(ArithmeticSymbol functionSymbol) {
	this.arithmeticSymbol = functionSymbol;
    }

    /**
     * @return the left
     */
    public RPNNode getLeft() {
	return left;
    }

    /**
     * @param left
     *            the left to set
     */
    public void setLeft(RPNNode left) {
	this.left = left;
    }

    /**
     * @return the right
     */
    public RPNNode getRight() {
	return right;
    }

    /**
     * @param right
     *            the right to set
     */
    public void setRight(RPNNode right) {
	this.right = right;
    }

    public RPNNode remove(RPNNode rem) {
	if (this.getLeft() == rem)
	    return this.right;
	else if (this.getRight() == rem)
	    return this.left;
	else
	    return new RPNFunctionSymbol(arithmeticSymbol, left.remove(rem), right.remove(rem));
    }

    public RPNConstant getConstantNode() {
	// WARNING MAY NOT BE CORRECT
	if (arithmeticSymbol == ArithmeticSymbol.TIMES)
	    return null;
	RPNConstant node1 = left.getConstantNode();
	RPNConstant node2 = right.getConstantNode();
	assert node1 == null || node2 == null;
	if (node1 != null)
	    return node1;
	return node2;
    }

    public RPNNode negate() {
	if (this.arithmeticSymbol == ArithmeticSymbol.TIMES) {
	    this.left = this.left.negate();
	} else if (this.arithmeticSymbol == ArithmeticSymbol.PLUS) {
	    this.left = this.left.negate();
	    this.right = this.right.negate();
	} else {
	    Logger.getLog().writeln("NICHT GUT");
	}
	return this;
    }

    public RPNNode clone() {
	return new RPNFunctionSymbol(arithmeticSymbol, this.left.clone(), this.right.clone());
    }

    public RPNNode applySubstitution(RPNVariable var, RPNNode sub) {
	if (left.containsVar(var.getValue()))
	    this.left = this.left.applySubstitution(var, sub);
	if (right.containsVar(var.getValue()))
	    this.right = this.right.applySubstitution(var, sub);

	return this;
    }
}
