try {
	FileWriter fw = new FileWriter("/home/timo/Downloads/ausgabe.txt");
	BufferedWriter bw = new BufferedWriter(fw);
	
	bw.write(result.toString());
	
	bw.close();
	fw.close();
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}