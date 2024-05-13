package servlets;

public class Coupon {
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private String code;
    private double value;
    private double minValue;
    private int availableCount;

    public int getAvailableCount() {
		return availableCount;
	}

	public void setAvailableCount(int availableCount) {
		this.availableCount = availableCount;
	}

	public Coupon(String code, double value, double minValue, int availableCount) {
        this.code = code;
        this.value = value;
        this.minValue = minValue;
        this.availableCount=availableCount;
    }

    public Coupon() {
		// TODO Auto-generated constructor stub
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	// Getter methods
    public String getCode() {
        return code;
    }

    public double getValue() {
        return value;
    }

    public double getMinValue() {
        return minValue;
    }
    
    public void decrementAvailableCount() {
        if (availableCount > 0) {
            availableCount--;
        }
    }
}
