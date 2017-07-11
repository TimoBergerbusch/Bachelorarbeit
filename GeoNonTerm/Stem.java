package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import org.sat4j.core.VecInt;

import aprove.Framework.BasicStructures.FunctionSymbol;

/**
 * This class is the representative of the STEM-vector used for the
 * {@link GeoNonTermAnalysis Geometric Non-Termination Analysis}.
 * 
 * @author Timo Bergerbusch
 *
 * @see GeoNonTermAnalysis
 * @see Logger
 */
public class Stem {

    /**
     * a static boolean to determine if the information about the process should
     * be printed using the {@link GeoNonTermAnalysis#LOG Logger}.
     */
    private static boolean SHOULD_PRINT = false;

    /**
     * the STEM vector to calculate for example the geometric series
     */
    private VecInt stemVec;

    /**
     * the {@link FunctionSymbol} that is the start symbol <br>
     * Example: f_99
     */
    private FunctionSymbol startFunctionSymbol;

    /**
     * initializing a new STEM by taking an array of {@link FunctionSymbol} and
     * extract the preset/STEM values. <br>
     * The first element is the {@link #startFunctionSymbol} and the following
     * are the values. <br>
     * <br>
     * Example: [f_99,3,10] -> {@link #startFunctionSymbol}=f_99 and
     * {@link #stemVec}={3,10}
     * 
     * @param array
     *            a {@link FunctionSymbol} array
     */
    public Stem(FunctionSymbol[] array) {
	if(array==null)
	    return;
	startFunctionSymbol = array[0];
	if (SHOULD_PRINT) {
	    GeoNonTermAnalysis.LOG.writeln("########################");
	    GeoNonTermAnalysis.LOG.writeln("######### STEM #########");
	    GeoNonTermAnalysis.LOG.writeln("########################");
	    GeoNonTermAnalysis.LOG.writeln("Recieved: " + GeoNonTermAnalysis.LOG.arrayToString(array));
	    GeoNonTermAnalysis.LOG.writeln("array[0]: " + array[0] + " saved in startFunctionSymbol");
	    GeoNonTermAnalysis.LOG.writeln("Define Rest as STEM.");
	}

	int[] stem = new int[array.length - 1];

	for (int i = 0; i < stem.length; i++) {
	    stem[i] = Integer.parseInt(array[i + 1].toString());
	}

	stemVec = new VecInt(stem);
	if (SHOULD_PRINT) {
	    GeoNonTermAnalysis.LOG.writeln("Final STEM: " + this.toString());
	    GeoNonTermAnalysis.LOG.writeln("########################");
	}
    }

    /**
     * transforms the {@link #stemVec} x into a vector representation of the
     * syntax: {x_1,x_2,...,x_n}
     */
    public String toString() {
	StringBuilder sb = new StringBuilder();

	sb.append("{ ");
	for (int i = 0; i < stemVec.size(); i++) {
	    sb.append(stemVec.get(i));
	    if (i < stemVec.size() - 1)
		sb.append(", ");
	}
	sb.append("}");

	return sb.toString();
    }

    /**
     * @return the stemVec
     */
    public VecInt getStemVec() {
	return stemVec;
    }

    /**
     * @param stemVec
     *            the stemVec to set
     */
    public void setStemVec(VecInt stemVec) {
	this.stemVec = stemVec;
    }

    /**
     * returns the {@link #startFunctionSymbol}
     * 
     * @return the {@link #startFunctionSymbol}
     */
    public FunctionSymbol getStartFunctionSymbol() {
	return this.startFunctionSymbol;
    }

}
