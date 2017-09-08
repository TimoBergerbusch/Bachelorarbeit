package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import aprove.DPFramework.BasicStructures.Position;
import aprove.DPFramework.BasicStructures.TRSCompoundTerm;
import aprove.DPFramework.BasicStructures.TRSFunctionApplication;
import aprove.DPFramework.BasicStructures.TRSTerm;
import aprove.DPFramework.BasicStructures.TRSVariable;
import aprove.DPFramework.IDPProblem.IGeneralizedRule;
import aprove.Framework.BasicStructures.FunctionSymbol;
import aprove.Framework.BasicStructures.Arithmetic.Integer.IntegerRelationType;
import aprove.Framework.BasicStructures.Arithmetic.Integer.PlainIntegerRelation;
import aprove.Framework.IntTRS.IRSwTProblem;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.ArithmeticSymbol;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNConstant;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNFunctionSymbol;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNNode;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNTreeParser;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNVariable;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.UnsupportetArithmeticSymbolException;
import aprove.Framework.Logic.YNM;
import aprove.Framework.SMT.Expressions.SMTExpression;
import aprove.Framework.SMT.Expressions.Calls.Call1;
import aprove.Framework.SMT.Expressions.Symbols.Symbol;
import aprove.Framework.SMT.Solver.SMTLIB.FunctionDefinition;
import aprove.Framework.SMT.Solver.SMTLIB.Model;
import aprove.Framework.SMT.Solver.Z3.Z3Solver;
import aprove.Framework.Utility.GenericStructures.Pair;

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

    private ArrayList<Pair<RPNVariable, RPNNode>> substitutions = new ArrayList<>();

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
     * be printed using the {@link Logger#getLog() Logger}.
     */
    private static int SHOULD_PRINT = 3;

    private static boolean GIVE_UP = false;

    /**
     * the original IRSwTProblem, which is generated out of the LLVMGraph
     * 
     */
    private IRSwTProblem problem;

    /**
     * the rules of the problem. <br>
     * these are separately stored to derive {@link Stem} and {@link Loop}
     * separately without recomputing the rules.
     */
    private IGeneralizedRule[] rules;

    /**
     * the STEM of the loop program
     */
    private Stem stem;

    /**
     * the LOOP of the loop program
     */
    private Loop loop;

    /**
     * a {@link SMTFactory} to create the SMT-Problem, which results in a
     * {@link GeoNonTermArgument} if one exists.
     */
    private SMTFactory smt = new SMTFactory();

    /**
     * an already derived {@link GeoNonTermAnalysis} to safe, so that we would
     * not compute it a second time.
     */
    private GeoNonTermArgument gna;

    /**
     * The constructor of the GeoNonTermAnalysis
     * 
     * @param problem
     *            the reduced IRSwTProblem generated by the LLVMGraph
     */
    public GeoNonTermAnalysis(IRSwTProblem problem) {
	this.problem = problem;
	rules = this.problem.getRules().toArray(new IGeneralizedRule[] {});

	if (SHOULD_PRINT >= 1) {
	    // Logger.getLog().writeln("The given Problem:");
	    Logger.getLog().writeln(problem.toString());
	    Logger.getLog().writeln("_______________________");
	}

	if (this.problem.getStartTerm() != null) {

	    // derive the STEM
	    this.deriveSTEM();

	    // derive the LOOP, iff not already given up
	    if (!GIVE_UP)
		this.deriveLOOP();

	    // create a gna == null to further work with
	    GeoNonTermArgument gna = null;

	    // try computing a GNA iff not already given up
	    if (!GIVE_UP)
		gna = this.tryDerivingAGNA();

	    if (gna != null) {
		boolean test = gna.validate(loop.getIterationMatrix(), loop.getIterationConstants());
		if(SHOULD_PRINT >= 1)
		    Logger.getLog().writeln("GNA-Test: " + test);
		if(!test){
		    Logger.getLog().close();
		    assert test;
		}
	    }
	    this.gna = gna;
	    // Logger.getLog().writeln(gna);
	}

	Logger.getLog().close();
    }

    /**
     * Derives the STEM part of the {@link #problem IRSwTProblem} using the
     * {@link aprove.Framework.IntTRS.IRSwTProblem#getStartTerm() startterm}
     */
    private void deriveSTEM() {
	TRSFunctionApplication startterm = this.problem.getStartTerm();

	if (SHOULD_PRINT >= 1) {
	    Logger.getLog().startClassOutput("STEM");
	    if (startterm != null)
		Logger.getLog().writeln("Der Startterm ist: " + startterm.toString());
	    else
		Logger.getLog().writeln("Das IRS hat keinen Startterm.");
	}

	if (startterm == null) {
	    if (SHOULD_PRINT >= 1) {
		Logger.getLog().writeln("Problem: No Startterm exists.");
		Logger.getLog().writeln("     ->: Give up on derivation.");
	    }
	    GIVE_UP = true;
	    Logger.getLog().close();
	} else
	    for (int i = 0; i < rules.length; i++) {
		if (rules[i].getLeft().equals(startterm)) {
		    // Suchen der ersten passenden regel
		    if (SHOULD_PRINT >= 2)
			Logger.getLog().writeln("First rule match:" + rules[i].toString());

		    FunctionSymbol[] ruleAsArray = this.getRuleAsArray(rules[i]);
		    if (!GIVE_UP)
			stem = new Stem(ruleAsArray, SHOULD_PRINT);
		    break; // danach kann abgebrochen werden
		} else if (i == rules.length - 1) {
		    // dieser Fall darf eigentlich nie eintreten
		    if (SHOULD_PRINT >= 1) {
			Logger.getLog().writeln("ERROR: No match found for startterm.");
			Logger.getLog().writeln("   ->: Give up on derivation.");
		    }
		    GIVE_UP = true;
		    Logger.getLog().close();
		}
	    }
	if (SHOULD_PRINT >= 1)
	    Logger.getLog().endClassOutput("STEM");

    }

    /**
     * This method takes in a rule and returns the variables that are mentioned
     * in the <u>right</u> side of the rule. Therefore it does not hold for
     * rule's that have different var's within a rule.
     * 
     * @param rule
     *            the rule to extract the variables from
     * @return the extracted variables
     */
    private FunctionSymbol[] getRuleAsArray(IGeneralizedRule rule) {
	assert ((TRSFunctionApplication) rule.getLeft()).getArity() == 0;

	TRSFunctionApplication r = (TRSFunctionApplication) rule.getRight();
	FunctionSymbol[] arr = new FunctionSymbol[r.getArity() + 1];
	arr[0] = r.getFunctionSymbol();

	if (r.getVariables().size() == 0) {
	    for (int i = 0; i < r.getArity(); i++) {
		TRSTerm arg = r.getArgument(i);
		arr[i + 1] = ((TRSFunctionApplication) arg).getFunctionSymbol();
	    }
	} else {

	    // Try deriving a sat. assignment for the STEM
	    ArrayList<TRSTerm> guards = this.getGuardConditions(rule);

	    ArrayList<RPNNode> condFilteredEq = this.filterEqualities(guards, rule);

	    ArrayList<RPNNode> condNormalized = this.normalize(condFilteredEq);

	    TRSVariable[] vars = rule.getAllVariables().toArray(new TRSVariable[0]);
	    ArrayList<TRSVariable> variablesOfInterest = this.getVariablesOfInterest(rule);

	    GNAMatrix guardMatrix = new GNAMatrix(condNormalized.size(), variablesOfInterest.size(),
		    GeoNonTermAnalysis.deriveVariablesAsStringArray(new HashSet<>(variablesOfInterest)));
	    GNAVector guardConstants = new GNAVector(condNormalized.size(), 0);

	    for (int i = 0; i < condNormalized.size(); i++) {
		for (int j = 0; j < vars.length; j++) {
		    if (condNormalized.get(i).containsVar(vars[j].getName())) {
			int factor = condNormalized.get(i).getFactorOfVar(vars[j].getName());
			guardMatrix.setEntry(i, vars[j].getName(), factor);
		    }
		}
		guardConstants.set(i, condNormalized.get(i).getConstantTerm());
	    }

	    try {
		Pair<GNAMatrix, GNAVector> pair = new Pair<GNAMatrix, GNAVector>(guardMatrix, guardConstants);
		GNAVariableVector vec = new GNAVariableVector(pair.getKey().getVarNames());

		Z3Solver solver = smt.getSolver();
		smt.addAssertion(pair.getKey(), vec, pair.getValue());

		for (int i = 0; i < r.getArity(); i++) {
		    RPNNode rpnnode = RPNTreeParser.parseSetToTree(r.getArgument(i));

		    for (Pair<RPNVariable, RPNNode> p : substitutions) {
			if (rpnnode.containsVar(p.getKey().getValue())) {
			    rpnnode = rpnnode.applySubstitution(p.getKey(), p.getValue());
			}
		    }

		    PlainIntegerRelation condRule = smt.createRule(IntegerRelationType.EQ, smt.createVar("GNAv" + i),
			    smt.parseRPNTreeToSMTRule(rpnnode));

		    solver.addAssertion(condRule.toSMTExp());
		}

		if (solver.checkSAT() == YNM.YES) {
		    if (SHOULD_PRINT >= 2) {
			Logger.getLog().writeln("Derived STEM-Values from the given StartSymbol");
			Logger.getLog().writeln(solver.getModel());
		    }
		    for (int i = 0; i < r.getArity(); i++) {
			arr[i + 1] = FunctionSymbol
				.create(this.getValueOfVariableWithinModel(solver.getModel(), "GNAv" + i) + "", 0);
		    }

		} else {
		    if (SHOULD_PRINT >= 1) {
			GIVE_UP = true;
			if (SHOULD_PRINT >= 1) {
			    Logger.getLog().writeln("Problem: No possible Startterm fulfilling the constrains ex.");
			    Logger.getLog().writeln("     ->: Give up on derivation");
			}
			Logger.getLog().close();
		    }

		}
	    } catch (UnsupportetArithmeticSymbolException e) {
		GIVE_UP = true;
		if (SHOULD_PRINT >= 1) {
		    Logger.getLog().writeln("Error: Occuring during the STEM derivation. Code: 12");
		    Logger.getLog().writeln("   ->: Give Up");
		}
		Logger.getLog().close();
	    }

	}

	return arr;
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

	if (SHOULD_PRINT >= 1)
	    Logger.getLog().startClassOutput("LOOP");
	this.loop = new Loop();

	int index = this.getIndexOfSymbol(rules, stem.getStartFunctionSymbol());
	TRSFunctionApplication leftSide = rules[index].getLeft();
	TRSTerm rightSide = rules[index].getRight();

	// Vergleichen des ersten FunctionSymbol der beiden Seiten
	if (leftSide.getFunctionSymbols().toArray(new FunctionSymbol[] {})[0]
		.equals(rightSide.getFunctionSymbols().toArray(new FunctionSymbol[] {})[0])) {
	    // die Regel hat die Form f_x -> f_x :|: cond
	    if (SHOULD_PRINT >= 3) {
		Logger.getLog().writeln("Investigating the rule " + rules[index]);
		Logger.getLog().writeln("Rule " + index + " is of the Form: f_x -> f_x :|: cond");
	    }
	    // suche aus den conditions the Regeln raus
	    this.deriveGuardPart(rules[index]);
	    if (SHOULD_PRINT >= 1)
		Logger.getLog().writeln("Successfully derived Guards");
	    if (SHOULD_PRINT >= 2) {
		Logger.getLog().writeln("Guard Matrix:");
		Logger.getLog().writeln(loop.getGuardUpdates());
		Logger.getLog().writeln("Guard Constants:");
		Logger.getLog().writeln(loop.getGuardConstants());
	    }

	    // Suche aus dem rechten Teil die Updates raus
	    this.deriveUpdatePart(rules[index]);
	    if (SHOULD_PRINT >= 1)
		Logger.getLog().writeln("Successfully derived Updates");
	    if (SHOULD_PRINT >= 2) {
		Logger.getLog().writeln("Update Matrix:");
		Logger.getLog().writeln(loop.getUpdateMatrix());
		Logger.getLog().writeln("Update Constants:");
		Logger.getLog().writeln(loop.getUpdateConstants());
	    }

	    loop.computeIterationMatrixAndConstants();
	    if (SHOULD_PRINT >= 1)
		Logger.getLog().writeln("Successfully derived Iteration-Matrices");
	    if (SHOULD_PRINT >= 2) {
		Logger.getLog().writeln("Iteration Matrix:");
		Logger.getLog().writeln(loop.getIterationMatrix());
		Logger.getLog().writeln("Iteration Constants:");
		Logger.getLog().writeln(loop.getIterationConstants());
	    }
	} else {
	    // die Regel hat die Form f_x -> f_y :|: cond
	    if (SHOULD_PRINT >= 1) {
		Logger.getLog().writeln("Rule " + index + " is of the Form: f_x -> f_y :|: cond");
		Logger.getLog().writeln("Problem: has not the desired form.");
		Logger.getLog().writeln("     ->: give up on derivation");
	    }
	    Logger.getLog().close();
	    GIVE_UP = true;
	}

	if (SHOULD_PRINT >= 1)
	    Logger.getLog().endClassOutput("LOOP");
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
    private void deriveUpdatePart(IGeneralizedRule rule) {
	TRSTerm leftSide = rule.getLeft();
	TRSTerm rightSide = rule.getRight();

	String[] occuringVars = GeoNonTermAnalysis.deriveVariablesAsStringArray(rule.getLeft().getVariables());

	RPNNode[] variableUpdates = new RPNNode[leftSide.getVariables().size()];

	try {
	    for (int i = 0; i < variableUpdates.length; i++)
		variableUpdates[i] = RPNTreeParser.parseSetToTree(rightSide.getSubterm(Position.create(i)));
	} catch (UnsupportetArithmeticSymbolException e) {
	    e.printStackTrace();
	    if (SHOULD_PRINT >= 1)
		Logger.getLog().writeln("UnsupportetArithmeticSymbolException: " + e.getMessage());
	} catch (ArrayIndexOutOfBoundsException e) {
	    e.printStackTrace();
	    if (SHOULD_PRINT >= 1)
		Logger.getLog().writeln("ArrayIndexOutOfBoundsException: More Vars then Updates");
	}
	GNAMatrix updateMatrix = new GNAMatrix(occuringVars);
	GNAVector updateConstants = new GNAVector(occuringVars.length, 0);

	// anwenden der Substitutionen aus der Guard
	for (int i = 0; i < variableUpdates.length; i++) {
	    for (Pair<RPNVariable, RPNNode> p : substitutions) {
		if (variableUpdates[i].containsVar(p.getKey().getValue())) {
		    variableUpdates[i] = variableUpdates[i].applySubstitution(p.getKey(), p.getValue());
		}
	    }
	}
	if (SHOULD_PRINT >= 2)
	    Logger.getLog().writeln(Logger.getLog().arrayToString(variableUpdates));

	// Herleiten der Update-Matrix Einträge
	for (int i = 0; i < variableUpdates.length; i++) {
	    for (int j = 0; j < occuringVars.length; j++) {
		int factor = variableUpdates[i].getFactorOfVar(occuringVars[j]);
		updateMatrix.setEntry(occuringVars[i], occuringVars[j], factor);
	    }
	    updateConstants.set(i, variableUpdates[i].getConstantTerm());
	}
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

	ArrayList<TRSTerm> guards = this.getGuardConditions(rule);
	if (SHOULD_PRINT >= 2) {
	    Logger.getLog().writeln("All plain guards:");
	    Logger.getLog().writeln(Logger.getLog().arrayToString(guards.toArray(new TRSTerm[0])));
	}

	ArrayList<RPNNode> guardsWithoutEQ = this.filterEqualities(guards, rule);
	if (SHOULD_PRINT >= 2) {
	    Logger.getLog().writeln("After filtering Equalities:");
	    Logger.getLog().writeln(Logger.getLog().arrayToString(guardsWithoutEQ.toArray(new RPNNode[0])));
	}

	ArrayList<RPNNode> normalizedGuards = this.normalize(guardsWithoutEQ);
	if (SHOULD_PRINT >= 2) {
	    Logger.getLog().writeln("After normalizing:");
	    Logger.getLog().writeln(Logger.getLog().arrayToString(normalizedGuards.toArray(new RPNNode[0])));
	}

	TRSVariable[] vars = rule.getAllVariables().toArray(new TRSVariable[0]);
	GNAMatrix guardMatrix = new GNAMatrix(normalizedGuards.size(), rule.getLeft().getVariables().size(),
		GeoNonTermAnalysis.deriveVariablesAsStringArray(rule.getLeft().getVariables()));
	GNAVector guardConstants = new GNAVector(normalizedGuards.size(), 0);
	for (int i = 0; i < normalizedGuards.size(); i++) {
	    for (int j = 0; j < vars.length; j++) {
		if (normalizedGuards.get(i).containsVar(vars[j].getName())) {
		    int factor = normalizedGuards.get(i).getFactorOfVar(vars[j].getName());
		    guardMatrix.setEntry(i, vars[j].getName(), factor);
		}
	    }
	    guardConstants.set(i, normalizedGuards.get(i).getConstantTerm());
	}

	loop.setGuardUpdates(guardMatrix);
	loop.setGuardConstants(guardConstants);
    }

    private ArrayList<TRSTerm> getGuardConditions(IGeneralizedRule rule) {
	ArrayList<TRSTerm> condTerms = new ArrayList<>();
	Stack<TRSTerm> stack = new Stack<>();
	stack.push(rule.getCondTerm());

	TRSTerm curr;
	while (!stack.isEmpty()) {
	    curr = stack.pop();

	    if (curr instanceof TRSCompoundTerm) {
		if (((TRSCompoundTerm) curr).getFunctionSymbol().toString().equals("&&_2")) {
		    stack.push(((TRSCompoundTerm) curr).getArgument(0));
		    stack.push(((TRSCompoundTerm) curr).getArgument(1));

		} else {
		    condTerms.add(curr);

		}
	    }
	}

	return condTerms;
    }

    private ArrayList<RPNNode> filterEqualities(ArrayList<TRSTerm> list, IGeneralizedRule rule) {
	ArrayList<RPNNode> guards = new ArrayList<>();

	ArrayList<String> set;

	substitutions.clear();

	if (!rule.getLeft().getVariables().isEmpty())
	    set = this.getSetSubtraction(rule.getRight().getVariables(), rule.getLeft().getVariables());
	else
	    set = this.getSetSubtraction(rule.getRight().getVariables(), this.getVariablesOfInterest(rule));

	if (SHOULD_PRINT >= 2) {
	    Logger.getLog().writeln("++++++++++++++++++");
	    Logger.getLog().writeln("SubstitutionSet: " + Logger.getLog().arrayToString(set.toArray()));
	    Logger.getLog().writeln("++++++++++++++++++");
	}

	for (TRSTerm t : list) {
	    try {
		RPNNode root = RPNTreeParser.parseSetToTree(t);
		if (root.containsEquality()) {
		    if (SHOULD_PRINT >= 3)
			Logger.getLog().writeln("Term: " + t + " contains equality.");
		    // ASSUMING = IS TOP LEVEL SYMBOL
		    RPNFunctionSymbol f = ((RPNFunctionSymbol) root);
		    assert f.getFunctionSymbol() == ArithmeticSymbol.EQUALS;

		    for (int i = 0; i < set.size(); i++) {
			// WORKS IF THE "NEW" VAR DOESNT HAVE A COEFFICIENT
			if (f.getLeft().containsVar(set.get(i)) || f.getRight().containsVar(set.get(i))) {
			    if (SHOULD_PRINT >= 3)
				Logger.getLog().writeln(set.get(i) + " is defined within this term.");

			    // ITERATE OVER SET
			    if (f.getRight().containsVar(set.get(i)) && !f.getLeft().containsVar(set.get(i))) {
				RPNNode r = f.getRight();
				f.setRight(f.getLeft());
				f.setLeft(r);
				if (SHOULD_PRINT >= 3)
				    Logger.getLog().writeln("!!!Changed sides of equality!!!");
			    }
			    if (f.getRight().containsVar(set.get(i))) {
				assert false;
				// neue var kam im term rechts und links vor.

			    } else if (f.getLeft().containsVar(set.get(i))) {
				RPNConstant consLeft = f.getLeft().getConstantNode();
				RPNConstant consRight = f.getRight().getConstantNode();
				if (consLeft != null && consRight != null) {
				    consRight.setValue(consRight.getValue() - consLeft.getValue());
				    f.setLeft(f.getLeft().remove(consLeft));
				} else if (consLeft != null) {
				    // Logger.getLog().writeln("Set new
				    // rightConsTerm: -" + consLeft.getValue());
				    f.setRight(new RPNFunctionSymbol(ArithmeticSymbol.PLUS, f.getRight(),
					    new RPNConstant(-consLeft.getValue())));
				    f.setLeft(f.getLeft().remove(consLeft));
				}

				// Logger.getLog().writeln("new f with fixed
				// const: " + f);

				if (f.getLeft() instanceof RPNFunctionSymbol) {
				    RPNFunctionSymbol fl = ((RPNFunctionSymbol) f.getLeft());
				    if (fl.getFunctionSymbol() == ArithmeticSymbol.PLUS) {
					if (fl.getLeft().containsVar(set.get(i))) {
					    // Logger.getLog().writeln("PLUS ex.
					    // in " + fl.toString() + " -> move
					    // "+ fl.getLeft() + " moves
					    // over.");
					    f.setLeft(fl.getLeft());
					    f.setRight(new RPNFunctionSymbol(ArithmeticSymbol.PLUS, f.getRight(),
						    new RPNFunctionSymbol(ArithmeticSymbol.TIMES, new RPNConstant(-1),
							    fl.getRight())));
					} else if (fl.getRight().containsVar(set.get(i))) {
					    // Logger.getLog().writeln("CAN
					    // HAPPEN?");
					    f.setLeft(fl.getRight());
					    f.setRight(new RPNFunctionSymbol(ArithmeticSymbol.PLUS, f.getRight(),
						    new RPNFunctionSymbol(ArithmeticSymbol.TIMES, new RPNConstant(-1),
							    fl.getLeft())));
					}
				    }
				}

				substitutions
					.add(new Pair<RPNVariable, RPNNode>(new RPNVariable(set.get(i)), f.getRight()));
				if (SHOULD_PRINT >= 3)
				    Logger.getLog()
					    .writeln("Try to subst. " + set.get(i) + " everywhere by " + f.getRight());
			    }
			}
		    }
		} else {
		    guards.add(root);
		}
	    } catch (UnsupportetArithmeticSymbolException e) {
		e.printStackTrace();
		assert false;
	    }
	}

	// APPLY SUBSTITUTIONS
	ArrayList<RPNNode> finalguards = new ArrayList<>();
	boolean change = false;
	for (RPNNode root : guards) {
	    for (Pair<RPNVariable, RPNNode> p : substitutions) {
		change = false;
		if (root.containsVar(p.getKey().getValue())) {
		    if (SHOULD_PRINT >= 3)
			Logger.getLog()
				.writeln(root + " contains " + p.getKey() + " so we subst. it with " + p.getValue());
		    finalguards.add(root.applySubstitution(p.getKey(), p.getValue()));
		    change = true;
		}
	    }
	    if (!change)
		finalguards.add(root);
	}

	return finalguards;
    }

    private ArrayList<TRSVariable> getVariablesOfInterest(IGeneralizedRule rule) {
	ArrayList<TRSVariable> set = new ArrayList<>();

	for (TRSTerm t : getGuardConditions(rule)) {
	    RPNNode node;
	    try {

		node = RPNTreeParser.parseSetToTree(t);
		// Logger.getLog().writeln("Term: " + node);
		// Logger.getLog().writeln("-> contains =: " +
		// node.containsEquality());
		if (!node.containsEquality()) {
		    for (TRSVariable t2 : t.getVariables()) {
			if (!set.contains(t2))
			    set.add(t2);
		    }
		    // set.addAll(t.getVariables());
		}
	    } catch (UnsupportetArithmeticSymbolException e) {
		if (SHOULD_PRINT >= 1)
		    Logger.getLog().writeln("Cond-Term not parsable");
		Logger.getLog().close();
		assert false;
		e.printStackTrace();
	    }
	}

	return set;
    }

    private ArrayList<String> getSetSubtraction(Set<TRSVariable> set1, ArrayList<TRSVariable> set2) {

	Set<TRSVariable> set = new HashSet<>(set2);

	return this.getSetSubtraction(set1, set);
    }

    private ArrayList<String> getSetSubtraction(Set<TRSVariable> set1, Set<TRSVariable> set2) {
	ArrayList<String> list = new ArrayList<>();

	for (TRSVariable var : set1) {
	    if (!set2.contains(var))
		list.add(var.getName());
	}

	return list;
    }

    private ArrayList<RPNNode> normalize(ArrayList<RPNNode> nodes) {
	ArrayList<RPNNode> normalizedNodes = new ArrayList<>();
	for (RPNNode node : nodes) {
	    normalizedNodes.add(node.normalize());
	}

	return normalizedNodes;
    }

    /**
     * This method try's to derive a {@link GeoNonTermArgument} using a
     * SMT-Solver. <br>
     * It can only do so if before the {@link Stem} and {@link Loop} including
     * {@link Loop#computeIterationMatrixAndConstants()} are computed. Otherwise
     * it's most likely to throw a {@link NullPointerException}. <br>
     * If a {@link GeoNonTermArgument} can be found it will be returned.
     * Otherwise this method will return <code>null</code>.
     * 
     * @return a {@link GeoNonTermArgument} or <code>null</code>
     * 
     * @see Stem
     * @see Loop
     * @see SMTFactory
     */
    private GeoNonTermArgument tryDerivingAGNA() {

	if (gna != null)
	    if (gna.validate(loop.getIterationMatrix(), loop.getIterationConstants()))
		return gna;

	int[] eigenvalues = loop.getUpdateMatrix().computeEigenvalues();

	Z3Solver solver = smt.getSolver();

	// Das berechnen vom Point Kriterium
	smt.addAssertion(loop.getIterationMatrix(), smt.createPointCriteriaVector(stem), loop.getIterationConstants());

	// berechnen des ray Kriterium
	char last = 'a';
	int size = stem.getStemVec().size();
	int xCount = 0;
	for (int i = 0; i < eigenvalues.length; i++) {
	    if (i == 0) {
		smt.addAssertion(loop.getIterationMatrix(), smt.createRayCriteriaVector(size, eigenvalues[i], last));
	    } else {
		smt.addAssertion(loop.getIterationMatrix(),
			smt.createRayCriteriaVector(size, eigenvalues[i], last, "X" + xCount++, (char) (last - 1)));
	    }

	    last = (char) (last + 1);
	}

	// Addition
	smt.addAdditionAssertion(size, last);

	// checking
	if (SHOULD_PRINT >= 1)
	    Logger.getLog().writeln("SAT: " + solver.checkSAT().toString());
	if (solver.checkSAT() == YNM.YES) {
	    if (SHOULD_PRINT >= 1)
		Logger.getLog().writeln("MODEl: " + solver.getModel().toString());

	    int[] muArray = this.createMuFromModel(solver.getModel());
	    GNAVector[] Y = this.createYFromModel(solver.getModel(), last);

	    GeoNonTermArgument gna = new GeoNonTermArgument(stem, Y, eigenvalues, muArray);

	    return gna;
	} else
	    return null;
    }

    /**
     * This method computes from a given {@link Model} the different
     * {@link GNAVector} as columns of the Y-matrix of the
     * {@link GeoNonTermAnalysis}. It does so by creating a vector for every
     * char between 'a' and the given last used char. The char for example 'a'
     * is afterwards used and gets indices to a max., which is the result of
     * {@link #countAppearance(Model, String)} and computes the value using
     * {@link #getValueOfVariableWithinModel(Model, String)}. The declarations
     * within the model get sorted by the index. <br>
     * 
     * <pre>
     * [(define-fun x0 2), (define-fun b1 2), (define-fun b0 14), (define-fun a1 0), (define-fun a0 8), (define-fun s1 2), (define-fun s0 22)]
     *         8   14
     * =&gt; [(0),( 2)]
     * </pre>
     * 
     * @param model
     *            the model of the SMT-Problem
     * @param last
     *            the last char used
     * @return an array of {@link GNAVector GNAVectors} to represent the
     *         variable assignment
     */
    private GNAVector[] createYFromModel(Model model, char last) {
	ArrayList<GNAVector> vectors = new ArrayList<>();

	for (int i = (int) 'a'; i < (int) last; i++) {
	    GNAVector vec = new GNAVector(stem.getStemVec().size(), 0);
	    for (int j = 0; j < vec.size(); j++) {
		vec.set(j, this.getValueOfVariableWithinModel(model, (char) i + "" + j));
	    }
	    vectors.add(vec);
	}

	return vectors.toArray(new GNAVector[vectors.size()]);
    }

    /**
     * This method computes from a given {@link Model} the mu's assignments so
     * that the ray criteria is fulfilled. It derives the assignments of the
     * mu's ordered chronologically by the index using
     * {@link #countAppearance(Model, String)} and
     * {@link #getValueOfVariableWithinModel(Model, String)}. <br>
     * Beispiel:
     * 
     * <pre>
     * [(define-fun X0 2), (define-fun b1 2), (define-fun b0 14), (define-fun a1 0), (define-fun a0 8), (define-fun s1 2), (define-fun s0 22)]
     * =&gt; [2]
     * </pre>
     * 
     * The mu's are written as capital X so that the other variables can contain
     * the lower capital letter 'x' .
     * 
     * @param model
     *            the model of the SMT-Problem
     * @return an <code>int</code>-array with the values of the mu's
     */
    private int[] createMuFromModel(Model model) {
	ArrayList<Integer> mu = new ArrayList<>();

	for (int i = 0; i < this.countAppearance(model, "X"); i++) {
	    // Logger.getLog().writeln("x" + i + ": " + m.get((Symbol<?>)
	    // smt.createVar("x" + i).toSMTExp()));
	    mu.add(this.getValueOfVariableWithinModel(model, "X" + i));
	}

	int[] finalMu = new int[mu.size()];
	for (int i = 0; i < finalMu.length; i++)
	    finalMu[i] = mu.get(i);
	return finalMu;
    }

    /**
     * This method takes a {@link Model} and the name of a desired values
     * variable and computes it's assignment within the model using
     * {@link Model#get(Symbol)} and parsing into <code>int</code>.
     * 
     * @param model
     *            the model of the SMT-Problem
     * @param varName
     *            the name of the variable (with index)
     * @return the assigned value of the variable
     */
    private int getValueOfVariableWithinModel(Model model, String varName) {
	// Logger.getLog().writeln(model);
	// Logger.getLog().writeln(varName);

	SMTExpression<?> smtExpression = model.get((Symbol<?>) smt.createVar(varName).toSMTExp());

	if (smtExpression instanceof Call1)
	    return Integer.parseInt("-" + ((Call1) smtExpression).getA0());
	else
	    return Integer.parseInt(smtExpression.toString().trim());
    }

    /**
     * @return The geometric nontermination argument if the analysis was
     *         successful, null else.
     */
    public GeoNonTermArgument getNontermArgument() {
	return this.gna;
    }

    /**
     * This method iterates through the given model and count's the appearances
     * of the given name. <br>
     * NOTE: if the variables would be "aa" and "ab" would be a problem.
     * 
     * @param model
     *            the model of the SMT-Problem
     * @param varName
     *            the name of the variable (without index)
     * @return the number of appearances of the variable
     */
    private int countAppearance(Model model, String varName) {
	int count = 0;

	for (Entry<Symbol<?>, FunctionDefinition> entry : model.getDeclarations().entrySet()) {
	    if (entry.getKey().toString().contains(varName))
		count++;
	}

	return count;
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
		if (SHOULD_PRINT >= 3) {
		    Logger.getLog().writeln("##########");
		    Logger.getLog().writeln(rules[i].getLeft().toString() + " matches " + fs.toString());
		    Logger.getLog().writeln("So Rule Nr." + j + " starts with " + fs.toString());
		    Logger.getLog().writeln("##########");
		}
		break;
	    }
	}

	return j;
    }

}
