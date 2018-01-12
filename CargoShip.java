/**
 * @author William Kendall
 * @filename CargoShip.java
 * @date 9/17/2017
 * 
 *       This is the CargoShip object. most functions for this class are handled by the ship class.
 *       its constructor extends the scanner for extra data. as well as the toString method returns
 *       extra data about this object
 */

import java.util.Scanner;
public class CargoShip extends Ship {
  double cargoValue;
  double cargoVolume;
  double cargoWeight;

  CargoShip(Scanner sc) {
    super(sc);
    if (sc.hasNextDouble())
      cargoWeight = sc.nextDouble();
    if (sc.hasNextDouble())
      cargoVolume = sc.nextDouble();
    if (sc.hasNextDouble())
      cargoValue = sc.nextDouble();
  }

  public String toString() {
    return "Cargo" + super.toString() + "\tCargoValue: " + Double.toString(cargoValue)
        + "\tCargoVolume: " + Double.toString(cargoVolume) + "\tCargoWeight: "
        + Double.toString(cargoWeight);
  }

}
