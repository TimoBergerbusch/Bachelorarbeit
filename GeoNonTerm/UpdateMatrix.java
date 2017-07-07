package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

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
     * creates a new square Identity-matrix, which is an {@link UpdateMatrix}
     * with 1's in the diagonal and 0 else.
     * 
     * @param size
     *            the dimension if the Identity-matrix
     * @return the Identity-matrix
     */
    public static UpdateMatrix IdentityMatrix(int size) {
	UpdateMatrix I = new UpdateMatrix(size, size);

	for (int i = 0; i < size; i++) {
	    I.setEntry(i, i, 1);
	}

	return I;
    }

    /**
     * a static method to negate an {@link UpdateMatrix}
     * 
     * @param m
     *            the {@link UpdateMatrix} that should be negated
     * @return the negated {@link UpdateMatrix}
     */
    public static UpdateMatrix negateMatrix(UpdateMatrix m) {
	for (int i = 0; i < m.rowSize(); i++)
	    for (int j = 0; j < m.columnSize(); j++)
		m.setEntry(i, j, m.getEntry(i, j) * -1);
	return m;
    }

    /**
     * a two dimensional int array to represent the values as a row-major
     * matrix.<br>
     * <br>
     * Example: the matrix
     * 
     * <pre>
     * <code>
     *        a b
     *  m = ( c d )
     * </code>
     * </pre>
     * 
     * would be <code>{{a,b},{c,d}}</code>
     */
    private int[][] matrix;

    /**
     * represents the names of the columns. Typically these are the variable
     * names
     */
    private final String[] columnNames;

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
     *            the {@link #columnNames names} of the variables
     */
    public UpdateMatrix(String[] varNames) {
	this.columnNames = varNames;
	this.rowNames = varNames;

	this.matrix = new int[varNames.length][varNames.length];
	for (int i = 0; i < varNames.length; i++)
	    for (int j = 0; j < varNames.length; j++)
		matrix[i][j] = 0;
    }

    /**
     * creates a new {@link UpdateMatrix}. <br>
     * It sets the {@link #matrix} to the given dimensions, sets the
     * {@link #columnNames} to the given parameter and initializes the
     * {@link #rowNames} as a typical enumeration.
     * 
     * @param rowDimension
     *            the row dimension
     * @param columnDimension
     *            the column dimension
     * @param columnNames
     *            the names of the columns
     */
    public UpdateMatrix(int rowDimension, int columnDimension, String[] columnNames) {
	assert columnDimension == columnNames.length;
	this.columnNames = columnNames;
	this.matrix = new int[rowDimension][columnDimension];

	this.rowNames = new String[rowDimension];
	for (int i = 0; i < rowDimension; i++)
	    this.rowNames[i] = i + "";
    }

    /**
     * creates a new {@link UpdateMatrix}. <br>
     * It sets the {@link #matrix} to the given dimensions and initializes the
     * {@link #rowNames} and {@link #columnNames} to the corresponding numbers
     * of the rows/columns.
     * 
     * @param rowDimension
     *            the row dimension
     * @param columnDimension
     *            the column dimension
     */
    public UpdateMatrix(int rowDimension, int columnDimension) {
	this.matrix = new int[rowDimension][columnDimension];

	this.columnNames = new String[columnDimension];
	this.rowNames = new String[rowDimension];
	for (int i = 0; i < rowDimension; i++) {
	    this.rowNames[i] = i + "";
	}
	for (int i = 0; i < columnDimension; i++) {
	    this.columnNames[i] = i + "";
	}
    }

    /**
     * inserts a {@link UpdateMatrix} at a given starting point into this
     * instances #{@link UpdateMatrix}.
     * 
     * @param src
     *            the {@link UpdateMatrix} that should be inserted
     * @param rowPos
     *            the row the insertion should start
     * @param columnPos
     *            the column the insertion should start
     */
    public void insert(UpdateMatrix src, int rowPos, int columnPos) {

	for (int row = rowPos; row < rowPos + src.rowSize(); row++) {
	    for (int column = columnPos; column < columnPos + src.columnSize(); column++) {
		this.matrix[row][column] = src.getEntry(row - rowPos, column - columnPos);
	    }
	}
    }

    /**
     * returns the {@link #matrix} with it's entries and the {@link #columnNames
     * labeling of the rows and columns} <br>
     * <br>
     * Example to the {@link #matrix } <code>m = {{a,b},{c,d}}</code> and the
     * {@link #columnNames labels} <code>varName= {"x", "y"}</code> <br>
     * <br>
     * 
     * <pre>
     * <code>
     *   x y
     * x a b
     * y c d
     * </code>
     * </pre>
     * 
     * @return the {@link #matrix} in the presented form
     */
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("\t");
	for (String s : columnNames)
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
     * column-variables stored in {@link #columnNames} using
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
	for (int i = 0; i < columnNames.length; i++) {
	    if (columnNames[i].equals(varName))
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
     * retrieves the index of an entry in {@link #columnNames} to the given
     * input.
     * 
     * @param key
     *            the name of the variables, which's index should be returned
     * @return the index of the key within the {@link #columnNames} array.
     *         Returns <code>null</code> if the value can't be found
     */
    private int getIndex(String key) {
	for (int i = 0; i < columnNames.length; i++)
	    if (columnNames[i].equals(key))
		return i;

	return -1;
    }

	// GETTER AND SETTER
	
    /**
     * getter for the {@link #matrix}
     * 
     * @return the matrix
     */
    public int[][] getMatrix() {
	return matrix;
    }

    /**
     * getter for the {@link #columnNames}
     * 
     * @return the varNames
     */
    public String[] getVarNames() {
	return columnNames;
    }

    /**
     * returns the number of rows of the matrix.
     * 
     * @return <code>matrix.length</code>
     */
    public int rowSize() {
	assert matrix != null;
	return matrix.length;
    }

    /**
     * returns the number of columns of the matrix
     * 
     * @return <code>matrix[0].length</code>
     */
    public int columnSize() {
	assert this.rowSize() >= 0;
	return matrix[0].length;
    }

}
