Stem teststem = new Stem(null);
teststem.setStemVec(new VecInt(new int[] { 3, 1 }));
GeoNonTermArgument gna = new GeoNonTermArgument(teststem,
new VecInt[] { new VecInt(new int[] { 4, 0 }), new VecInt(new int[] { 3, 1 }) }, new int[] { 3, 2 },
new int[] { 1 });

UpdateMatrix g = new UpdateMatrix(1, 2);
g.setEntry(0, 0, -1);
g.setEntry(0, 1, -1);
VecInt gConst = new VecInt(new int[] { -4 });
UpdateMatrix m = new UpdateMatrix(2, 2);
m.setEntry(0, 0, 3);
m.setEntry(0, 1, 1);
m.setEntry(1, 1, 2);
VecInt mConst = new VecInt(new int[] { 0, 0 });
Loop testloop = new Loop();
testloop.setGuardUpdates(g);
testloop.setGuardConstants(gConst);
testloop.setUpdateMatrix(m);
testloop.setUpdateConstants(mConst);
testloop.computeIterationMatrixAndConstants();

LOG.writeln(Loop.getSystemAsString(testloop.getIterationMatrix(),
new String[] { "x0_0", "x0_1", "x1_0", "x1_1" }, testloop.getIterationConstants()));
LOG.writeln(
"Point: " + gna.checkPointCriteria(testloop.getIterationMatrix(), testloop.getIterationConstants()));
LOG.writeln("Ray: " + gna.checkRayCriteria(testloop.getIterationMatrix()));
LOG.writeln("Overall: " + gna.validate(testloop.getIterationMatrix(), testloop.getIterationConstants()));