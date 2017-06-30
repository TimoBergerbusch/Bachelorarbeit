package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

/**
 * Represents a square <code>int</code>-matrix and a labeling to the rows and
 * columns. <br>
 * The i-th row has the same label as the i-th column. <br>
 * <br>
 * The application of it are the speed and direction matrices in the
 * {@link Loop} used within the {@link GeoNonTermAnalysis Geometric Non
 * Termination Analysis}.
 * 
 * @author Timo Bergerbusch
 *
 */
public class UpdateMatrix {

    /**
     * a two dimensional int array to represent the values as a row-major
     * matrix.<br>
     * <br>
     * Example: the matrix m =
     * <table>
     * <tr>
     * <td>a</td>
     * <td>b</td>
     * </tr>
     * <tr>
     * <td>c</td>
     * <td>d</td>
     * </tr>
     * </table>
     * would be <code>{{a,b},{c,d}}</code>
     */
    private int[][] matrix;

    /**
     * represents the names of the rows and columns. The first row has the same
     * name as the first column and so on.
     */
    private final String[] varNames;

    /**
     * creates a new {@link UpdateMatrix}. <br>
     * It sets the {@link #matrix} as a square matrix defined by the length of
     * the parameter array length and sets every entry to <code>0</code> by
     * <code>default</code>.
     * 
     * @param varNames
     *            the {@link #varNames names} of the variables
     */
    public UpdateMatrix(String[] varNames) {
	this.varNames = varNames;

	this.matrix = new int[varNames.length][varNames.length];
	for (int i = 0; i < varNames.length; i++)
	    for (int j = 0; j < varNames.length; j++)
		matrix[i][j] = 0;
    }

    /**
     * sets an entry in the {@link #matrix} using the exact
     * <code>int</code>-values to adress the entry and sets it to the parameter
     * <code>value</code>.
     * 
     * @param row
     *            the row
     * @param column
     *            the column
     * @param value
     *            the new value
     */
    public void setEntry(int row, int column, int value) {
	this.matrix[row][column] = value;
    }

    /**
     * sets an entry in the {@link #matrix} using the names of the row- and
     * column-variables stored in {@link #varNames} using
     * {@link #getIndex(String)} and {@link #setEntry(int, int, int)}
     * 
     * @param rowOf
     *            the name of the row variable
     * @param valueOf
     *            the name of the column variable
     * @param value
     *            the new value
     */
    public void setEntry(String rowOf, String valueOf, int value) {
	int row = getIndex(rowOf);
	int column = getIndex(valueOf);
	if (row >= 0 && column >= 0)
	    this.setEntry(row, column, value);
	else
	    GeoNonTermAnalysis.LOG.writeln("ERRROR: Invalid Matrix setEntry Attributes: " + rowOf + "= " + row + "\t"
		    + valueOf + "=" + column + "\t" + value);
    }

    /**
     * retrieves the index of an entry in {@link #varNames} to the given input.
     * 
     * @param key
     *            the name of the variables, which's index should be returned
     * @return the index of the key within the {@link #varNames} array. Returns
     *         <code>null</code> if the value can't be found
     */
    private int getIndex(String key) {
	for (int i = 0; i < varNames.length; i++)
	    if (varNames[i].equals(key))
		return i;

	return -1;
    }

    /**
     * getter for the {@link #matrix}
     * 
     * @return the matrix
     */
    public int[][] getMatrix() {
	return matrix;
    }

    /**
     * getter for the {@link #varNames}
     * 
     * @return the varNames
     */
    public String[] getVarNames() {
	return varNames;
    }

    /**
     * returns the {@link #matrix} with it's entries and the {@link #varNames
     * labeling of the rows and columns} <br>
     * <br>
     * Example to the {@link #matrix } <code>m = {{a,b},{c,d}}</code> and the
     * {@link #varNames labels} <code>varName= {"x", "y"}</code> <br>
     * <br>
     * <table>
     * <tr>
     * <td></td>
     * <td>x</td>
     * <td>y</td>
     * </tr>
     * <tr>
     * <td>x</td>
     * <td>a</td>
     * <td>b</td>
     * </tr>
     * <tr>
     * <td>y</td>
     * <td>c</td>
     * <td>d</td>
     * </tr>
     * </table>
     * 
     * @return the {@link #matrix} in the presented form
     */
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("\t");
	for (String s : varNames)
	    sb.append(s + "\t");
	sb.append("\n");
	for (int i = 0; i < matrix.length; i++) {
	    sb.append(varNames[i] + "\t");
	    for (int j = 0; j < matrix[0].length; j++) {
		sb.append(matrix[i][j] + "\t");
	    }
	    sb.append("\n");
	}
	return sb.toString();
    }
}