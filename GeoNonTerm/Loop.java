package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import org.sat4j.core.VecInt;
import org.sat4j.specs.IVecInt;

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
     * 
     * <pre>
     * <code>
     *        G  0
     *  A = ( M -I )
     *       -M  I
     * </code>
     * </pre>
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
     * computed as
     * 
     * <pre>
     * <code>
     *        g
     *  b = (-m)
     *        m
     * </code>
     * </pre>
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
     * computes the {@link #iterationMatrix}A and {@link #iterationConstants} b
     * as <br>
     * 
     * <pre>
     * <code>
     *        G  0             g
     *  A = ( M -I ) and b = (-m)
     *       -M  I             m
     * </code>
     * </pre>
     */
    public void computeIterationMatrixAndConstants() {
	assert guardMatrix != null;
	assert guardConstants != null;
	assert updateMatrix != null;
	assert updateConstants != null;

	int n = guardMatrix.columnSize();
	int m = guardMatrix.rowSize();

	iterationConstants = new VecInt(0, 0);

	iterationConstants.pushAll(guardConstants);
	iterationConstants.pushAll(GeoNonTermAnalysis.negateVec(updateConstants));
	iterationConstants.pushAll(updateConstants);

	iterationMatrix = new UpdateMatrix(2 * n + m, 2 * n);

	// Setzen von G
	iterationMatrix.insert(guardMatrix, 0, 0);
	// Setzen von M
	iterationMatrix.insert(updateMatrix, m, 0);
	// Setzen von -M
	iterationMatrix.insert(UpdateMatrix.negateMatrix(updateMatrix), m + n, 0);
	// Setzen von -I
	iterationMatrix.insert(UpdateMatrix.negateMatrix(UpdateMatrix.IdentityMatrix(n)), m, n);
	// Setzen von I
	iterationMatrix.insert(UpdateMatrix.IdentityMatrix(n), m + n, n);

	if (SHOULD_PRINT) {
	    GeoNonTermAnalysis.LOG.writeln(iterationMatrix);
	    GeoNonTermAnalysis.LOG.writeln(iterationConstants);
	}
    }

    // print OR toString METHODS

    /**
     * returns the matrices in a simple-to-read way. It should be the typical
     * mathematical form.
     * 
     * @return the matrices in mathematical form
     */
    public static String getSystemAsString(UpdateMatrix matrix, String[] names, VecInt constant) {
	StringBuilder sb = new StringBuilder();

	for (int row = 0; row < matrix.getMatrix().length; row++) {

	    sb.append(Loop.getFrontString(row, matrix.getMatrix().length)).append("\t");

	    for (int column = 0; column < matrix.getMatrix()[0].length; column++) {
		sb.append(matrix.getEntry(row, column) + "\t");
	    }
	    // sb.append("]").append("\t");

	    sb.append(Loop.getBackString(row, matrix.getMatrix().length));

	    sb.append("\t");
	    if (row < names.length) {
		sb.append(Loop.getFrontString(row, names.length)).append("\t");
		sb.append(names[row]);
		// sb.append("x");
		sb.append("\t").append(Loop.getBackString(row, names.length));
	    } else {
		sb.append("\t").append("\t");
	    }
	    sb.append("\t").append("<=").append("\t");
	    sb.append(Loop.getFrontString(row, constant.size())).append("\t");
	    sb.append(constant.get(row)).append("\t");
	    sb.append(Loop.getBackString(row, constant.size())).append("\n");

	}

	return sb.toString();

    }

    /**
     * a helping function for
     * {@link #getSystemAsString(UpdateMatrix, String[], VecInt)}. It finds the
     * best fitting matrix opening symbol for a row with respect to the cap.
     * 
     * @param rowNumber
     *            the row within the matrix
     * @param cap
     *            the max. number of rows
     * @return a top left, mid oder bottom left matrix opening symbol
     */
    private static String getFrontString(int rowNumber, int cap) {
	if (rowNumber == 0)
	    return "┌";
	else if (rowNumber == cap - 1)
	    return "└";
	else
	    return "├";
    }

    /**
     * a helping function for
     * {@link #getSystemAsString(UpdateMatrix, String[], VecInt)}. It finds the
     * best fitting matrix closing symbol for a row with respect to the cap.
     * 
     * @param rowNumber
     *            the row within the matrix
     * @param cap
     *            the max. number of rows
     * @return a top right, mid oder bottom right matrix closing symbol
     */
    private static String getBackString(int rowNumber, int cap) {
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
