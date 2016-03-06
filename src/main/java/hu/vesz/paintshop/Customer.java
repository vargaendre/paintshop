package hu.vesz.paintshop;

import java.util.ArrayList;
import java.util.List;

public class Customer {

	private List<Paint> paintPreferences = new ArrayList<>();
	
	public void addPaintPreference(int color, Finish finish) {
		Paint paint = new Paint(color, finish);
		paintPreferences.add(paint);
	}
	
	public List<Paint> getPaintPreferences() {
		return paintPreferences;
	}
	
	public Integer numOfPaintPreferences() {
		return paintPreferences.size();
	}
}
