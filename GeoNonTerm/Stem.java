package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import aprove.DPFramework.IDPProblem.IGeneralizedRule;
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
     * be printed using the {@link Logger}.
     */
    private static boolean SHOULD_PRINT = false;

    /**
     * the STEM vector to calculate for example the geometric series
     */
    private GNAVector stemVec;

    /**
     * the {@link FunctionSymbol} that is the start symbol <br>
     * Example: f_99
     */
    private FunctionSymbol startFunctionSymbol;

    private int index;

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
    public Stem(FunctionSymbol[] symbols, int index) {
	assert symbols.length > 0;
	this.index = index;
	startFunctionSymbol = symbols[0];
	if (SHOULD_PRINT) {
	    Logger.getLog().startClassOutput("Stem");
	    Logger.getLog().writeln("Recieved: " + Logger.getLog().arrayToString(symbols));
	    Logger.getLog().writeln("array[0]: " + symbols[0] + " saved in startFunctionSymbol");
	    Logger.getLog().writeln("Define Rest as STEM.");
	}

	int[] stem = new int[symbols.length - 1];

	for (int i = 0; i < stem.length; i++) {
	    stem[i] = Integer.parseInt(symbols[i + 1].toString());
	}

	stemVec = new GNAVector(stem);
	if (SHOULD_PRINT) {
	    Logger.getLog().endClassOutput("Stem");
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
    /*
     * public void createUpdated(String[] strings, String[] varNames) {
     * Logger.getLog().writeln("strings: " +
     * Logger.getLog().arrayToString(strings));
     * Logger.getLog().writeln("varnames: " +
     * Logger.getLog().arrayToString(varNames)); Logger.getLog().close();
     * GNAVector vec = new GNAVector(varNames.length, 0); for (int i = 0; i <
     * varNames.length; i++) { int occ = this.getIndexOfName(varNames,
     * strings[i]); if (occ != -1) vec.set(occ, stemVec.get(i)); } stemVec =
     * vec;
     * 
     * }
     * 
     * private int getIndexOfName(String[] strings, String name) { for (int i =
     * 0; i < strings.length; i++) if (strings[i].equals(name)) return i; return
     * -1; }
     * 
     * public int getIndex() { return this.index; }
     */
}
