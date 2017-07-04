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
     * The five supported arithmetic operators:
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
     * <tr>
     * <td><code>LESS_THAN</code>
     * <td>&gt;</td>
     * </tr>
     * <tr>
     * <td><code>GREATER_THAN</code>
     * <td>&lt;</td>
     * </tr>
     * </table>
     */
    PLUS, MINUS, TIMES, LESS_THAN, GREATER_THAN;

    /**
     * gives the arithmetic operator as it's representing <code>String</code>.<br>
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
	case LESS_THAN:
	    return " < ";
	case GREATER_THAN:
	    return " > ";
	default:
	    return "ENUM toString() Error";
	}
    }
}
