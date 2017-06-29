package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

public abstract class RPNNode {

	protected boolean isLeaf;

	public String toString(){
		return this.toInfixString();
	}
	
	public String toInfixString() {

		if (this instanceof RPNNumber)
			return ((RPNNumber) this).getValue() + "";
		else if (this instanceof RPNVariable)
			return ((RPNVariable) this).getValue();
		else if (this instanceof RPNFunctionSymbol)
			return ((RPNFunctionSymbol) this).getLeft().toInfixString() + " "
					+ ((RPNFunctionSymbol) this).getFunctionSymbol() + " "
					+ ((RPNFunctionSymbol) this).getRight().toInfixString();

		return "ERROR";
	}

	public String toPrefixString() {

		if (this instanceof RPNNumber)
			return ((RPNNumber) this).getValue() + "";
		else if (this instanceof RPNVariable)
			return ((RPNVariable) this).getValue();
		else if (this instanceof RPNFunctionSymbol)
			return ((RPNFunctionSymbol) this).getFunctionSymbol() + " "
					+ ((RPNFunctionSymbol) this).getLeft().toPrefixString() + " "
					+ ((RPNFunctionSymbol) this).getRight().toPrefixString();

		return "ERROR";
	}

	public String toSuffixString() {

		if (this instanceof RPNNumber)
			return ((RPNNumber) this).getValue() + "";
		else if (this instanceof RPNVariable)
			return ((RPNVariable) this).getValue();
		else if (this instanceof RPNFunctionSymbol)
			return ((RPNFunctionSymbol) this).getLeft().toSuffixString() + " "
					+ ((RPNFunctionSymbol) this).getRight().toSuffixString() + " "
					+ ((RPNFunctionSymbol) this).getFunctionSymbol();

		return "ERROR";
	}
}
