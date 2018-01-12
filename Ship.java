/**
 * @author William Kendall
 * @filename Ship.java
 * @date 9/17/2017
 * 
 *       This is the Ship object. To use this class call its constructor with a Scanner that
 *       contains its type. objects can be added to it if the parent object exist using the add
 *       method. Note: the add method will check all objects for a parent match it can return, using
 *       toString(), a single line of the object or using toString(int i), where int i is an indent
 *       count, formated string of child objects as well as itself
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.tree.DefaultMutableTreeNode;


public class Ship extends Thing {
  PortTime arrivalTime, dockTime;
  double draft, length, weight, width;
  ArrayList<Job> jobs;


  public static enum ShipComparators implements ThingComparator {
    SHIP_WEIGHT_SORT {
      public int compare(Thing t1, Thing t2) {
        if (t1 instanceof Ship && t2 instanceof Ship) {
          return (int) (((Ship) t1).weight - ((Ship) t2).weight);
        }
        return 0;
      }
    },
    SHIP_LENGTH_SORT {
      public int compare(Thing t1, Thing t2) {
        if (t1 instanceof Ship && t2 instanceof Ship) {
          return (int) (((Ship) t1).length - ((Ship) t2).length);
        }
        return 0;
      }
    },
    SHIP_WIDTH_SORT {
      public int compare(Thing t1, Thing t2) {
        if (t1 instanceof Ship && t2 instanceof Ship) {
          return (int) (((Ship) t1).width - ((Ship) t2).width);
        }
        return 0;
      }
    },
    SHIP_DRAFT_SORT {
      public int compare(Thing t1, Thing t2) {
        if (t1 instanceof Ship && t2 instanceof Ship) {
          return (int) (((Ship) t1).draft - ((Ship) t2).draft);
        }
        return 0;
      }
    };
  }


  public Ship(Scanner sc) {
    // ship name index parent(dock/port) weight length width draft
    // ship <string> <int> <int> <double> <double> <double> <double>
    super(sc);
    jobs = new ArrayList<Job>();

    if (sc.hasNextDouble())
      weight = sc.nextDouble();
    if (sc.hasNextDouble())
      length = sc.nextDouble();
    if (sc.hasNextDouble())
      width = sc.nextDouble();
    if (sc.hasNextDouble())
      draft = sc.nextDouble();
  }


  public String toString() {
    return "Ship: " + super.toString() + "\tDraft: " + Double.toString(draft) + "\tLength: "
        + Double.toString(length) + "\tWeight: " + Double.toString(weight) + "\tWidth: "
        + Double.toString(width);
  }

  public String toString(int i) {
    String t = indent(i) + toString();
    if (this.jobs.size() > 0) {
      t += "\n" + indent(i + 1) + "List of jobs:";
      for (Job job : jobs)
        t += "\n" + job.toString(i + 1);
    }
    return t;
  }
  


  @Override
  public void add(Thing o) {
    if (o instanceof Job)
      Thing.insert((Job) o, this.jobs);
    o.parent = this;
    return;
  }

  @Override
  public void getItems(HashMap<Integer, Thing> array) {
    array.put(this.index, this);
  }


  @Override
  public int compareTo(Thing o) {
    return this.name.compareToIgnoreCase(o.name);
  }


  //project 3 additions
  @Override
  public DefaultMutableTreeNode buildTree() {
    DefaultMutableTreeNode dmt = new DefaultMutableTreeNode(this.name);
   if(this.jobs.size() > 0) {
    for(Thing t: this.jobs)
    {
     dmt.add(t.buildTree());
    }
  }
        return dmt;
  }
  

public void inDock()
{
  for(Job j: this.jobs)
  {
    j.inDock = true;
  }
}

  @Override
  public void delete(Thing o) {
    this.jobs.remove(o);
  }


  @Override
  public void update() {
    for(Thing t: this.jobs)
      t.update();
    
  }


}
