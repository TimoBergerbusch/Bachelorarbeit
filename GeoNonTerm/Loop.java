package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import org.sat4j.core.VecInt;

/**
 * This class is the representative of the LOOP-matrices used for the
 * {@link GeoNonTermAnalysis Geometric Non-Termination Analysis}.
 * 
 * @author Timo Bergerbusch
 *
 * @see GeoNonTermAnalysis
 * @see Logger
 */
public class Loop {

    /**
     * a static boolean to determine if the information about the process should
     * be printed using the {@link GeoNonTermAnalysis#LOG Logger}.
     */
    private static boolean SHOULD_PRINT = false;

    /**
     * the guards depending on the variables. <br>
     * often referred as G.
     */
    private UpdateMatrix guardMatrix;

    /**
     * the update in every step of the iteration only within the rule without
     * regarding the guards.<br>
     * often referred as M.
     */
    private UpdateMatrix updateMatrix;

    /**
     * the matrix which describes a complete iteration step. It is computed as
     * <code>A= 
     * <table>
     * <tr>
     * <td>G</td>
     * <td>0</td>
     * </tr>
     * <tr>
     * <td>M</td>
     * <td>-I</td>
     * </tr>
     * <tr>
     * <td>-M</td>
     * <td>I</td>
     * </tr>
     * </table>
     * </code>
     */
    private UpdateMatrix iterationMatrix;

    /**
     * the constants corresponding to the {@link #guardMatrix}
     */
    private VecInt guardConstants;

    /**
     * the constants corresponding to the {@link #updateMatrix}
     */
    private VecInt updateConstants;

    /**
     * the constants corresponding to the {@link #iterationMatrix}. It is
     * computed as <code>b= 
     * <table>
     * <tr>
     * <td>g</td>
     * </tr>
     * <tr>
     * <td>-m</td>
     * </tr>
     * <tr>
     * <td>m</td>
     * </tr>
     * </table>
     * </code>
     */
    private VecInt iterationConstants;

    /**
     * the default constructor. No other functionality so far.
     */
    public Loop() {
	// if (SHOULD_PRINT) {
	// GeoNonTermAnalysis.LOG.startClassOutput("LOOP");
	// }
	//
	// if (SHOULD_PRINT) {
	// GeoNonTermAnalysis.LOG.endClassOutput("LOOP");
	// }
    }

    /**
     * returns the matrices in a simple-to-read way. It should be the typical
     * mathematical form.
     * 
     * @return the matrices in mathematical form
     */
    public String getSystemAsString() {
	StringBuilder sb = new StringBuilder();

	sb.append("Guards:" + "\n");
	for (int row = 0; row < guardMatrix.getMatrix().length; row++) {

	    sb.append(this.getFrontString(row, guardMatrix.getMatrix().length)).append("\t");

	    for (int column = 0; column < guardMatrix.getMatrix()[0].length; column++) {
		sb.append(guardMatrix.getEntry(row, column) + "\t");
	    }
	    // sb.append("]").append("\t");

	    sb.append(this.getBackString(row, guardMatrix.getMatrix().length));

	    sb.append("\t");
	    if (row < guardMatrix.getVarNames().length) {
		sb.append(this.getFrontString(row, guardMatrix.getVarNames().length)).append("\t");
		sb.append(guardMatrix.getVarNames()[row]);
		// sb.append("x");
		sb.append("\t").append(this.getBackString(row, guardMatrix.getVarNames().length));
	    } else {
		sb.append("\t").append("\t");
	    }
	    sb.append("\t").append("<=").append("\t");
	    sb.append(this.getFrontString(row, guardConstants.size())).append("\t");
	    sb.append(guardConstants.get(row)).append("\t");
	    sb.append(this.getBackString(row, guardConstants.size())).append("\n");

	}

	return sb.toString();

    }

    /**
     * a helping function for {@link #getSystemAsString()}. It finds the best
     * fitting matrix opening symbol for a row with respect to the cap.
     * 
     * @param rowNumber
     *            the row within the matrix
     * @param cap
     *            the max. number of rows
     * @return a top left, mid oder bottom left matrix opening symbol
     */
    private String getFrontString(int rowNumber, int cap) {
	if (rowNumber == 0)
	    return "┌";
	else if (rowNumber == cap - 1)
	    return "└";
	else
	    return "├";
    }

    /**
     * a helping function for {@link #getSystemAsString()}. It finds the best
     * fitting matrix closing symbol for a row with respect to the cap.
     * 
     * @param rowNumber
     *            the row within the matrix
     * @param cap
     *            the max. number of rows
     * @return a top right, mid oder bottom right matrix closing symbol
     */
    private String getBackString(int rowNumber, int cap) {
	if (rowNumber == 0)
	    return "┐";
	else if (rowNumber == cap - 1)
	    return "┘";
	else
	    return "┤";
    }

    /**
     * prints the matrices and corresponding constants if they are initialized
     * as a simple string.
     */
    public void printInformationString() {
	StringBuilder sb = new StringBuilder();
	if (guardMatrix != null) {
	    sb.append("GuardUpdates: " + guardMatrix.toString());
	    sb.append("GuardConstants: " + guardConstants.toString());
	}
	if (updateMatrix != null) {
	    sb.append("updateMatrix: " + updateMatrix.toString());
	    sb.append("updateConstants: " + updateConstants.toString());
	}
	if (iterationMatrix != null) {
	    sb.append("iterationMatrix: " + iterationMatrix.toString());
	    sb.append("iterationConstants: " + iterationConstants.toString());
	}

	GeoNonTermAnalysis.LOG.startClassOutput("LOOP");
	GeoNonTermAnalysis.LOG.writeln(sb.toString());
	GeoNonTermAnalysis.LOG.endClassOutput("LOOP");
    }

    // GETTER UND SETTER

    /**
     * @return the guardUpdates
     */
    public UpdateMatrix getGuardUpdates() {
	return guardMatrix;
    }

    /**
     * @param guardUpdates
     *            the guardUpdates to set
     */
    public void setGuardUpdates(UpdateMatrix guardUpdates) {
	this.guardMatrix = guardUpdates;
    }

    /**
     * @return the updateMatrix
     */
    public UpdateMatrix getUpdateMatrix() {
	return updateMatrix;
    }

    /**
     * @param updateMatrix
     *            the updateMatrix to set
     */
    public void setUpdateMatrix(UpdateMatrix updateMatrix) {
	this.updateMatrix = updateMatrix;
    }

    /**
     * @return the iterationMatrix
     */
    public UpdateMatrix getIterationMatrix() {
	return iterationMatrix;
    }

    /**
     * @param iterationMatrix
     *            the iterationMatrix to set
     */
    public void setIterationMatrix(UpdateMatrix iterationMatrix) {
	this.iterationMatrix = iterationMatrix;
    }

    /**
     * @return the guardConstants
     */
    public VecInt getGuardConstants() {
	return guardConstants;
    }

    /**
     * @param guardConstants
     *            the guardConstants to set
     */
    public void setGuardConstants(VecInt guardConstants) {
	this.guardConstants = guardConstants;
    }

    /**
     * @return the updateConstants
     */
    public VecInt getUpdateConstants() {
	return updateConstants;
    }

    /**
     * @param updateConstants
     *            the updateConstants to set
     */
    public void setUpdateConstants(VecInt updateConstants) {
	this.updateConstants = updateConstants;
    }

    /**
     * @return the iterationConstants
     */
    public VecInt getIterationConstants() {
	return iterationConstants;
    }

    /**
     * @param iterationConstants
     *            the iterationConstants to set
     */
    public void setIterationConstants(VecInt iterationConstants) {
	this.iterationConstants = iterationConstants;
    }

}
