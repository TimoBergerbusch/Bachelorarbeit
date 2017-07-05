package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

import aprove.DPFramework.BasicStructures.TRSCompoundTerm;

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
}
