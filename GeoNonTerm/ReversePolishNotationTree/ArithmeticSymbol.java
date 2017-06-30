package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

/**
 * The <code>enum</code> type of the supported arithmetical operators within the
 * RPNTree used mainly within the {@link RPNFunctionSymbol}.
 * 
 * @author Timo Bergerbusch
 *
 * @see RPNFunctionSymbol
 * @see UnsupportetArithmeticSymbolException
 */
public enum ArithmeticSymbol {

    /**
     * The three supported arithmetic operators:
     * <table>
     * <tr>
     * <td>Name</td>
     * <td>represents</td>
     * </tr>
     * <tr>
     * <td><code>PLUS</code>
     * <td>+</td>
     * </tr>
     * <tr>
     * <td><code>MINUS</code>
     * <td>-</td>
     * </tr>
     * <tr>
     * <td><code>TIMES</code>
     * <td>*</td>
     * </tr>
     * </table>
     */
    PLUS, MINUS, TIMES;

    /**
     * gives the arithmetic operator as it's representing
     * <code>String</code>.<br>
     * It's mainly used within the {@link RPNFunctionSymbol#toPrefixString()},
     * {@link RPNFunctionSymbol#toInfixString()} and
     * {@link RPNFunctionSymbol#toSuffixString()}.
     * 
     * @return the common symbol of the arithmetic operation
     * 
     * @see RPNFunctionSymbol
     */
    public String toString() {
	switch (this) {
	case PLUS:
	    return " + ";
	case MINUS:
	    return " - ";
	case TIMES:
	    return " * ";
	default:
	    return "ENUM toString() Error";
	}
    }
}
