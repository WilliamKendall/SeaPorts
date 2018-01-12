/**
 * @author William Kendall
 * @filename Job.java
 * @date 9/17/2017
 * 
 *       This is the Job object. its constructor extends the scanner for extra data. as well as the
 *       toString method returns extra data about this object
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

public class Job extends Thing implements Runnable {
  Double duration;
  ArrayList<String> requirements; // List of job skills
  ArrayList<Person> workersOnTheJob = new ArrayList<Person>();


  ActionListener progressListener;
  public Object syncLock = null; // its like a request line at the seaport

  public boolean inDock = false;

  private boolean jobCanceled = false;



  Job(Scanner sc) {
    super(sc);
    requirements = new ArrayList<String>();
    if (sc.hasNextDouble())
      duration = sc.nextDouble();
    while (sc.hasNext()) {
      requirements.add(sc.next());
      java.util.Collections.sort(this.requirements);
    }

    (new Thread(this)).start();
  }



  public String toString(int i) {
    String t = indent(i) + "Job: " + super.toString();
    t += " Requirements:";
    for (String req : requirements)
      t += " " + req;
    return t;
  }


  @Override
  public void getItems(HashMap<Integer, Thing> array) {
    array.put(this.index, this);
  }


  // project 3 additions
  @Override
  public DefaultMutableTreeNode buildTree() {
    return new DefaultMutableTreeNode(this.name);
  }


  @Override
  public void run() {

    // wait until the ship pulls into port
    while (this.inDock == false) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }
      this.progressListener.actionPerformed(new ActionEvent(this, 1, "checkStatus"));
      this.progressListener.actionPerformed(new ActionEvent(this, 1, "update", 0));
    }

    SeaPort port = (SeaPort) this.parent.parent.parent;


    // check to see if the job can be completed
    int reqCount = 0;
    for (String r : this.requirements) {
      for (Person p : port.persons) {
        if (r.compareToIgnoreCase(p.skill) == 0) {
          reqCount++;
        }
      }
    }

    if (reqCount != this.requirements.size()) {
      // can not complete the job
      jobCanceled = true;
      JOptionPane.showMessageDialog(null, "Unable to complete job: " + this.toString(),
          "Worker not available", JOptionPane.INFORMATION_MESSAGE);
      this.progressListener.actionPerformed(new ActionEvent(this, 3, "canceled")); // finished
      return;
    }


    // get workers
    while (workersOnTheJob.size() != this.requirements.size()) {
      this.progressListener.actionPerformed(new ActionEvent(this, 1, "update", 0));//tell progress
      synchronized (port.persons) { // protect from other threads grabbing workers

        // try and get workers again
        for (String r : this.requirements) {
          for (Person p : port.persons) {
            if (r.compareToIgnoreCase(p.skill) == 0) {
              Person x = p.getWorking();
              if (x != null) {
                workersOnTheJob.add(x);
              }
              break;
            } // end if
          } // end person for
        } // end requi for

        if (workersOnTheJob.size() != this.requirements.size()) {
          // release workers if requirements not met
          for (Person p : workersOnTheJob) {
            p.releaseWorking();
          }
          workersOnTheJob.clear();

        }
      } // end sync
      try {
        Thread.sleep(100);;// thread goes and gets a cup of coffee
      } catch (InterruptedException e) {
      }
      this.progressListener.actionPerformed(new ActionEvent(this, 1, "checkStatus"));
    } // end while



    this.progressListener.actionPerformed(new ActionEvent(this, 1, "gotWorkers"));

    // run jobs
    try {
      for (double d = 0; d < this.duration; d += 1) {
        Thread.sleep(50);// simulate work
        this.progressListener.actionPerformed(new ActionEvent(this, 1, "checkStatus"));
        this.progressListener
            .actionPerformed(new ActionEvent(this, 1, "update", (int) ((100 * d) / this.duration)));
        if (jobCanceled) {
          synchronized (port.persons) {
            // release workers
            for (Person p : workersOnTheJob) {
              p.releaseWorking();
            }
          }
          this.progressListener.actionPerformed(new ActionEvent(this, 1, "canceled"));
          return;
        }
      }
      this.progressListener.actionPerformed(new ActionEvent(this, 1, "update", 100)); // finished
      this.progressListener.actionPerformed(new ActionEvent(this, 2, "finished")); // finished
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    this.progressListener.actionPerformed(new ActionEvent(this, 1, "releaseWorkers"));

    synchronized (port.persons) {
      // release workers
      for (Person p : workersOnTheJob) {
        p.releaseWorking();
      }
    }
  }

  public void cancelJob() {
    this.jobCanceled = true;
  }


  @Override
  public void delete(Thing o) {

  }



  @Override
  public void update() {}

}
