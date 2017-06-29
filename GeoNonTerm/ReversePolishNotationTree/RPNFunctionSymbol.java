package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

public class RPNFunctionSymbol extends RPNNode {

	protected enum ArithmeticSymbol {
		PLUS, MINUS, TIMES;

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
		isLeaf = left == null && right == null;
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
		isLeaf = left == null && right == null;
	}
}
