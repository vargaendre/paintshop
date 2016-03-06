package hu.vesz.paintshop;

public class Paint {

	private Integer color;
	private Finish finish;
	
	public Paint(Integer color, Finish finish) {
		this.color = color;
		this.finish = finish;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public Finish getFinish() {
		return finish;
	}

	public void setFinish(Finish finish) {
		this.finish = finish;
	}
}
