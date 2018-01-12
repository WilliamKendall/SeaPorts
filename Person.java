/**
 * @author William Kendall
 * @filename Person.java
 * @date 9/17/2017
 * 
 *       This is the Person object. its constructor extends the scanner for extra data. as well as
 *       the toString method returns extra data about this object
 */

import java.util.HashMap;
import java.util.Scanner;

import javax.swing.tree.DefaultMutableTreeNode;



public class Person extends Thing {
  public String skill;

  private boolean working = false;

  Person(Scanner sc) {
    // person name index parent skill
    // person <string> <int> <int> <string>
    super(sc);
    if (sc.hasNext())
      skill = sc.next();

  }


  public Person getWorking() {
    if (this.working) {
      return null;
    } else {
      this.working = true;
      return this;
    }
  }

  public void releaseWorking() {
      this.working = false;
  }


  public String toString() {
    return "Person: " + super.toString() + " Skill: " + skill;
  }

  public String toString(int i) {
    return indent(i) + toString();
  }

  @Override
  public void getItems(HashMap<Integer, Thing> array) {
    array.put(this.index, this);
  }


  @Override
  public DefaultMutableTreeNode buildTree() {
    return new DefaultMutableTreeNode(this.name);
  }


  @Override
  public void delete(Thing o) {}


  @Override
  public void update() {}



}
