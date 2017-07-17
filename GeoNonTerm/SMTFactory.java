package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import java.math.BigInteger;
import java.util.ArrayList;

import org.sat4j.minisat.SolverFactory;

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

public class SMTFactory {

    private Z3Solver solver;

    public Z3Solver createNewSolver() {
	return solver = new Z3ExtSolverFactory().getSMTSolver(SMTLIBLogic.QF_NIA, AbortionFactory.create());
    }

    public Z3Solver getSolver() {
	if (solver == null)
	    return this.createNewSolver();
	else
	    return solver;
    }

    public PlainIntegerVariable createVar(String name) {
	return new PlainIntegerVariable(name);
    }

    public PlainIntegerConstant createConst(int value) {
	return new PlainIntegerConstant(new BigInteger(value + ""));
    }

    public PlainIntegerOperation createOperation(ArithmeticOperationType type, FunctionalIntegerExpression left,
	    FunctionalIntegerExpression right) {
	return new PlainIntegerOperation(type, left, right);
    }

    public PlainIntegerRelation createRule(IntegerRelationType type, FunctionalIntegerExpression left,
	    FunctionalIntegerExpression right) {
	return new PlainIntegerRelation(type, left, right);
    }

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

    public GNAVariableVector createRayCriteriaVec(int size, int lambda, char varName) {
	GNAVariableVector vec = new GNAVariableVector(2 * size);

	for (int i = 0; i < size; i++) {
	    vec.setEntry(i, varName + "" + i);
	    vec.setEntry(i + size, lambda + "*" + varName + "" + i);
	}

	return vec;
    }

    public GNAVariableVector createRayCriteriaVec(int size, int lambda, char varName, String mu, char addVarName) {
	GNAVariableVector vec = new GNAVariableVector(2 * size);

	for (int i = 0; i < size; i++) {
	    vec.setEntry(i, varName + "" + i);
	    vec.setEntry(i + size, lambda + "*" + varName + "" + i + "+" + mu + "*" + addVarName + "" + i);
	}

	return vec;
    }

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

    public void addAssertion(GNAVariableVector vec, GNAMatrix matrix) {
	this.addAssertion(matrix, vec, new GNAVector(matrix.rowSize(), 0));
    }

    public void addAssertion(GNAMatrix matrix, GNAVariableVector vec, GNAVector cons) {
	RPNNode[] nodes = matrix.mult(vec);
	FunctionalIntegerExpression exp;
	for (int i = 0; i < nodes.length; i++) {
	    exp = this.parseRPNTreeToSMTRule(nodes[i]);
	    solver.addAssertion(this.createRule(IntegerRelationType.LE, exp, this.createConst(cons.get(i))).toSMTExp());
	}
    }

    public FunctionalIntegerExpression recursiveAdd(ArrayList<String> list) {
	if (list.size() == 1)
	    return this.createVar(list.get(0));
	else {
	    String s = list.get(0);
	    list.remove(0);
	    return this.createOperation(ArithmeticOperationType.ADD, this.createVar(s), this.recursiveAdd(list));
	}
    }

}
