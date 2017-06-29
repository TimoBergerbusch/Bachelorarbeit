package aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree;

import aprove.DPFramework.BasicStructures.TRSCompoundTerm;
import aprove.DPFramework.BasicStructures.TRSConstantTerm;
import aprove.DPFramework.BasicStructures.TRSTerm;
import aprove.DPFramework.BasicStructures.TRSVariable;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.GeoNonTermAnalysis;
import aprove.Framework.IntTRS.Nonterm.GeoNonTerm.ReversePolishNotationTree.RPNFunctionSymbol.ArithmeticSymbol;

public class RPNTreeParser {

	public RPNNode parseSetToTree(TRSTerm rightSide) throws UnsupportetArithmeticSymbolException {

		if (rightSide instanceof TRSVariable) {
			return new RPNVariable(rightSide.toString());
		} else if (rightSide instanceof TRSConstantTerm) {
			return new RPNNumber(Integer.parseInt(rightSide.toString()));
		} else if (rightSide instanceof TRSCompoundTerm) {
			TRSCompoundTerm compountTerm = ((TRSCompoundTerm) rightSide);
			ArithmeticSymbol symbol;
			if (compountTerm.getFunctionSymbol().toString().equals("+_2"))
				symbol = ArithmeticSymbol.PLUS;
			else if (compountTerm.getFunctionSymbol().toString().equals("-_2"))
				symbol = ArithmeticSymbol.MINUS;
			else if (compountTerm.getFunctionSymbol().toString().equals("*_2"))
				symbol = ArithmeticSymbol.TIMES;
			else
				throw new UnsupportetArithmeticSymbolException(compountTerm.getFunctionSymbol().toString());

			RPNNode left = parseSetToTree(compountTerm.getArgument(0));
			RPNNode right = parseSetToTree(compountTerm.getArgument(1));

			return new RPNFunctionSymbol(symbol, left, right);
		}

//		GeoNonTermAnalysis.LOG.endClassOutput("RPNStringParser");
		GeoNonTermAnalysis.LOG.writeln("NICHT GUT");
		return null;
	}
}