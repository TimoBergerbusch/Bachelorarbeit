package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.ArithmeticSymbol;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNConstant;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNFunctionSymbol;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNNode;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNVariable;
import aprove.Framework.Utility.GenericStructures.Pair;
import aprove.InputModules.Programs.diologic.ParseException;

public class GNAVariableVector {

    private String[] values;

    public GNAVariableVector(int size) {
	this.values = new String[size];
    }

    public GNAVariableVector(String[] values) {
	this.values = values;
    }

    public void setEntry(int index, String value) {
	this.values[index] = value;
    }

    public void setEntry(int index, int value) {
	this.setEntry(index, value + "");
    }

    public String getEntry(int index) {
	return this.values[index];
    }

    public int getEntryAsInt(int index) {
	assert this.isInt(index);

	return Integer.parseInt(values[index]);
    }

    public boolean isInt(int index) {
	try {
	    Integer.parseInt(values[index]);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }

    public boolean containsVars() {
	for (String s : values)
	    try {
		Integer.parseInt(s);
	    } catch (Exception e) {
		return true;
	    }

	return false;
    }

    public GNAVector transformToGNAVector() {
	assert this.containsVars() == false;

	GNAVector vec = new GNAVector(values.length, 0);

	for (int i = 0; i < vec.size(); i++) {
	    vec.set(i, Integer.parseInt(values[i]));
	}

	return vec;
    }

    public int size() {
	return values.length;
    }

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

	// Logger.getLog().writeln("Size: " + nodeList.size());

	// Logger.getLog().writeln(this.createRecursiveAdd(nodeList).toInfixString());
	return this.createRecursiveAdd(nodeList);
    }

    private RPNNode createRecursiveAdd(ArrayList<RPNNode> nodes) {
	if (nodes.size() == 1)
	    return nodes.get(0);
	else {
	    RPNNode node = nodes.get(0);
	    nodes.remove(node);
	    return new RPNFunctionSymbol(ArithmeticSymbol.PLUS, node, this.createRecursiveAdd(nodes));
	}
    }

    private RPNNode createRecursiveMult(ArrayList<String> varNames) {
	if (varNames.size() == 1)
	    return new RPNVariable(varNames.get(0));
	else {
	    String s = varNames.get(0);
	    varNames.remove(s);
	    return new RPNFunctionSymbol(ArithmeticSymbol.TIMES, new RPNVariable(s),
		    this.createRecursiveMult(varNames));
	}
    }

    public boolean isInt(String s) {
	try {
	    Integer.parseInt(s);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }
}
