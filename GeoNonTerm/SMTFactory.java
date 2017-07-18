package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import java.math.BigInteger;
import java.util.ArrayList;

import aprove.Framework.BasicStructures.Arithmetic.ArithmeticOperationType;
import aprove.Framework.BasicStructures.Arithmetic.Integer.FunctionalIntegerExpression;
import aprove.Framework.BasicStructures.Arithmetic.Integer.IntegerRelationType;
import aprove.Framework.BasicStructures.Arithmetic.Integer.PlainIntegerConstant;
import aprove.Framework.BasicStructures.Arithmetic.Integer.PlainIntegerOperation;
import aprove.Framework.BasicStructures.Arithmetic.Integer.PlainIntegerRelation;
import aprove.Framework.BasicStructures.Arithmetic.Integer.PlainIntegerVariable;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNConstant;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNFunctionSymbol;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNNode;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNVariable;
import aprove.Framework.SMT.SMTLIBLogic;
import aprove.Framework.SMT.Solver.Factories.Z3ExtSolverFactory;
import aprove.Framework.SMT.Solver.Z3.Z3Solver;
import aprove.Strategies.Abortions.AbortionFactory;

/**
 * The {@link SMTFactory} contains the functions and instances that are
 * necessary to derive a {@link GeoNonTermArgument} within the
 * {@link GeoNonTermAnalysis}. It mainly has two different kinds of methods:
 * <ol>
 * <li>methods to create a {@link FunctionalIntegerExpression}
 * ({@link PlainIntegerVariable}, {@link PlainIntegerConstant},
 * {@link PlainIntegerOperation} {@link PlainIntegerRelation}</li>
 * <li>methods to add assertions to a {@link Z3Solver}</li>
 * </ol>
 * 
 * @author Timo Bergerbusch
 *
 */
public class SMTFactory {

    /**
     * the {@link Z3Solver} that solves the (in-)equations
     */
    private Z3Solver solver;

    /**
     * This method creates a new instance of a {@link Z3Solver} and stores it in
     * {@link #solver}
     * 
     * @return new freshly generated {@link Z3Solver}
     */
    public Z3Solver createNewSolver() {
	return solver = new Z3ExtSolverFactory().getSMTSolver(SMTLIBLogic.QF_NIA, AbortionFactory.create());
    }

    /**
     * This method is used to access the instance of the {@link Z3Solver} and
     * creates a new {@link Z3Solver}, if {@link #solver} is not initialized
     * yet.
     * 
     * @return the (new) {@link Z3Solver}-instance
     */
    public Z3Solver getSolver() {
	if (solver == null)
	    return this.createNewSolver();
	else
	    return solver;
    }

    /**
     * This method creates a new {@link PlainIntegerVariable} with a given name.
     * 
     * @param name
     *            the name of the variable
     * @return the variable as {@link PlainIntegerVariable}
     */
    public PlainIntegerVariable createVar(String name) {
	return new PlainIntegerVariable(name);
    }

    /**
     * This method creates a new {@link PlainIntegerConstant} with a given
     * <code>int</code>-value.
     * 
     * @param value
     *            the value of the constant
     * @return the {@link PlainIntegerConstant} with given value
     */
    public PlainIntegerConstant createConst(int value) {
	return new PlainIntegerConstant(new BigInteger(value + ""));
    }

    /**
     * This method creates a new {@link PlainIntegerOperation} for two
     * {@link FunctionalIntegerExpression}'s connected with a
     * {@link ArithmeticOperationType}.
     * 
     * @param type
     *            the operation type
     * @param left
     *            the left side of the operation
     * @param right
     *            the right side of the operation
     * @return a new {@link PlainIntegerOperation} connecting left and right
     *         with the given type
     * 
     * @see ArithmeticOperationType
     */
    public PlainIntegerOperation createOperation(ArithmeticOperationType type, FunctionalIntegerExpression left,
	    FunctionalIntegerExpression right) {
	return new PlainIntegerOperation(type, left, right);
    }

    /**
     * This method creates a new {@link PlainIntegerRelation} for two
     * {@link FunctionalIntegerExpression}'s connected with a
     * {@link IntegerRelationType}.
     * 
     * @param type
     *            the relation type
     * @param left
     *            the left side of the relation
     * @param right
     *            the right side of the relation
     * @return a new {@link PlainIntegerRelation} relating left and right with
     *         the given type
     * 
     * @see IntegerRelationType
     */
    public PlainIntegerRelation createRule(IntegerRelationType type, FunctionalIntegerExpression left,
	    FunctionalIntegerExpression right) {
	return new PlainIntegerRelation(type, left, right);
    }

    /**
     * This method takes a {@link RPNNode} and iterates over it's nodes to
     * derive a equivalent {@link FunctionalIntegerExpression}.
     * 
     * @param root
     *            the {@link RPNNode} that should be converted
     * @return the {@link RPNNode root} as an equivalent
     *         {@link FunctionalIntegerExpression}
     * 
     * @see RPNNode
     * @see FunctionalIntegerExpression
     */
    public FunctionalIntegerExpression parseRPNTreeToSMTRule(RPNNode root) {
	if (root instanceof RPNConstant)
	    return this.createConst(((RPNConstant) root).getConstantTerm());
	else if (root instanceof RPNVariable)
	    return this.createVar(((RPNVariable) root).getValue());
	else if (root instanceof RPNFunctionSymbol) {
	    RPNFunctionSymbol fs = (RPNFunctionSymbol) root;

	    FunctionalIntegerExpression leftExpr = this.parseRPNTreeToSMTRule(fs.getLeft());
	    FunctionalIntegerExpression rightExpr = this.parseRPNTreeToSMTRule(fs.getRight());

	    switch (fs.getFunctionSymbol()) {
	    case PLUS:
		return this.createOperation(ArithmeticOperationType.ADD, leftExpr, rightExpr);
	    case TIMES:
		return this.createOperation(ArithmeticOperationType.MUL, leftExpr, rightExpr);
	    default:
		Logger.getLog().writeln("ÜBERHAUPT NICHT GUT");
		break;
	    }

	}
	return null;

    }

    /**
     * This method generates a {@link GNAVariableVector} for the point criteria
     * of the {@link GeoNonTermArgument}. <br>
     * It does so by taking the {@link Stem} and building a {@link GNAVector} of
     * size <code>2*n</code> where the stem vector is of size <code>n</code>,
     * setting the first <code>n</code>-entry's to the {@link Stem stem vector}
     * entry's and the second <code>n</code> entry's as the entry's of the
     * {@link Stem stem vector} + s_i for 0&lt;=i&lt;=<code>n</code>. <br>
     * Example: for the stem vector (10 2)^T the result would be (10 2 10+s_0
     * 2+s_1)^T
     * 
     * @param stem
     *            the {@link Stem} of the {@link GeoNonTermAnalysis}
     * @return the {@link GNAVariableVector} for the point criteria
     * 
     * @see Stem
     * @see Loop
     */
    public GNAVariableVector createPointCriteriaVector(Stem stem) {

	GNAVector stemVec = stem.getStemVec();
	GNAVariableVector vec = new GNAVariableVector(2 * stemVec.size());
	int half = stemVec.size();
	for (int i = 0; i < stemVec.size(); i++) {
	    vec.setEntry(i, stemVec.get(i) + "");
	    vec.setEntry(i + half, stemVec.get(i) + "+s" + i);
	}

	return vec;
    }

    /**
     * This method creates a {@link GNAVariableVector} for the ray criteria of
     * the {@link GeoNonTermAnalysis}. <br>
     * Only for the very first ray criteria check this method is called, since
     * otherwise we would have to consider mu and the previous y.<br>
     * Example: size = 2, lambda = 3 and varName = 'a' the result would be (a0
     * a1 3*a0 3*a1).
     * 
     * @param size
     *            the size of the vector
     * @param lambda
     *            the eigenvalue of this entry
     * @param varName
     *            the name this variable should have
     * @return a {@link GNAVariableVector} of the mentioned form
     * 
     * @see #createRayCriteriaVector(int, int, char, String, char)
     */
    public GNAVariableVector createRayCriteriaVector(int size, int lambda, char varName) {
	GNAVariableVector vec = new GNAVariableVector(2 * size);

	for (int i = 0; i < size; i++) {
	    vec.setEntry(i, varName + "" + i);
	    vec.setEntry(i + size, lambda + "*" + varName + "" + i);
	}

	return vec;
    }

    /**
     * This method creates a {@link GNAVariableVector} for the ray criteria of
     * the {@link GeoNonTermAnalysis}. <br>
     * Every except the first ray criteria check calls this method. <br>
     * Example: size = 2, lambda = 2, varName = 'b' mu="X0" and addVarName = 'a'
     * the result would be (b0 b1 2*b0+X0*a0 2*b1+X0*a1).
     * 
     * @param size
     *            the size of the vector
     * @param lambda
     *            the eigenvalue of this entry
     * @param varName
     *            the name this variable should have
     * @param mu
     *            the mu-coefficient of the previous variable
     * @param addVarName
     *            the name of the previous variable
     * @return a {@link GNAVariableVector} of the mentioned form
     */
    public GNAVariableVector createRayCriteriaVector(int size, int lambda, char varName, String mu, char addVarName) {
	GNAVariableVector vec = new GNAVariableVector(2 * size);

	for (int i = 0; i < size; i++) {
	    vec.setEntry(i, varName + "" + i);
	    vec.setEntry(i + size, lambda + "*" + varName + "" + i + "+" + mu + "*" + addVarName + "" + i);
	}

	return vec;
    }

    /**
     * This method creates the addition relation between the ray criteria's and
     * the point criteria, so that the system is consistent. <br>
     * It does so by using {@link #recursiveAdd(ArrayList)} starting with 'a' to
     * last and adds the resulting {@link FunctionalIntegerExpression}'s to the
     * {@link #solver}. Example: size=2 and last = 'b' the method creates
     * a0+b0=s0 and a1+b1=s1 .
     * 
     * @param size
     *            the size of the variable space
     * @param last
     *            the last used variable name
     */
    public void addAdditionAssertion(int size, char last) {

	ArrayList<String> list = new ArrayList<>();
	for (int i = 0; i < size; i++) {
	    for (int j = (int) ('a'); j < (int) last; j++) {
		list.add(((char) j) + "" + i);
	    }
	    solver.addAssertion(this
		    .createRule(IntegerRelationType.EQ, this.recursiveAdd(list), this.createVar("s" + i)).toSMTExp());
	    list.clear();
	}
    }

    /**
     * This method recursively creates {@link FunctionalIntegerExpression} with
     * the {@link ArithmeticOperationType#ADD} for all elements of the given
     * list as {@link PlainIntegerVariable} using {@link #createVar(String)}.
     * 
     * @param list
     *            a list of variables
     * @return one {@link FunctionalIntegerExpression} containing the addition
     *         of all list elements
     */
    public FunctionalIntegerExpression recursiveAdd(ArrayList<String> list) {
	if (list.size() < 1)
	    return this.createConst(0);
	else if (list.size() == 1)
	    return this.createVar(list.get(0));
	else {
	    String s = list.get(0);
	    list.remove(0);
	    return this.createOperation(ArithmeticOperationType.ADD, this.createVar(s), this.recursiveAdd(list));
	}
    }

    /**
     * This method creates a new assertion for the {@link #solver} using
     * {@link #addAssertion(GNAMatrix, GNAVariableVector, GNAVector)} and a
     * GNAVector of the desired size only containing 0's.
     * 
     * @param matrix
     *            the {@link Loop#getIterationMatrix()}
     * @param vec
     *            the {@link GNAVariableVector} that should fulfill this
     *            assertion
     */
    public void addAssertion(GNAMatrix matrix, GNAVariableVector vec) {
	this.addAssertion(matrix, vec, new GNAVector(matrix.rowSize(), 0));
    }

    /**
     * This method create new assertions for the {@link #solver} by multiplying
     * the given matrix with the given {@link GNAVariableVector} and creating
     * one assertion per row as the result of the multiplication &lt;= the
     * corresponding index of the given {@link GNAVector constants vector}.
     * Example:
     * 
     * <pre>
     *                                  ( m00 m01)   (   a0 )       ( c0 )
     * matrix*vec &lt;= cons  &lt;=&gt; ( m10 m11) * ( l*a0 ) &lt;= ( c1 )
     * 
     * results in the two assertions:
     * 		m00*a0 + m01*l*a0 &lt;= c0
     *          m10*a0 + m11*l*a1 &lt;= c1
     * </pre>
     * 
     * It does so by using {@link #parseRPNTreeToSMTRule(RPNNode)} after
     * computing the {@link RPNNode}'s using
     * {@link GNAMatrix#mult(GNAVariableVector)}.
     * 
     * @param matrix
     *            the {@link GNAMatrix}
     * @param vec
     *            the {@link GNAVariableVector}
     * @param cons
     *            the {@link GNAVector}
     * 
     * @see #parseRPNTreeToSMTRule(RPNNode)
     * @see GNAMatrix#mult(GNAVariableVector)
     */
    public void addAssertion(GNAMatrix matrix, GNAVariableVector vec, GNAVector cons) {
	RPNNode[] nodes = matrix.mult(vec);
	FunctionalIntegerExpression exp;
	for (int i = 0; i < nodes.length; i++) {
	    exp = this.parseRPNTreeToSMTRule(nodes[i]);
	    solver.addAssertion(this.createRule(IntegerRelationType.LE, exp, this.createConst(cons.get(i))).toSMTExp());
	}
    }

}
