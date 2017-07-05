package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import aprove.Framework.Algebra.Matrices.Matrix;

/**
 * Represents a <code>int</code>-matrix and a labeling to the rows and columns.
 * <br>
 * <br>
 * The application of it are the update-, guard- and iteration matrices in the
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
     * represents the names of the columns. Typically these are the variable
     * names
     */
    private final String[] varNames;

    /**
     * represents the names of the rows. Depending if it is a square matrix is
     * could be again the variable names or simple an enumeration.
     */
    private final String[] rowNames;

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
	this.rowNames = varNames;

	this.matrix = new int[varNames.length][varNames.length];
	for (int i = 0; i < varNames.length; i++)
	    for (int j = 0; j < varNames.length; j++)
		matrix[i][j] = 0;
    }

    /**
     * creates a new {@link UpdateMatrix}. <br>
     * It sets the {@link Matrix} to the given dimensions, sets the
     * {@link #varNames} to the given parameter and initializes the
     * {@link #rowNames} as a typical enumeration.
     * 
     * @param rowDimension
     * @param columnDimension
     * @param varNames
     */
    public UpdateMatrix(int rowDimension, int columnDimension, String[] varNames) {
	assert columnDimension == varNames.length;
	this.varNames = varNames;
	this.matrix = new int[rowDimension][columnDimension];

	this.rowNames = new String[rowDimension];
	for (int i = 0; i < rowDimension; i++)
	    this.rowNames[i] = i + "";
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
     * sets an entry in the {@link #matrix} using the given row number and
     * variable name using {@link #getIndex(String)} and
     * {@link #setEntry(int, int, int)}. <br>
     * This method is typically used for non-square matrices.
     * 
     * @param rowNumber
     *            the row of the entry
     * @param varName
     *            the corresponding variable
     * @param value
     *            the new value of the entry
     */
    public void setEntry(int rowNumber, String varName, int value) {
	int columnIndex = -1;
	for (int i = 0; i < varNames.length; i++) {
	    if (varNames[i].equals(varName))
		columnIndex = i;
	}

	if (columnIndex == -1)
	    assert false;
	else
	    this.setEntry(rowNumber, columnIndex, value);
    }

    /**
     * returns the entry at a requested position.
     * 
     * @param row
     *            the row of the entry
     * @param column
     *            the column of the entry
     * @return the value of {@link #getMatrix()} at
     *         <code>matrix[row][column]</code>
     */
    public int getEntry(int row, int column) {
	return this.matrix[row][column];
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
	    sb.append(rowNames[i] + "\t");
	    for (int j = 0; j < matrix[0].length; j++) {
		sb.append(matrix[i][j] + "\t");
	    }
	    sb.append("\n");
	}
	return sb.toString();
    }

    /**
     * returns the size of rows of the matrix.
     * 
     * @return <code>matrix.length</code>
     */
    public int size() {
	return matrix.length;
    }

    /**
     * multiplies the {@link #matrix} with -1.
     */
    public void negateMatrix() {
	for (int i = 0; i < matrix.length; i++)
	    for (int j = 0; j < matrix[0].length; j++)
		matrix[i][j] *= -1;
    }
}
