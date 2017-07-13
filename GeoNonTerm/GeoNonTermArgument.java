package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

public class GeoNonTermArgument {

    private Stem x;

    private GNAVector[] y;

    private int[] lambda;

    private int[] mu;

    public GeoNonTermArgument(Stem x, GNAVector[] y, int[] lambda, int[] mu) {
	assert y.length == lambda.length;
	assert mu.length == y.length - 1;

	this.x = x;
	this.y = y;
	this.lambda = lambda;
	this.mu = mu;
    }

    public boolean validate(GNAMatrix a, GNAVector b) {

	boolean bool = true;

	// since criteria 1 and 2 are trivial to check, because of the
	// circumstances I don't check them
	bool &= this.checkPointCriteria(a, b); // check point
	bool &= this.checkRayCriteria(a); // check ray

	return bool;
    }

    public boolean checkPointCriteria(GNAMatrix a, GNAVector b) {

	GNAVector sum = y[0];
	for (int i = 1; i < y.length; i++) {
	    // sum = GeoNonTermAnalysis.add(sum, y[i]);
	    sum = sum.add(y[i]);
	}

	GNAVector vec = new GNAVector();

	vec.pushAll(x.getStemVec());
	// vec.pushAll(GeoNonTermAnalysis.add(x.getStemVec(), sum));
	vec.pushAll(sum.add(x.getStemVec()));

	if (!checkHolding(a, vec, b)) {
	    Logger.getLog().writeln("PointCriteria doesn't hold");
	    return false;
	}

	return true;
    }

    public boolean checkRayCriteria(GNAMatrix a) {
	GNAVector vec = new GNAVector();
	GNAVector nullen = new GNAVector();
	for (int entry = 0; entry < a.rowSize(); entry++) {
	    nullen.push(0);
	}

	for (int i = 0; i < y.length; i++) {

	    vec.pushAll(y[i]);
	    if (i == 0)
		// vec.pushAll(GeoNonTermAnalysis.mult(y[i], lambda[i]));
		vec.pushAll(y[i].mult(lambda[i]));
	    else
		// vec.pushAll(GeoNonTermAnalysis.add(GeoNonTermAnalysis.mult(y[i],
		// lambda[i]),
		// GeoNonTermAnalysis.mult(y[i - 1], mu[i - 1])));
		vec.pushAll(y[i].mult(lambda[i]).add(y[i - 1].mult(mu[i - 1])));

	    if (!this.checkHolding(a, vec, nullen)) {
		Logger.getLog().writeln("RayCriteria of index: " + i + "doesn't hold!");
		Logger.getLog().writeln(Loop.getSystemAsString(a, vec, nullen));
		return false;
	    }
	    vec.clear();
	}

	return true;
    }

    private boolean checkHolding(GNAMatrix a, GNAVector vec, GNAVector b) {
	// vec = UpdateMatrix.mult(a, vec);
	vec = a.mult(vec);

	return vec.lessOrEqualThan(b);
    }
}
