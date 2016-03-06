package hu.vesz.paintshop;

public enum Finish {
	
	GLOSS, 
	MATTE;
	
	public static Finish parseFinish(String abbreviation) throws Exception {
		switch (abbreviation) {
		case "M":
			return MATTE;
		case "G":			
			return GLOSS;
		default:
			throw new Exception("Invalid abbreviation for Finish: " + abbreviation);
		}
	}

	public String getAbbreviation() {
		return toString().substring(0,1);
	}
}
