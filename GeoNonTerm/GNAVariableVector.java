package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import java.util.ArrayList;

import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.ArithmeticSymbol;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNConstant;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNFunctionSymbol;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNNode;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNVariable;

/**
 * This Class defines a vector similar to the {@link GNAVector}, with the
 * difference, that this vector can contain variables within the vector. Since
 * this is not possible to store within an <code>int</code>-array thsi class can
 * not be an extension of the {@link GNAVector}-class. <br>
 * It contains a subset of methods of {@link GNAVector}. <br>
 * It is used within the {@link GeoNonTermAnalysis} to represent vectors
 * containing variables for the SMT-Solver
 * 
 * @author Timo Bergerbusch
 * 
 * @see GeoNonTermAnalysis
 * @see SMTFactory
 */
public class GNAVariableVector {

    /**
     * this <code>String</code>-array contains the elements of the
     * {@link GNAVariableVector}. If a row does not contain a variable it's
     * still stored as an <code>String</code>.
     */
    private String[] values;

    /**
     * creates a new instance with the desired size. NOTE: no
     * <code>default</code>-value is set.
     * 
     * @param size
     *            the size of the {@link GNAVariableVector}
     */
    public GNAVariableVector(int size) {
	this.values = new String[size];
    }

    /**
     * creates a new instance with values that the vector should contain. The
     * size of the vector will be set accordingly and the order will be kept.
     * 
     * @param values
     *            the values the vector should obtain
     */
    public GNAVariableVector(String[] values) {
	this.values = values;
    }

    /**
     * sets an entry at a given index to a given value.
     * 
     * @param index
     *            the index to be set
     * @param value
     *            the value to set
     */
    public void setEntry(int index, String value) {
	this.values[index] = value;
    }

    /**
     * sets an entry at a given index to a given value using
     * {@link #setEntry(int, String)}
     * 
     * @param index
     *            the index to be set
     * @param value
     *            the value to set, which get's converted to a
     *            <code>String</code>
     */
    public void setEntry(int index, int value) {
	this.setEntry(index, value + "");
    }

    /**
     * returns an entry within the {@link GNAVariableVector} at a given index
     * 
     * @param index
     *            the index of the position
     * @return the value at the index
     */
    public String getEntry(int index) {
	return this.values[index];
    }

    /**
     * returns an entry within the {@link GNAVariableVector} at a given index as
     * an <code>int</code>-value. <br>
     * NOTE: if the entry is no <code>int</code> an assertion error will occur
     * 
     * @param index
     *            the index of the position
     * @return the <code>int</code>-value at that position
     */
    public int getEntryAsInt(int index) {
	assert this.isInt(index);

	return Integer.parseInt(values[index]);
    }

    /**
     * checks weather an entry at a given position is castable into an integer
     * 
     * @param s
     *            the index of the position to test
     * @return a boolean indicating if it's castable
     */
    private boolean isInt(String s) {
	try {
	    Integer.parseInt(s);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }

    /**
     * checks weather an entry at a given position is castable into an integer
     * using {@link #isInt(String)}
     * 
     * @param index
     *            the index of the position to test
     * @return a boolean indicating if it's castable
     */
    public boolean isInt(int index) {
	return this.isInt(values[index]);
    }

    /**
     * returns the number of indices of the {@link GNAVariableVector}
     * 
     * @return the size
     */
    public int size() {
	return values.length;
    }

    /**
     * creates a <code>String</code> representing the {@link GNAVariableVector}
     * as [ x_0, ... ,x_n], where x_i is entry i and n is the result of
     * {@link #size()}.
     * 
     * @return the {@link GNAVariableVector} as String
     */
    public String toString() {
	StringBuilder sb = new StringBuilder();

	sb.append("[ ");
	for (String s : values) {
	    sb.append(s);
	    if (!s.equals(values[values.length - 1]))
		sb.append(", ");
	}

	sb.append("]");
	return sb.toString();
    }

    /**
     * this method analyzes the entry's, creates a {@link RPNNode RPNTree} of
     * every entry sometimes using {@link #createRecursiveMult(ArrayList)} and
     * multiplying integer coefficients and finally uses
     * {@link #createRecursiveAdd(ArrayList)} to add up all the entry's.
     * 
     * @return a {@link RPNNode} (a {@link RPNFunctionSymbol}) representing the
     *         sum of the entry's as a {@link RPNNode RPNTree}
     */
    public RPNNode getTreeOfVector() {
	int value = 0;

	ArrayList<RPNNode> nodeList = new ArrayList<>();

	for (int i = 0; i < this.size(); i++) {
	    if (this.isInt(i))
		value += this.getEntryAsInt(i);
	    else if (this.getEntry(i).contains("*")) {
		int factor = 1;

		ArrayList<String> varNames = new ArrayList<>();
		for (String s : this.getEntry(i).split("\\*")) {
		    if (this.isInt(s))
			factor *= Integer.parseInt(s);
		    else
			varNames.add(s);
		}

		if (varNames.size() != 0 && factor != 0)
		    nodeList.add(new RPNFunctionSymbol(ArithmeticSymbol.TIMES, new RPNConstant(factor),
			    this.createRecursiveMult(varNames)));
		else if (factor != 0)
		    nodeList.add(new RPNConstant(factor));

	    }
	}
	nodeList.add(new RPNConstant(value));
	return this.createRecursiveAdd(nodeList);
    }

    /**
     * creates a {@link RPNNode PRNTree} adding up all the {@link RPNNode
     * RPNNode's} given by adding recursively. an empty array will return 0.
     * 
     * @param nodes
     *            the notes that should be added
     * @return one {@link RPNNode RPNTree} containing the sum of the array-nodes
     */
    private RPNNode createRecursiveAdd(ArrayList<RPNNode> nodes) {
	if (nodes.size() < 1)
	    return new RPNConstant(0);
	else if (nodes.size() == 1)
	    return nodes.get(0);
	else {
	    RPNNode node = nodes.get(0);
	    nodes.remove(node);
	    return new RPNFunctionSymbol(ArithmeticSymbol.PLUS, node, this.createRecursiveAdd(nodes));
	}
    }

    /**
     * creates a {@link RPNNode PRNTree} multiplying all the {@link RPNNode
     * RPNNode's} given by adding recursively. an empty array will return 0.
     * 
     * @param nodes
     *            the notes that should be added
     * @return one {@link RPNNode RPNTree} containing the sum of the array-nodes
     */
    private RPNNode createRecursiveMult(ArrayList<String> nodes) {
	if (nodes.size() < 1)
	    return new RPNConstant(0);
	else if (nodes.size() == 1)
	    return new RPNVariable(nodes.get(0));
	else {
	    String s = nodes.get(0);
	    nodes.remove(s);
	    return new RPNFunctionSymbol(ArithmeticSymbol.TIMES, new RPNVariable(s),
		    this.createRecursiveMult(nodes));
	}
    }

}
