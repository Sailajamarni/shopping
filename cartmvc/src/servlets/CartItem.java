package servlets;

public class CartItem {

    public CartItem(int prodId, int quantity) {
        pid = prodId;
        this.quantity = quantity;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int prodId) {
        pid = prodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getHsnCode() {
		return hsnCode;
	}

	public void setHsnCode(int hsnCode) {
		this.hsnCode = hsnCode;
	}

	private int pid;
    private int quantity;
    private double priceInclusive;
    private int hsnCode;
	public double getPriceInclusive() {
		return priceInclusive;
	}

	public CartItem(int pid, int quantity, double priceInclusive, int hsnCode) {
		this.pid = pid;
		this.quantity = quantity;
		this.priceInclusive = priceInclusive;
	}

	public void setPriceInclusive(double priceInclusive) {
		this.priceInclusive = priceInclusive;
	}
}
