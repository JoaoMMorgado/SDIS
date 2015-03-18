package main;

public class Vehicle {
	String plate;
	String owner;
	
	public Vehicle(String plate, String owner) {
		super();
		this.plate = plate;
		this.owner = owner;
	}
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
}
