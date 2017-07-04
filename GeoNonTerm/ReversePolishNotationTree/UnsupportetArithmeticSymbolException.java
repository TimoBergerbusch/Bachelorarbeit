package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.ArithmeticSymbol;

/**
 * The UnsupportetArithmeticSymbolException is a subclass of {@link Exception}
 * and is used to indicate that an other {@link ArithmeticSymbol} than
 * {@link ArithmeticSymbol#PLUS + (PLUS)}, {@link ArithmeticSymbol#MINUS -
 * (MINUS)} or {@link ArithmeticSymbol#TIMES * (TIMES)} is used. <br>
 * 
 * @author Timo Bergerbusch
 *
 */
public class UnsupportetArithmeticSymbolException extends Exception {

    /**
     * auto generated serialVersion
     */
    private static final long serialVersionUID = -8053037530554020581L;

    /**
     * creates a new UnsupportetArithmeticSymbolException with the unsupported
     * {@link ArithmeticSymbol} as message.
     * 
     * @param message
     *            the unsupported {@link ArithmeticSymbol}
     */
    public UnsupportetArithmeticSymbolException(String message) {
	super(message);
    }

}
