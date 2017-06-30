package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

import aprove.DPFramework.BasicStructures.TRSConstantTerm;

/**
 * The RPNConstant represents a {@link TRSConstantTerm} within the RPNTree
 * (reverse polish notation tree). Since it is a constant <code>int</code>-term
 * it can't have any following {@link RPNNode RPNNode's}
 * 
 * @author Timo Bergerbusch
 *
 * @see RPNNode
 */
public class RPNConstant extends RPNNode {

    /**
     * the constant <code>int</code> value
     */
    private int value;

    /**
     * creates a new {@link RPNConstant} and sets the {@link #value} accordingly
     * to the parameter
     * 
     * @param value the initial {@link #value}
     */
    public RPNConstant(int value) {
	this.setValue(value);
    }

    /**
     * getter of {@link #value}
     * 
     * @return the value
     */
    public int getValue() {
	return value;
    }

    /**
     * setter of {@link #value}
     * 
     * @param value
     *            the value to set
     */
    public void setValue(int value) {
	this.value = value;
    }

}
