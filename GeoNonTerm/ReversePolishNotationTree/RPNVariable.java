package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

import aprove.DPFramework.BasicStructures.TRSVariable;

/**
 * A RPNVariable represents a {@link TRSVariable} within the RPNTree (reverse
 * polish notation tree). Since it is a variable it can't have any following
 * {@link RPNNode RPNNode's}
 * 
 * @author Timo Bergerbusch
 *
 * @see RPNNode
 */
public class RPNVariable extends RPNNode {

    /**
     * the name of the {@link TRSVariable}
     */
    private String varName;

    /**
     * creates a new {@link RPNVariable}
     * 
     * @param varName
     */
    public RPNVariable(String varName) {
	this.varName = varName;
    }

    /**
     * getter of {@link #varName}
     * 
     * @return the value of {@link #varName}
     */
    public String getValue() {
	return this.varName;
    }

    /**
     * setter for {@link #varName}
     * 
     * @param varName  the value to set {@link #varName} to
     */
    public void setValue(String varName) {
	this.varName = varName;
    }
}