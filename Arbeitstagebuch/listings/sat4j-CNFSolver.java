import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.Reader;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;

//eine Testklasse fuer SAT4J
public class main {

	public static void main(final String[] args) { 
		//Anlegen eines neuen Solvers
		ISolver solver = SolverFactory.newDefault();
		
	    solver.setTimeout(300);	    
	    //Anlegen eines Readers
	    Reader reader = new DimacsReader(solver);
	    
	    try{
	    	//Auslesen des Programms aus der Datei
	    	IProblem problem = reader.parseInstance("src/input.txt");
	    	
	    	//Pruefen der Erfuellbarkeit
	    	if (problem.isSatisfiable()) {
	    		//Wenn erfuellbar ein Model angeben
                System.out.println("Satisfiable !");
                System.out.println(reader.decode(problem.model()));
            } else {
            	//Ausgeben dass es nicht Erfuellbar ist
                System.out.println("Unsatisfiable !");
            }
	    } catch (Exception e){
	    	//Fangen aller Exceptions
	    	e.printStackTrace();
	    }
    } 
	
}
