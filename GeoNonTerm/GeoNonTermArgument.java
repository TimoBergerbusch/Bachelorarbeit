package aprove.Framework.IntTRS.Nonterm.GeoNonTerm;

import org.sat4j.core.VecInt;

public class GeoNonTermArgument {

    private Stem x;

    private VecInt[] y;

    private int[] lambda;

    private int[] mu;

    public GeoNonTermArgument(Stem x, VecInt[] y, int[] lambda, int[] mu) {
	assert y.length == lambda.length;
	assert mu.length == y.length - 1;

	this.x = x;
	this.y = y;
	this.lambda = lambda;
	this.mu = mu;
    }

    public boolean validate(UpdateMatrix a, VecInt b) {

	boolean bool = true;

	// since criteria 1 and 2 are trivial to check, because of the
	// circumstances I don't check them
	bool &= this.checkPointCriteria(a, b); // check point
	bool &= this.checkRayCriteria(a); // check ray

	return bool;
    }

    public boolean checkPointCriteria(UpdateMatrix a, VecInt b) {

	VecInt sum = y[0];
	for (int i = 1; i < y.length; i++) {
	    sum = GeoNonTermAnalysis.add(sum, y[i]);
	}

	VecInt vec = new VecInt();

	vec.pushAll(x.getStemVec());
	vec.pushAll(GeoNonTermAnalysis.add(x.getStemVec(), sum));

	if(!checkHolding(a, vec, b)){
	    GeoNonTermAnalysis.LOG.writeln("PointCriteria doesn't hold");
	    return false;
	}

	return true;
    }

    public boolean checkRayCriteria(UpdateMatrix a) {
	VecInt vec = new VecInt();
	VecInt nullen = new VecInt();
	for (int entry = 0; entry < a.rowSize(); entry++) {
	    nullen.push(0);
	}

	for (int i = 0; i < y.length; i++) {

	    vec.pushAll(y[i]);
	    if (i == 0)
		vec.pushAll(GeoNonTermAnalysis.mult(y[i], lambda[i]));
	    else
		vec.pushAll(GeoNonTermAnalysis.add(GeoNonTermAnalysis.mult(y[i], lambda[i]),
			GeoNonTermAnalysis.mult(y[i - 1], mu[i - 1])));

	    if (!this.checkHolding(a, vec, nullen)) {
		GeoNonTermAnalysis.LOG.writeln("RayCriteria of index: " + i + "doesn't hold!");
		GeoNonTermAnalysis.LOG.writeln(Loop.getSystemAsString(a, vec, nullen));
		return false;
	    }
	    vec.clear();
	}

	return true;
    }

    private boolean checkHolding(UpdateMatrix a, VecInt vec, VecInt b) {
	vec = UpdateMatrix.mult(a, vec);

	return GeoNonTermAnalysis.lessOrEqualThan(vec, b);
    }
}
