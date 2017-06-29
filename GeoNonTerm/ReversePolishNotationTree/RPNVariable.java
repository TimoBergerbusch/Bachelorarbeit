package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

import aprove.DPFramework.BasicStructures.TRSTerm;

public class RPNVariable extends RPNNode {

	private String varName;

	public RPNVariable(String varName) {
		this.varName = varName;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.varName;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String varName) {
		this.varName = varName;
	}
}
