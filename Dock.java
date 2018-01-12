/**
 * @author William Kendall
 * @filename Dock.java
 * @date 9/17/2017
 * 
 *       This is the Dock object. To use this class call its constructor with a Scanner that
 *       contains its type. objects can be added to it if the parent object exist using the add
 *       method. Note: the add method will check all objects for a parent match it can return, using
 *       toString(), a single line of the object or using toString(int i), where int i is an indent
 *       count, formated string of child objects as well as itself
 */

import java.util.HashMap;
import java.util.Scanner;

import javax.swing.tree.DefaultMutableTreeNode;

public class Dock extends Thing {
  public Ship ship;

  public Dock(Scanner sc) {
    // dock input
    // dock name index parent(port)
    // dock <string> <int> <int>
    super(sc);
  }

  @Override
  public int compareTo(Thing o) {
    return this.name.compareToIgnoreCase(o.name);
  }


  public String toString() {
    return "Dock: " + super.toString();
  }

  public String toString(int i) {
    String t = indent(i) + toString();
    t += "\n" + indent(i + 1) + "Docked Ship(s):";
    if (this.ship != null)
      t += "\n" + ship.toString(i + 1);
    return t;
  }


  @Override
  public void add(Thing o) {
    if (o instanceof Ship) {
      ship = (Ship) o;
      ship.parent = this;
    }
  }

  @Override
  public void getItems(HashMap<Integer, Thing> array) {
    array.put(this.index, (Dock) this);
    if (this.ship != null)
      this.ship.getItems(array);
  }


  @Override
  public DefaultMutableTreeNode buildTree() {
    DefaultMutableTreeNode dmt = new DefaultMutableTreeNode(this.toString());
    if (this.ship != null) {
      DefaultMutableTreeNode s = this.ship.buildTree();
      dmt.add(s);
    }
    return dmt;
  }

  @Override
  public void delete(Thing o) {
    if (this.ship != null && this.ship.equals(o))
      this.ship = null;

  }

  @Override
  public void update() {}



}
