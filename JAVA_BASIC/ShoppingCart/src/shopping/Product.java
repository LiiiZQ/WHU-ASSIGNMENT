package shopping;

public class Product {
	private String name;
	private int quantity;
	private double price;

	public Product(String paramString, int paramInt, double paramDouble) {
		this.name = paramString;
		this.quantity = paramInt;
		this.price = paramDouble;
	}

	public String getName() {
		return this.name;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public double getPrice() {
		return this.price;
	}

	public String toString() {
		return getName() + "_" + getQuantity() + "_" + getPrice();
	}

	public double getValue() {
		return getQuantity() * getPrice();
	}
}