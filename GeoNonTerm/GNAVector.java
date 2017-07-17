package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import org.sat4j.core.VecInt;

/**
 * the {@link GNAVector} is an extension of the {@link VecInt}. It's used within
 * the {@link GeoNonTermAnalysis} to represent vectors only containing
 * <code>int</code>-values. The additional functions are mostly arithmetical
 * functions like {@link #add(GNAVector)}, {@link #mult(int)} or
 * {@link #negatedCopy()}.
 * 
 * @author Timo Bergerbusch
 * @see VecInt
 */
public class GNAVector extends VecInt {

    /**
     * a generated serial number
     */
    private static final long serialVersionUID = -1926438681693818091L;

    /**
     * creates a new instance without any conditions
     * 
     * @see VecInt#VecInt()
     */
    public GNAVector() {
	super();
    }

    /**
     * creates a new instance with values that the vector should contain. The
     * size of the vector will be set accordingly and the order will be kept.
     * 
     * @param values
     *            the values the vector should obtain
     */
    public GNAVector(int[] values) {
	super(values);
    }

    /**
     * creates a new instance with the desired size and a default value all the
     * elements are set to, so that the vector it self does not get any
     * <code>null</code>-pointer exceptions.
     * 
     * @param size
     * @param defaultValue
     */
    public GNAVector(int size, int defaultValue) {
	super(size, defaultValue);
    }

    /**
     * Adds two {@link GNAVector GNAVector's} as a normal vector addition. The
     * result will <u>not</u> be stored in this instance, but in an instance
     * returned as the result.
     * 
     * @param vec
     *            the {@link GNAVector} that should be added.
     * @return the {@link GNAVector} containing the result
     */
    public GNAVector add(GNAVector vec) {
	assert size() == vec.size();

	GNAVector res = new GNAVector(size(), 0);
	for (int i = 0; i < size(); i++) {
	    res.set(i, get(i) + vec.get(i));
	}

	return res;
    }

    /**
     * multiplying an {@link GNAVector} with a constant term, which is
     * equivalent to multiplying every entry with the constant term. The result
     * will <u>not</u> be stored in this instance, but in an instance returned
     * as the result.
     * 
     * @param cons
     *            the constant term
     * @return the result of the multiplication
     */
    public GNAVector mult(int cons) {
	GNAVector res = new GNAVector(size(), 0);
	for (int i = 0; i < size(); i++) {
	    res.set(i, get(i) * cons);
	}
	return res;
    }

    /**
     * returns this {@link GNAVector} multiplied by -1 using {@link #mult(int)}.
     * 
     * @return this {@link GNAVector} with negated coefficient
     */
    public GNAVector negatedCopy() {
	return this.mult(-1);
    }

    /**
     * checks weather this {@link GNAVector} is less or equal to a given
     * {@link GNAVector}. It does so by checking for every entry <code>i</code>
     * if {@link #get(int) this.get(i)} <= {@link #get(int) right.get(i)}
     * 
     * @param right
     *            the vector that should be tested weather this is less or equal
     *            to.
     * @return a <code>boolean</code> indicating weather this is less or equal
     *         than the given {@link GNAVector}
     */
    public boolean lessOrEqualThan(GNAVector right) {
	assert size() == right.size();

	for (int i = 0; i < size(); i++)
	    if (!(get(i) <= right.get(i)))
		return false;

	return true;
    }

    /**
     * Creates a <code>String</code>-array, where every item of the
     * {@link GNAVector} is represented as a <code>String</code> keeping the
     * order.
     * 
     * @return the {@link GNAVector} as a <code>String</code>-array
     */
    public String[] toStringArray() {
	String[] items = new String[size()];
	for (int i = 0; i < items.length; i++) {
	    items[i] = get(i) + "";
	}
	return items;
    }
}
