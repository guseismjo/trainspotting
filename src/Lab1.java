import TSim.*;

public class Lab1 {

  public Lab1(Integer speed1, Integer speed2) {
    TSimInterface tsi = TSimInterface.getInstance();
   
    Train t1 = new Train(1, tsi, speed1, 0);
	Train t2 = new Train(2, tsi, speed2, 1);
	
	t1.start();
	t2.start();

    
  }
}
