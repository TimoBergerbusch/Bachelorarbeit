package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

import aprove.DPFramework.BasicStructures.TRSCompoundTerm;
import aprove.DPFramework.BasicStructures.TRSConstantTerm;
import aprove.DPFramework.BasicStructures.TRSTerm;
import aprove.DPFramework.BasicStructures.TRSVariable;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.GeoNonTermAnalysis;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.ArithmeticSymbol;

/**
 * The RPNTreeParser provides a <code>static</code> method to parse a
 * {@link TRSTerm} into a RPNTree using {@link RPNNode}.
 * 
 * @author Timo Bergerbusch
 * 
 * @see TRSTerm
 * @see RPNNode
 */
public class RPNTreeParser {

    /**
     * The parseSetToTree-method takes a {@link TRSTerm} and parses it into a
     * RPNTree of {@link RPNNode RPNNode's} using the subclasses to represent
     * the corresponding {@link TRSTerm TRSTerm's}. <br>
     * <table>
     * <tr>
     * <td>TRSTerm-subclass</td>
     * <td>corresponding RPNNode-subclass</td>
     * </tr>
     * <tr>
     * <td>{@link TRSVariable}</td>
     * <td>{@link RPNVariable}</td>
     * </tr>
     * <tr>
     * <td>{@link TRSConstantTerm}</td>
     * <td>{@link RPNConstant}</td>
     * </tr>
     * <tr>
     * <td>{@link TRSCompoundTerm}</td>
     * <td>{@link RPNFunctionSymbol}</td>
     * </tr>
     * </table>
     * The method can work straight forward for {@link TRSVariable
     * TRSVariable's} and {@link TRSConstantTerm TRSConstantTerm's} and
     * recursively calls {@link #parseSetToTree(TRSTerm)} for the nested terms
     * of a {@link TRSCompoundTerm} to derive the child {@link RPNNode
     * RPNNode's} of a RPNFunctionSymbol.
     * 
     * WARNING: The method only provides a binary tree and does <u>not</u>
     * support any {@link ArithmeticSymbol ArithmeticalSymbol's} other than the
     * three dyadic {@link ArithmeticSymbol ArithmeticalSymbol's} stated.
     * 
     * 
     * @param trsTerm
     *            the term that should be parsed
     * @return the root node of the tree derived from the trsTerm
     * @throws UnsupportetArithmeticSymbolException
     *             throws a {@link UnsupportetArithmeticSymbolException} if any
     *             other than the stated {@link ArithmeticSymbol
     *             ArithmeticSymbol's} occurs.
     */
    public static RPNNode parseSetToTree(TRSTerm trsTerm) throws UnsupportetArithmeticSymbolException {

	if (trsTerm instanceof TRSVariable) {
	    return new RPNVariable(trsTerm.toString());
	} else if (trsTerm instanceof TRSConstantTerm) {
	    return new RPNConstant(Integer.parseInt(trsTerm.toString()));
	} else if (trsTerm instanceof TRSCompoundTerm) {
	    TRSCompoundTerm compountTerm = ((TRSCompoundTerm) trsTerm);
	    ArithmeticSymbol symbol;
	    if (compountTerm.getFunctionSymbol().toString().equals("+_2"))
		symbol = ArithmeticSymbol.PLUS;
	    else if (compountTerm.getFunctionSymbol().toString().equals("-_2"))
		symbol = ArithmeticSymbol.MINUS;
	    else if (compountTerm.getFunctionSymbol().toString().equals("*_2"))
		symbol = ArithmeticSymbol.TIMES;
	    else if (compountTerm.getFunctionSymbol().toString().equals("<_2"))
		symbol = ArithmeticSymbol.LESS_THAN;
	    else if (compountTerm.getFunctionSymbol().toString().equals(">_2"))
		symbol = ArithmeticSymbol.GREATER_THAN;
	    else
		throw new UnsupportetArithmeticSymbolException(compountTerm.getFunctionSymbol().toString());

	    RPNNode left = parseSetToTree(compountTerm.getArgument(0));
	    RPNNode right = parseSetToTree(compountTerm.getArgument(1));

	    return new RPNFunctionSymbol(symbol, left, right);
	}

	// GeoNonTermAnalysis.LOG.endClassOutput("RPNStringParser");
	GeoNonTermAnalysis.LOG.writeln("NICHT GUT");
	return null;
    }
}