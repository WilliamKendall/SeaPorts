/**
 * @author William Kendall
 * @filename PassengerShip.java
 * @date 9/17/2017
 * 
 *       This is the PassengerShip object. most functions for this class are handled by the ship
 *       class. its constructor extends the scanner for extra data. as well as the toString method
 *       returns extra data about this object
 */

import java.util.Scanner;

public class PassengerShip extends Ship {
  int numberOfOccupiedRooms;
  int numberOfPassengers;
  int numberOfRooms;

  PassengerShip(Scanner sc) {
    // pship name index parent(dock/port) weight length width draft numPassengers numRooms
    // numOccupied
    // pship <string> <int> <int> <double> <double> <double> <double> <int> <int> <int>
    super(sc);
    if (sc.hasNextInt())
      numberOfPassengers = sc.nextInt();
    if (sc.hasNextInt())
      numberOfRooms = sc.nextInt();
    if (sc.hasNextInt())
      numberOfOccupiedRooms = sc.nextInt();

  }

  public String toString() {
    return "Passenger" + super.toString() + "\tPassengers: " + Integer.toString(numberOfPassengers)
        + "\tRooms: " + Integer.toString(numberOfRooms) + "\tOccupied Rooms: "
        + Integer.toString(numberOfOccupiedRooms);
  }


}
