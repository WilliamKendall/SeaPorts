/**
 * @author William Kendall
 * @filename Thing.java
 * @date 9/17/2017
 * 
 *       This is the main type class. it imports a scanner that reads the first three lines of data
 *       storing the name index and parent of an item. most functions of this class just provide a
 *       prototype for other classes
 */
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.tree.DefaultMutableTreeNode;


public abstract class Thing implements java.lang.Comparable<Thing> {

  public String name;
  public int index;
  public int parentIndex;
  public Thing parent;

 
  
  
  public static interface ThingComparator extends Comparator<Thing> {
    // just a named interface
  }
  public static enum ThingComparators implements ThingComparator {
    NAME_SORT {
      public int compare(Thing t1, Thing t2) {
        return t1.name.compareToIgnoreCase(t2.name);
      }
    },
    INDEX_SORT {
      public int compare(Thing t1, Thing t2) {
        return t1.index - t2.index;
      }
    };
  }

  /**
   * stackComparators will return one comparator for a list of many. The first order comparators
   * take priority
   * 
   * @param compareList a list of ThingComparators ie: ThingComparator1, ThingComparator2,
   *        ThingComparator3
   * @return A java.util.Comparator Type: Thing
   */
  public static Comparator<Thing> stackComparators(ThingComparator... compareList) {
    return new Comparator<Thing>() {
      public int compare(Thing t1, Thing t2) {
        for (ThingComparator comp : compareList) {
          int result = comp.compare(t1, t2);
          if (result != 0) {
            return result;
          }
        }
        return 0;
      }
    };
  }


  Thing(Scanner sc) {
    if (sc.hasNext())
      name = sc.next();
    if (sc.hasNextInt())
      index = sc.nextInt();
    if (sc.hasNextInt())
      parentIndex = sc.nextInt();
  }

  /**
   * Instert items into array in order
   * 
   * @param o Thing to add
   * @param list ArrayList to add things to
   */
  public static <T extends Thing> void insert(T o, java.util.ArrayList<T> list) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).compareTo(o) > 0) {
        list.add(i, o);
        return;
      }
    }
    list.add(o);// last item
  }


  // for a formated toString(int i) method
  protected String indent(int i) {
    String n = "";
    for (int x = 0; x < i; x++)
      n += "     ";
    return n;
  }

  public String toString() {
    return name;
  }

  public String toString(int i) {
    return "";
  }

  public void add(Thing o) {
    return;
  }
  
  
  /**
   * returns a hashmap containing the item and sub items it holds
   * 
   * @param array An array to add the items to
   */
  public abstract void getItems(java.util.HashMap<Integer, Thing> array);

  public int compareTo(Thing o) {
    return this.name.compareToIgnoreCase(o.name);
  }

  public void Sort(Thing.ThingComparator... comp) {
  }
  
  
  
  abstract public DefaultMutableTreeNode buildTree();
  abstract public void delete(Thing o);
  abstract public void update();

}
