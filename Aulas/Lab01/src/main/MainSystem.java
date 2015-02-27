package main;

import java.util.Vector;

public class MainSystem {
	Vector<Vehicle> vehicles = new Vector<Vehicle>();
	
	public int register(String plate, String owner) {
		Vehicle v1 = new Vehicle(plate, owner);
		for (int i = 0; i < vehicles.size(); i++) {
			if (vehicles.get(i).getPlate() == v1.getPlate())
				return -1;
		}
		vehicles.add(v1);
		return vehicles.size();
	}
	
	public String lookup(String plate) {
		for ( int i = 0; i < vehicles.size(); i++) {
			if (vehicles.get(i).getPlate() == plate) 
				return vehicles.get(i).getOwner();
		}
		return "NOT_FOUND";
	}
}
