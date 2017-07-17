package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import java.util.ArrayList;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNNode;

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
public class GNAMatrix {

    /**
     * creates a new square Identity-matrix, which is an {@link GNAMatrix} with
     * 1's in the diagonal and 0 else.
     * 
     * @param size
     *            the dimension if the Identity-matrix
     * @return the Identity-matrix
     */
    public static GNAMatrix IdentityMatrix(int size) {
	GNAMatrix I = new GNAMatrix(size, size);

	for (int i = 0; i < size; i++) {
	    I.setEntry(i, i, 1);
	}

	return I;
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
     * creates a new {@link GNAMatrix}. <br>
     * It sets the {@link #matrix} as a square matrix defined by the length of
     * the parameter array length and sets every entry to <code>0</code> by
     * <code>default</code>.
     * 
     * @param varNames
     *            the {@link #columnNames names} of the variables
     */
    public GNAMatrix(String[] varNames) {
	this.columnNames = varNames;
	this.rowNames = varNames;

	this.matrix = new int[varNames.length][varNames.length];
	for (int i = 0; i < varNames.length; i++)
	    for (int j = 0; j < varNames.length; j++)
		matrix[i][j] = 0;
    }

    /**
     * creates a new {@link GNAMatrix}. <br>
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
    public GNAMatrix(int rowDimension, int columnDimension, String[] columnNames) {
	assert columnDimension == columnNames.length;
	this.columnNames = columnNames;
	this.matrix = new int[rowDimension][columnDimension];

	this.rowNames = new String[rowDimension];
	for (int i = 0; i < rowDimension; i++)
	    this.rowNames[i] = i + "";
    }

    /**
     * creates a new {@link GNAMatrix}. <br>
     * It sets the {@link #matrix} to the given dimensions and initializes the
     * {@link #rowNames} and {@link #columnNames} to the corresponding numbers
     * of the rows/columns.
     * 
     * @param rowDimension
     *            the row dimension
     * @param columnDimension
     *            the column dimension
     */
    public GNAMatrix(int rowDimension, int columnDimension) {
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
     * returns a matrix like this with every entry multiplied by -1. <br>
     * NOTE: this method does <u>not</u> change this matrix's entry's!
     * 
     * @return a negated copy of this matrix
     * 
     * @see #negate()
     */
    public GNAMatrix negatedMatrixCopy() {
	GNAMatrix res = new GNAMatrix(this.rowSize(), this.columnSize());
	for (int i = 0; i < res.rowSize(); i++)
	    for (int j = 0; j < res.columnSize(); j++)
		res.setEntry(i, j, this.getEntry(i, j) * -1);
	return res;
    }

    /**
     * negates every {@link #matrix} entry with -1. <br>
     * NOTE: this method changes this {@link #matrix}'s entry's. <br>
     * NOTE: the return <u>is</u> needed
     * 
     * @return this matrix after it gets negated
     */
    public GNAMatrix negate() {
	this.matrix = this.negatedMatrixCopy().matrix;
	return this;
    }

    /**
     * multiplies this {@link GNAMatrix} with a given {@link GNAVector}
     * 
     * @param vec
     *            the {@link GNAVector} this {@link GNAMatrix} should be
     *            multiplicated with
     * @return the resulting {@link GNAVector}
     */
    public GNAVector mult(GNAVector vec) {
	assert this.columnSize() == vec.size();

	GNAVector res = new GNAVector(this.rowSize(), 0);
	int entry;
	for (int i = 0; i < this.rowSize(); i++) {
	    entry = 0;
	    for (int j = 0; j < this.columnSize(); j++) {
		entry += this.getEntry(i, j) * vec.get(j);
	    }
	    res.set(i, entry);
	}

	return res;
    }

    /**
     * multiplies this {@link GNAMatrix} with a {@link GNAVariableVector} and
     * generates a {@link RPNNode RPNTree} for every entry of the resulting
     * {@link GNAVariableVector} of the multiplication. It does so by
     * multiplying integer coefficients and rewrite formulas like <br>
     * 
     * <pre>
     * <code>
     *  c * (x_0 + x_1) &lt; = &gt; c*x_0 + c*x_1
     * </code>
     * </pre>
     * 
     * 
     * using {@link GNAVariableVector#getTreeOfVector()}.
     * 
     * @param vec
     *            the {@link GNAVariableVector}
     * @return the resulting {@link GNAVariableVector} as {@link RPNNode
     *         RPNTree} for every for every entry
     * 
     * @see GNAVariableVector#getTreeOfVector()
     * @see RPNNode
     */
    public RPNNode[] mult(GNAVariableVector vec) {
	assert this.columnSize() == vec.size();

	RPNNode[] nodes = new RPNNode[this.rowSize()];

	GNAVariableVector tmp; // = new GNAVariableVector(vec.size());
	ArrayList<String> list = new ArrayList<>();

	for (int i = 0; i < this.rowSize(); i++) {
	    for (int j = 0; j < vec.size(); j++) {
		if (vec.isInt(j))
		    list.add((this.matrix[i][j] * vec.getEntryAsInt(j)) + "");
		else if (vec.getEntry(j).contains("+")) {
		    for (String s : vec.getEntry(j).split("\\+"))
			list.add(this.matrix[i][j] + "*" + s);
		} else
		    list.add(this.matrix[i][j] + "*" + vec.getEntry(j));
	    }
	    tmp = new GNAVariableVector(list.toArray(new String[] {}));
	    list.clear();
	    nodes[i] = tmp.getTreeOfVector();
	}
	return nodes;
    }

    /**
     * inserts a {@link GNAMatrix} at a given starting point into this instances
     * #{@link GNAMatrix}.
     * 
     * @param src
     *            the {@link GNAMatrix} that should be inserted
     * @param rowPos
     *            the row the insertion should start
     * @param columnPos
     *            the column the insertion should start
     */
    public void insert(GNAMatrix src, int rowPos, int columnPos) {

	for (int row = rowPos; row < rowPos + src.rowSize(); row++) {
	    for (int column = columnPos; column < columnPos + src.columnSize(); column++) {
		this.matrix[row][column] = src.getEntry(row - rowPos, column - columnPos);
	    }
	}
    }

    public int[] computeEigenvalues() {
	EigenDecomposition ed = new EigenDecomposition(this.getAsRealMatrix());
	double[] values = ed.getRealEigenvalues();
	int[] normValues = new int[values.length];
	for (int i = 0; i < values.length; i++)
	    normValues[i] = (int) Math.abs((values[i]));
	return normValues;
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

    public RealMatrix getAsRealMatrix() {
	double[][] doubleMatrix = new double[this.rowSize()][this.columnSize()];
	for (int i = 0; i < this.rowSize(); i++)
	    for (int j = 0; j < this.columnSize(); j++)
		doubleMatrix[i][j] = matrix[i][j];
	return new BlockRealMatrix(doubleMatrix);
    }

    // GETTER AND SETTER

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
	    Logger.getLog().writeln("ERRROR: Invalid Matrix setEntry Attributes: " + rowOf + "= " + row + "\t" + valueOf
		    + "=" + column + "\t" + value);
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
