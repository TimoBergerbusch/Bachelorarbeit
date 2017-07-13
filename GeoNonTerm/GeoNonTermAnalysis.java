package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import aprove.DPFramework.BasicStructures.Position;
import aprove.DPFramework.BasicStructures.TRSCompoundTerm;
import aprove.DPFramework.BasicStructures.TRSFunctionApplication;
import aprove.DPFramework.BasicStructures.TRSTerm;
import aprove.DPFramework.BasicStructures.TRSVariable;
import aprove.DPFramework.IDPProblem.IGeneralizedRule;
import aprove.Framework.BasicStructures.FunctionSymbol;
import aprove.Framework.IntTRS.IRSwTProblem;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNNode;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNTreeParser;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.UnsupportetArithmeticSymbolException;

/**
 * The main class of the geometric non-termination analysis presented by Jan
 * Leike and Matthias Heizmann. (Source:
 * https://monteverdi.informatik.uni-freiburg.de/tomcat/Website/?ui=tool&tool=lasso_ranker)
 * <br/>
 * <br/>
 * This class generates the STEM X and LOOP parts of a given
 * {@link aprove.Framework.IntTRS.IRSwTProblem#IRSwTProblem(immutables.Immutable.ImmutableSet)
 * IRSwTProblem}, derives a speed matrix J as a column vector of generalized
 * eigenvalues and a direction matrix Y. <br/>
 * After the derivation of the iteration matrix A and constants b, so that A*(x
 * x') <= b , the method uses a SMTSolver to derive a so called Geometric
 * Non-Termination Argument.
 * 
 * @author Timo Bergerbusch *
 */
public class GeoNonTermAnalysis {

    /**
     * multiplies a {@link VecInt} with -1
     * 
     * @param vec
     *            the {@link VecInt} that should be negated
     * @return the negated {@link VecInt}
     */
    protected static VecInt negateVec(VecInt vec) {
	for (int i = 0; i < vec.size(); i++) {
	    vec.set(i, vec.get(i) * -1);
	}
	return vec;
    }

    protected static VecInt add(VecInt a, VecInt b) {
	assert a.size() == b.size();

	VecInt res = new VecInt(a.size(), 0);
	for (int i = 0; i < a.size(); i++) {
	    res.set(i, a.get(i) + b.get(i));
	}

	return res;
    }

    protected static boolean lessOrEqualThan(VecInt vec, VecInt b) {
	assert vec.size() == b.size();

	for (int i = 0; i < vec.size(); i++)
	    if (!(vec.get(i) <= b.get(i)))
		return false;

	return true;
    }

    protected static VecInt mult(VecInt vec, int cons) {
	VecInt res = new VecInt(vec.size(), 0);
	for (int i = 0; i < vec.size(); i++) {
	    res.set(i, vec.get(i) * cons);
	}
	return res;
    }

    private static int[] computeEigenvalues(UpdateMatrix m) {
	EigenDecomposition ed = new EigenDecomposition(m.getAsRealMatrix());
	double[] values = ed.getRealEigenvalues();
	int[] normValues = new int[values.length];
	for (int i = 0; i < values.length; i++)
	    normValues[i] = (int) Math.abs((values[i]));
	return normValues;
    }

    /**
     * converts a {@link Set} of {@link TRSVariable TRSVariable's} into a
     * <code>String</code> array of the same size only storing their names given
     * by {@link TRSVariable}{@link #toString()}.
     * 
     * @param variables
     *            the Set of {@link TRSVariable TRSVariable's}
     * @return the <code>String</code> array of the variables names
     */
    private static String[] deriveVariablesAsStringArray(Set<TRSVariable> variables) {
	String[] names = new String[variables.size()];
	Iterator<TRSVariable> iterator = variables.iterator();
	int i = 0;
	while (iterator.hasNext()) {
	    names[i] = iterator.next().toString();
	    i++;
	}

	return names;
    }

    /**
     * a static boolean to determine if the information about the process should
     * be printed using the {@link GeoNonTermAnalysis#LOG Logger}.
     */
    private static boolean SHOULD_PRINT = false;

    /**
     * a Logger or Outputstream to print data into a file <br/>
     * (Default: "/home/timo/Downloads/GeoNonTerm-ausgabe.txt")
     */
    public static final Logger LOG = new Logger();

    /**
     * the original IRSwTProblem, which is generated out of the LLVMGraph
     * 
     */
    private final IRSwTProblem problem;

    /**
     * the rules of the problem. <br>
     * these are separately stored to derive {@link Stem} and {@link Loop}
     * separately without recomputing the rules.
     */
    private final IGeneralizedRule[] rules;

    /**
     * the STEM of the loop program
     */
    private Stem stem;

    /**
     * the LOOP of the loop program
     */
    private Loop loop;

    /**
     * The constructor of the GeoNonTermAnalysis
     * 
     * @param problem
     *            the reduced IRSwTProblem generated by the LLVMGraph
     */
    public GeoNonTermAnalysis(IRSwTProblem problem) {
	this.problem = problem;
	rules = this.problem.getRules().toArray(new IGeneralizedRule[] {});

	this.deriveSTEM();
	this.deriveLOOP();

	GeoNonTermArgument gna = this.tryDerivingAGNA();
	LOG.writeln("GNA-Test: " + gna.validate(loop.getIterationMatrix(), loop.getIterationConstants()));

	LOG.close();
    }

    /**
     * derives the STEM part of the {@link #problem IRSwTProblem} using the
     * {@link aprove.Framework.IntTRS.IRSwTProblem#getStartTerm() startterm}
     */
    private void deriveSTEM() {
	TRSFunctionApplication startterm = this.problem.getStartTerm();

	if (SHOULD_PRINT)
	    LOG.writeln("Der Startterm ist: " + startterm.toString());

	for (int i = 0; i < rules.length; i++) {
	    if (rules[i].getLeft().equals(startterm)) {
		// Suchen der ersten passenden regel
		if (SHOULD_PRINT)
		    LOG.writeln("First rule match:" + rules[i].toString());

		TRSTerm r = rules[i].getRight();

		FunctionSymbol[] arr = new FunctionSymbol[r.getSize() - 1];
		arr = r.getFunctionSymbols().toArray(arr);
		stem = new Stem(arr);
		break; // danach kann abgebrochen werden
	    } else if (i == rules.length - 1) {
		// dieser Fall darf eigentlich nie eintreten
		LOG.writeln("ERROR: No match found for startterm.");
	    }
	}

    }

    /**
     * computes the {@link Loop} by computing the updateMatrix and
     * updateConstants of the {@link Loop} using
     * {@link #deriveUpdatePart(TRSTerm, TRSTerm)} and afterwards computing the
     * guardMatrix with corresponding guardConstants using
     * {@link #deriveGuardPart(IGeneralizedRule)}.
     * 
     * @see GeoNonTermAnalysis#deriveUpdatePart(TRSTerm, TRSTerm)
     * @see #deriveGuardPart(IGeneralizedRule)
     * @see Loop
     */
    private void deriveLOOP() {
	this.loop = new Loop();

	int index = this.getIndexOfSymbol(rules, stem.getStartFunctionSymbol());
	TRSFunctionApplication leftSide = rules[index].getLeft();
	TRSTerm rightSide = rules[index].getRight();

	// Vergleichen des ersten FunctionSymbol der beiden Seiten
	if (leftSide.getFunctionSymbols().toArray(new FunctionSymbol[] {})[0]
		.equals(rightSide.getFunctionSymbols().toArray(new FunctionSymbol[] {})[0])) {
	    // die Regel hat die Form f_x -> f_x :|: cond
	    if (SHOULD_PRINT) {
		LOG.writeln("Investigating the rule " + rules[index]);
		LOG.writeln("Rule " + index + " is of the Form: f_x -> f_x :|: cond");
	    }

	    // loop.setDimensions(leftSide.getVariables().size(),
	    // rules[index].getCondTerm().toString().split("&&").length);

	    // Suche aus dem rechten Teil die Updates raus
	    this.deriveUpdatePart(leftSide, rightSide);

	    // suche aus den conditions the Regeln raus
	    this.deriveGuardPart(rules[index]);

	    loop.computeIterationMatrixAndConstants();

	    if (SHOULD_PRINT) {
		LOG.writeln(Loop.getSystemAsString(loop.getIterationMatrix(),
			new String[] { "x0_0", "x0_1", "x1_0", "x1_1" }, loop.getIterationConstants()));
	    }
	    // LOG.writeln(rightSide.getSubterm(Position.create(0)).toString());
	} else {
	    // die Regel hat die Form f_x -> f_y :|: cond
	    if (SHOULD_PRINT)
		LOG.writeln("Rule " + index + " is of the Form: f_x -> f_y :|: cond");
	}
    }

    /**
     * This method derives the update matrix and constants to a given rule, by
     * parsing it into a {@link RPNNode RPNTree} using
     * {@link RPNTreeParser#parseSetToTree(TRSTerm)}. Note: it gets left and
     * right instead of the rule itself, because they are already computed
     * anyway.
     * 
     * @param leftSide
     *            the left side the rule
     * @param rightSide
     *            the right side of the rule.
     */
    private void deriveUpdatePart(TRSTerm leftSide, TRSTerm rightSide) {
	String[] occuringVars = this.deriveVariablesAsStringArray(leftSide.getVariables());

	// Es wird versucht die rechte Seite in ein RPNTree zu parsen
	RPNNode[] variableUpdates = new RPNNode[leftSide.getVariables().size()];
	try {
	    for (int i = 0; i < variableUpdates.length; i++)
		variableUpdates[i] = RPNTreeParser.parseSetToTree(rightSide.getSubterm(Position.create(i)));
	} catch (UnsupportetArithmeticSymbolException e) {
	    e.printStackTrace();
	    LOG.writeln("UnsupportetArithmeticSymbolException: " + e.getMessage());
	} catch (ArrayIndexOutOfBoundsException e) {
	    e.printStackTrace();
	    LOG.writeln("ArrayIndexOutOfBoundsException: More Vars then Updates");
	}
	UpdateMatrix updateMatrix = new UpdateMatrix(occuringVars);
	VecInt updateConstants = new VecInt(occuringVars.length, 0);

	// Herleiten der Update-Matrix Einträge
	for (int i = 0; i < occuringVars.length; i++) {
	    for (int j = 0; j < occuringVars.length; j++) {
		// LOG.writeln("Does " + occuringVars[i] + " contain " +
		// occuringVars[j] + "? "
		// + variableUpdates[i].containsVar(occuringVars[j]) + " \t
		// Value:"
		// + variableUpdates[i].getFactorOfVar(occuringVars[j]));
		updateMatrix.setEntry(occuringVars[i], occuringVars[j],
			variableUpdates[i].getFactorOfVar(occuringVars[j]));
	    }
	    // LOG.writeln("Does Update of Var. " + i + " contain a Constant
	    // term? Term: "
	    // + variableUpdates[i].getConstantTerm());
	    updateConstants.set(i, variableUpdates[i].getConstantTerm());
	}

	// LOG.writeln(updateMatrix);
	loop.setUpdateMatrix(updateMatrix);
	loop.setUpdateConstants(updateConstants);
    }

    /**
     * This method derives the guard matrix and constants of a given rule, by
     * parsing only the condition terms into a {@link RPNNode RPNTree} after
     * splitting them into the different rules. <br>
     * So it splits: <code>x_1 > 0 && x_3 > 5</code><br>
     * Into the two conditions and parses them separately.
     * 
     * @param rule
     *            the rule containing the conditions
     */
    private void deriveGuardPart(IGeneralizedRule rule) {
	// LOG.writeln("++++++++++");
	// LOG.writeln("Cond Term: " + rule.getCondTerm());
	// LOG.writeln("Cond Vars" + rule.getCondVariables());

	ArrayList<TRSTerm> condTerms = new ArrayList<>();
	Stack<TRSTerm> stack = new Stack<>();
	stack.push(rule.getCondTerm());

	TRSTerm curr;
	while (!stack.isEmpty()) {
	    curr = stack.pop();
	    // LOG.writeln("investigating: " + curr.toString());
	    if (curr instanceof TRSCompoundTerm) {
		if (((TRSCompoundTerm) curr).getFunctionSymbol().toString().equals("&&_2")) {
		    stack.push(((TRSCompoundTerm) curr).getArgument(0));
		    stack.push(((TRSCompoundTerm) curr).getArgument(1));
		    // LOG.writeln("TRSCompoundTerm");
		} else {
		    condTerms.add(curr);
		    // LOG.writeln("Not TRSCompoundTerm");
		}
	    }
	}

	ArrayList<TRSTerm> condTermsReverse = new ArrayList<>();
	for (int i = condTerms.size() - 1; i >= 0; i--) {
	    condTermsReverse.add(condTerms.get(i));
	}
	condTerms = condTermsReverse;

	// Die GuardMatrix für die versch. Bedingungen
	UpdateMatrix guardMatrix = new UpdateMatrix(condTerms.size(), rule.getLeft().getVariables().size(),
		this.deriveVariablesAsStringArray(rule.getLeft().getVariables()));
	// Die Constanten, welche erfüllt werden müssen
	VecInt guardConstants = new VecInt(condTerms.size(), 0);

	// LOG.writeln("Cond Terms: " + condTerms);
	for (int i = 0; i < condTerms.size(); i++)
	    try {
		RPNNode root = RPNTreeParser.parseSetToTree(condTerms.get(i));
		if (SHOULD_PRINT)
		    LOG.writeln("Überprüfe: " + root.toString());
		for (TRSVariable var : condTerms.get(i).getVariables()) {
		    if (root.containsVar(var.toString())) {
			if (SHOULD_PRINT)
			    LOG.writeln("-> beinhaltet " + var.toString() + " mit dem Factor: "
				    + root.getFactorOfVar(var.toString()));
			guardMatrix.setEntry(i, var.toString(), root.getFactorOfVar(var.toString()));
		    }
		}
		guardConstants.set(i, root.getConstantTerm());
	    } catch (UnsupportetArithmeticSymbolException e) {
		e.printStackTrace();
	    }
	// alle > in < umdrehen, sodass die constante immer < da steht:
	// ... < c
	UpdateMatrix.negateMatrix(guardMatrix);
	for (int i = 0; i < guardConstants.size(); i++)
	    guardConstants.set(i, guardConstants.get(i) * -1 - 1);

	loop.setGuardUpdates(guardMatrix);
	loop.setGuardConstants(guardConstants);

	// LOG.writeln(guardMatrix);
	// LOG.writeln(guardConstants);
	// LOG.writeln("++++++++++");
    }

    private GeoNonTermArgument tryDerivingAGNA() {
	int[] eigenvalues = computeEigenvalues(loop.getUpdateMatrix());

	// +++++++++++++++++++++++++++++++++++++++++++++
	ISolver solver = SolverFactory.newDefault();
	solver.setTimeout(36000);
	solver.newVar(4);
	try {
	    solver.addClause(new VecInt(new int[] { 1, 2, 3, 4 }));

	    IProblem problem = solver;

	    LOG.writeln("Problem: " + problem.toString());
	    LOG.writeln("Result: " + problem.isSatisfiable());
	} catch (ContradictionException | TimeoutException e) {
	    e.printStackTrace();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++
	GeoNonTermArgument gna = new GeoNonTermArgument(stem,
		new VecInt[] { new VecInt(new int[] { 12, 0 }), new VecInt(new int[] { 10, 2 }) }, eigenvalues,
		new int[] { 1 });

	return gna;
    }

    /**
     * looks up the index of a {@link IGeneralizedRule} in an array that starts
     * with the given {@link FunctionSymbol}
     * 
     * @param rules
     *            the array of {@link IGeneralizedRule}'s
     * @param fs
     *            the query {@link FunctionSymbol}
     * @return den index ( -1 if isn't contained)
     */
    private int getIndexOfSymbol(IGeneralizedRule[] rules, FunctionSymbol fs) {
	int j = -1;
	for (int i = 0; i < this.rules.length; i++) {
	    if (rules[i].getLeft().getFunctionSymbol().equals(fs)) {
		j = i;
		if (SHOULD_PRINT) {
		    LOG.writeln("##########");
		    LOG.writeln(rules[i].getLeft().toString() + " matches " + fs.toString());
		    LOG.writeln("So Rule Nr." + j + " starts with " + fs.toString());
		    LOG.writeln("##########");
		}
		break;
	    }
	}

	return j;
    }

}
