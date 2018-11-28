package shopping;

import java.util.ArrayList;
import java.util.Iterator;

public class ShoppingCart {
	private ArrayList<Product> cart;

	public ShoppingCart() {
		this.cart = new ArrayList<Product>();
	}

	public void addProduct(Product paramProduct) {
		this.cart.add(paramProduct);
	}

	public String toString() {
		if (this.cart.size() == 0) {
			return "empty";
		}

		Iterator<Product> localIterator = this.cart.iterator();
		StringBuffer localStringBuffer = new StringBuffer(((Product) localIterator.next()).toString());

		while (localIterator.hasNext()) {
			localStringBuffer.append("\n");
			localStringBuffer.append(((Product) localIterator.next()).toString());
		}

		return localStringBuffer.toString();
	}

	public double getTotalValue() {
		double d = 0.0D;

		for (Product localProduct : this.cart) {
			d += localProduct.getValue();
		}

		return d;
	}
}