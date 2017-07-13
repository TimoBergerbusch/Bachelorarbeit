package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import org.sat4j.core.VecInt;

public class GNAVector extends VecInt {

    /**
     * a generated serial number
     */
    private static final long serialVersionUID = -1926438681693818091L;

    public GNAVector(int size, int defaultValue) {
	super(size, defaultValue);
    }

    public GNAVector(int[] array) {
	super(array);
    }

    public GNAVector() {
	super();
    }

    public GNAVector add(GNAVector vec) {
	assert size() == vec.size();

	GNAVector res = new GNAVector(size(), 0);
	for (int i = 0; i < size(); i++) {
	    res.set(i, get(i) + vec.get(i));
	}

	return res;
    }

    public GNAVector mult(int cons) {
	GNAVector res = new GNAVector(size(), 0);
	for (int i = 0; i < size(); i++) {
	    res.set(i, get(i) * cons);
	}
	return res;
    }

    public GNAVector negatedCopy() {
	GNAVector res = new GNAVector(size(), 0);
	for (int i = 0; i < size(); i++) {
	    res.set(i, get(i) * -1);
	}
	return res;
    }

    public boolean lessOrEqualThan(GNAVector right) {
	assert size() == right.size();

	for (int i = 0; i < size(); i++)
	    if (!(get(i) <= right.get(i)))
		return false;

	return true;
    }

    public String[] toStringArray() {
	String[] items = new String[size()];
	for (int i = 0; i < items.length; i++) {
	    items[i] = get(i) + "";
	}
	return items;
    }
}
