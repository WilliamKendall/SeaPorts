/**
 * @author William Kendall
 * @filename SeaPort.java
 * @date 9/17/2017
 * 
 *       This is the Port object. To use this class call its constructor with a Scanner that
 *       contains its type. objects can be added to it if the parent object exist using the add
 *       method. Note: the add method will check all objects for a parent match it can return, using
 *       toString(), a single line of the object or using toString(int i), where int i is an indent
 *       count, formated string of child objects as well as itself
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.tree.DefaultMutableTreeNode;

public class SeaPort extends Thing {
  public ArrayList<Dock> docks;
  public ArrayList<Ship> que; // the list of ships waiting to dock
  public ArrayList<Ship> ships; // a list of all the ships at this port
  public ArrayList<Person> persons; // people with skills at this port

  SeaPort(Scanner sc) {
    // port input
    // port name index parent(null)
    // port <string> <int> <int>
    super(sc);

    docks = new ArrayList<Dock>();
    que = new ArrayList<Ship>();
    ships = new ArrayList<Ship>();
    persons = new ArrayList<Person>();

  }


  public String toString() {
    return "Port: " + super.toString();
  }


  public void moveShipFromQueueToDock(Ship ship, Dock dock) {
    dock.add(ship);
    que.remove(ship);

    ship.parentIndex = dock.index;
    ship.parent = dock;
    
    ship.inDock();
  }


  @Override
  public void getItems(HashMap<Integer, Thing> array) {
    array.put(this.index, this);
    docks.forEach((v) -> {
      v.getItems(array);
    });
    que.forEach((v) -> {
      v.getItems(array);
    });
    ships.forEach((v) -> {
      v.getItems(array);
    });
    persons.forEach((v) -> {
      v.getItems(array);
    });
  }

  @Override
  public void add(Thing o) {
    if (o instanceof Dock) {
      Thing.insert((Dock) o, docks);
      o.parent = this;
      return;
    }
    if (o instanceof Ship) {
      Thing.insert((Ship) o, que);
      Thing.insert((Ship) o, ships);
      o.parent = this;
      return;
    }
    if (o instanceof Person) {
      Thing.insert((Person) o, this.persons);
      o.parent = this;
      return;
    }
  }

  @Override
  public void Sort(Thing.ThingComparator... comp) {
    que.sort(Thing.stackComparators(comp));
  }


  public String toString(int i) {
    String t = indent(i) + toString();
    t += "\n\n" + indent(i + 1) + "List of docks:";
    for (Dock dock : docks)
      t += "\n" + dock.toString(i + 2);
    t += "\n\n" + indent(i + 1) + "Ships in que:";
    for (Ship ship : que)
      t += "\n" + ship.toString(i + 2);
    t += "\n\n" + indent(i + 1) + "All Ships at this port:";
    for (Ship ship : ships)
      t += "\n" + ship.toString(i + 2);
    t += "\n\n" + indent(i + 1) + "People at this port:";
    for (Person person : persons)
      t += "\n" + person.toString(i + 2);

    return t;
  }


  @Override
  public DefaultMutableTreeNode buildTree() {
    DefaultMutableTreeNode dmt = new DefaultMutableTreeNode(this.toString());

    DefaultMutableTreeNode d = new DefaultMutableTreeNode("Docks");
    DefaultMutableTreeNode q = new DefaultMutableTreeNode("Queue");
    DefaultMutableTreeNode s = new DefaultMutableTreeNode("All Ships");
    DefaultMutableTreeNode p = new DefaultMutableTreeNode("Personel");

    dmt.add(p);
    dmt.add(d);
    dmt.add(q);
    dmt.add(s);


    for (Thing t : this.persons)
      p.add(t.buildTree());
    for (Thing t : this.docks)
      d.add(t.buildTree());
    for (Thing t : this.que)
      q.add(t.buildTree());
    for (Thing t : this.ships)
      s.add(t.buildTree());


    return dmt;
  }


  @Override
  public void delete(Thing o) {
    for (Thing t : this.persons)
      t.delete(o);
    for (Thing t : this.docks)
      t.delete(o);
    for (Thing t : this.que)
      t.delete(o);
    for (Thing t : this.ships)
      t.delete(o);

    this.persons.remove(o);
    this.docks.remove(o);
    this.que.remove(o);
    this.ships.remove(o);

  }


  @Override
  public void update() {
    for (Dock s : this.docks) {
      if (s.ship != null) {
        if (s.ship.jobs.isEmpty()) {
          if (!this.que.isEmpty())
            this.moveShipFromQueueToDock(this.que.get(0), s);
          else
            s.ship = null;
        }
      }
    }
  }



}
