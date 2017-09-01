package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

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
     * the STEM vector to calculate for example the geometric series
     */
    private GNAVector stemVec;

    /**
     * the {@link FunctionSymbol} that is the start symbol <br>
     * Example: f_99
     */
    private FunctionSymbol startFunctionSymbol;

    // private int SHOULD_PRINT = 0;

    /**
     * initializing a new STEM by taking an array of {@link FunctionSymbol} and
     * extract the preset/STEM values. <br>
     * The first element is the {@link #startFunctionSymbol} and the following
     * are the values. <br>
     * <br>
     * Example: [f_99,3,10] -> {@link #startFunctionSymbol}=f_99 and
     * {@link #stemVec}={3,10}
     * 
     * @param symbols
     *            a {@link FunctionSymbol} array
     */
    public Stem(FunctionSymbol[] symbols, int printPriority) {
	assert symbols.length > 0;
	startFunctionSymbol = symbols[0];
	if (printPriority >= 3) {
	    Logger.getLog().writeln("Recieved: " + Logger.getLog().arrayToString(symbols));
	    Logger.getLog().writeln("array[0]: " + symbols[0] + " saved in startFunctionSymbol");
	    Logger.getLog().writeln("Define Rest as STEM.");
	}

	int[] stem = new int[symbols.length - 1];

	for (int i = 0; i < stem.length; i++) {
	    stem[i] = Integer.parseInt(symbols[i + 1].toString());
	}

	stemVec = new GNAVector(stem);

	if (printPriority >= 1) {
	    Logger.getLog().writeln("Final STEM: " + this.toString());
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
    public GNAVector getStemVec() {
	return stemVec;
    }

    /**
     * @param stemVec
     *            the stemVec to set
     */
    public void setStemVec(GNAVector stemVec) {
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
