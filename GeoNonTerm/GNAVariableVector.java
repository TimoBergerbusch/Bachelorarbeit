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
	assert !this.isVar(index);

	return Integer.parseInt(values[index]);
    }

    public boolean isVar(int index) {
	try {
	    Integer.parseInt(values[index]);
	    return false;
	} catch (Exception e) {
	    return true;
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

	ArrayList<Pair<String, Integer>> list = new ArrayList<>();

	for (int i = 0; i < this.size(); i++) {
	    if (!isVar(i))
		value += this.getEntryAsInt(i);
	    else {
		// Logger.getLog().writeln("test:"+this.getEntry(i));
		// Logger.getLog().close();
		String[] split = this.getEntry(i).split("\\*");
		list.add(new Pair<String, Integer>(split[1], Integer.parseInt(split[0])));
	    }
	}

	return this.getTreeOfParts(value, list);
    }

    private RPNNode getTreeOfParts(int value, ArrayList<Pair<String, Integer>> list) {
	if (list.size() == 0)
	    return new RPNConstant(value);
	else {
	    Pair<String, Integer> pair = list.get(list.size() - 1);
	    list.remove(pair);
	    // Logger.getLog().writeln("Pair:" + pair.toString());
	    // erstellen eines Knoten der die Variable als Binäre Opertation
	    // auffasst und den Rest rekursiv abarbeitet
	    RPNFunctionSymbol root = new RPNFunctionSymbol(ArithmeticSymbol.PLUS, getTreeOfParts(value, list),
		    new RPNFunctionSymbol(ArithmeticSymbol.TIMES, new RPNConstant(pair.getValue()),
			    new RPNVariable(pair.getKey())));
	    return root;
	}
    }

}
