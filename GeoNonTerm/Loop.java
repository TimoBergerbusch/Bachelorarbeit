package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import org.sat4j.core.VecInt;

import aprove.Framework.Algebra.Matrices.Matrix;

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
     * a static boolean to determine if the information about the process should be
     * printed using the {@link GeoNonTermAnalysis#LOG Logger}.
     */
    private static boolean SHOULD_PRINT = false;

    private UpdateMatrix guardMatrix;

    private UpdateMatrix updateMatrix;

    private UpdateMatrix iterationMatrix;

    private VecInt guardConstants;

    private VecInt updateConstants;

    private VecInt iterationConstants;

    public Loop() {
	// if (SHOULD_PRINT) {
	// GeoNonTermAnalysis.LOG.startClassOutput("LOOP");
	// }
	//
	// if (SHOULD_PRINT) {
	// GeoNonTermAnalysis.LOG.endClassOutput("LOOP");
	// }
    }

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

    private String getFrontString(int rowNumber, int cap) {
	if (rowNumber == 0)
	    return "┌";
	else if (rowNumber == cap - 1)
	    return "└";
	else
	    return "├";
    }

    private String getBackString(int rowNumber, int cap) {
	if (rowNumber == 0)
	    return "┐";
	else if (rowNumber == cap - 1)
	    return "┘";
	else
	    return "┤";
    }

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
     * @return the sHOULD_PRINT
     */
    public static boolean isSHOULD_PRINT() {
	return SHOULD_PRINT;
    }

    /**
     * @param sHOULD_PRINT
     *            the sHOULD_PRINT to set
     */
    public static void setSHOULD_PRINT(boolean sHOULD_PRINT) {
	SHOULD_PRINT = sHOULD_PRINT;
    }

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
