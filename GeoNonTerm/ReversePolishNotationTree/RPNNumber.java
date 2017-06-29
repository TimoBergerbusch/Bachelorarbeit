package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

public class RPNNumber extends RPNNode{

	
	private int value;
	
	public RPNNumber(int value){
		this.setValue(value);
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	
}
