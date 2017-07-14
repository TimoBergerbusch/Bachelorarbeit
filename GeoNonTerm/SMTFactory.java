package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import java.math.BigInteger;

import aprove.Framework.BasicStructures.Arithmetic.ArithmeticOperationType;
import aprove.Framework.BasicStructures.Arithmetic.Integer.FunctionalIntegerExpression;
import aprove.Framework.BasicStructures.Arithmetic.Integer.IntegerRelationType;
import aprove.Framework.BasicStructures.Arithmetic.Integer.PlainIntegerConstant;
import aprove.Framework.BasicStructures.Arithmetic.Integer.PlainIntegerOperation;
import aprove.Framework.BasicStructures.Arithmetic.Integer.PlainIntegerRelation;
import aprove.Framework.BasicStructures.Arithmetic.Integer.PlainIntegerVariable;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.ArithmeticSymbol;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNConstant;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNFunctionSymbol;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNNode;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNVariable;

public class SMTFactory {

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
	// TODO: für eine nested Variable z.B. 2*s1 bekommt man derzeit falsche
	// Ergebnisse
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
}
