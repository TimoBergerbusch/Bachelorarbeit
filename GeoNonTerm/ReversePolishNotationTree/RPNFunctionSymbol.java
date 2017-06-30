package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

import aprove.DPFramework.BasicStructures.TRSCompoundTerm;

/**
 * The RPNFunctionSymbol represents a {@link TRSCompoundTerm} within the RPNTree
 * (reverse polish notation tree). <br>
 * Since it contains arithmetic operators it contains an <code>enum</code> with
 * the supported arithmetical operators and contains child {@link RPNNode
 * RPNNode's}.
 * 
 * @author Timo Bergerbusch
 *
 * @see RPNNode
 * @see ArithmeticSymbol
 * @see UnsupportetArithmeticSymbolException
 */
public class RPNFunctionSymbol extends RPNNode {

    protected RPNNode left, right;

    private ArithmeticSymbol arithmeticSymbol;

    public RPNFunctionSymbol(ArithmeticSymbol symbol) {
	this.arithmeticSymbol = symbol;
    }

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
