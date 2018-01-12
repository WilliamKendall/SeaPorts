/**
 * @author William Kendall
 * @filename World.java
 * @date 9/17/2017
 * 
 *       This is the main object of the program. by adding (using the add method), the program can
 *       add an object to the world that exist in the world. the object will not be added if the
 *       parent does not exist.
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.tree.DefaultMutableTreeNode;


public class World extends Thing {
  ArrayList<SeaPort> ports;
  PortTime time;

  public World(Scanner sc) {
    super(sc);
    ports = new ArrayList<SeaPort>();
    time = new PortTime();
  }

  @Override
  public int compareTo(Thing o) {
    return o instanceof World ? this.name.compareToIgnoreCase(o.name) : 0;
  }

  public void add(Thing o) {
    if (o instanceof SeaPort) {
      Thing.insert((SeaPort) o, this.ports);
      o.parent = this;
    }
  }


  public String toString() {
    return "World: " + super.toString() + " " + time.toString();
  }

  public String toString(int i) {
    String t = indent(i) + toString();

    t += "\n" + indent(i + 1) + "Sea Ports:";
    for (SeaPort sp : ports)
      t += "\n" + sp.toString(i + 1);


    return t;
  }

  @Override
  public void getItems(HashMap<Integer, Thing> array) {
    array.put(this.index, this);
    ports.forEach((v) -> {
      v.getItems(array);
    });
  }

  @Override
  public DefaultMutableTreeNode buildTree() {
    DefaultMutableTreeNode dmt = new DefaultMutableTreeNode(this.toString());
    for(Thing t: this.ports)
    {
      dmt.add(t.buildTree());
    }
    return dmt;
  }

  @Override
  public void delete(Thing o) {
    for(Thing t: this.ports)
    {
      t.delete(o);      
    }
    this.ports.remove(o);
    
    
  }

  @Override
  public void update() {
    for(Thing t: this.ports)
    {
      t.update();      
    }
    
  }


  
}
