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
	 * a static boolean to determine if the information about the process should
	 * be printed using the {@link GeoNonTermAnalysis#LOG Logger}.
	 */
	private static boolean SHOULD_PRINT = false;

	/**
	 * the speed {@link Matrix} defining the speed of convergence of the
	 * geometric series
	 */
	private Matrix speedMatrix;

	/**
	 * the direction {@link Matrix} defining the direction of convergence of the
	 * geometric series
	 */
	private Matrix directionMatrix;

	/**
	 * a vector of just one's to convert the matrices into vectors <br>
	 * Note: the size can't be set on start, so it can't be static
	 */
	private final VecInt one;

	/**
	 * TODO
	 */
	public Loop() {
		if (SHOULD_PRINT) {
			GeoNonTermAnalysis.LOG.startClassOutput("LOOP");
		}

		one = new VecInt();

		if (SHOULD_PRINT) {
			GeoNonTermAnalysis.LOG.endClassOutput("LOOP");
		}
	}

	/**
	 * @return the directionMatrix
	 */
	public Matrix getDirectionMatrix() {
		return directionMatrix;
	}

	/**
	 * @param directionMatrix
	 *            the directionMatrix to set
	 */
	public void setDirectionMatrix(Matrix directionMatrix) {
		this.directionMatrix = directionMatrix;
	}

	/**
	 * @return the speedMatrix
	 */
	public Matrix getSpeedMatrix() {
		return speedMatrix;
	}

	/**
	 * @param speedMatrix
	 *            the speedMatrix to set
	 */
	public void setSpeedMatrix(Matrix speedMatrix) {
		this.speedMatrix = speedMatrix;
	}

}
