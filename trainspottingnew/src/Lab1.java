import TSim.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Lab1 {

  private Train train1, train2;
  static ArrayList<Semaphore> semaphores;
  //protected static int nrAcquired;

  public Lab1(Integer speed1, Integer speed2) {
    TSimInterface tsi = TSimInterface.getInstance();


    semaphores = new ArrayList<>();
    for(int i = 0; i < 6; i++){
      semaphores.add(new Semaphore(1));
    }
    semaphores.get(0).drainPermits();


    train1 = new Train(1, false, speed1);
    train2 = new Train(2, true, speed2);

    train1.start();
    train2.start();

    try {
      tsi.setSpeed(1,speed1);
      tsi.setSpeed(2,speed2);
    }
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1);
    }
  }
}
