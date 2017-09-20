import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Track {

    static ArrayList<Semaphore> semaphoreList = new ArrayList<Semaphore>();

    private Point[] sensorPositions;
    private Point[] switchesPositions;

    private HashMap<Integer, int[]> sensorWhichTracks;
    private HashMap<Integer, Integer> sensorExitTrack;
    private HashMap<Integer, Integer> sensorDirection;
    private HashMap<Integer, Integer> sensorSwitch;
    private HashMap<int[], Integer> trackToTrack;

    public Point[] getSwitchesPositions() {
        return switchesPositions;
    }

    public HashMap<Integer, int[]> getSensorWhichTracks() {
        return sensorWhichTracks;
    }

    public HashMap<Integer, Integer> getSensorExitTrack() {
        return sensorExitTrack;
    }

    public HashMap<Integer, Integer> getSensorDirection() {
        return sensorDirection;
    }

    public HashMap<Integer, Integer> getSensorSwitch() {
        return sensorSwitch;
    }

    public HashMap<int[], Integer> getTrackToTrack() {
        return trackToTrack;
    }

    public Point[] getSensorPositions() {
        return sensorPositions;
    }

    public synchronized boolean acquireSemaphore(Train train, int trackToEnterId, int speed, int id, boolean lastIteration){

        try {

            if(semaphoreList.get(trackToEnterId).tryAcquire()){

                return true;

            }else{

                if(lastIteration){

                    train.setSpeed(id, 0);

                    if(semaphoreList.get(trackToEnterId).tryAcquire(3000, TimeUnit.MILLISECONDS)){
                        Thread.sleep(2000);
                        return true;
                    }
                    return false;

                }
                return false;

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

    }

    public synchronized boolean releaseSemaphore(int lastTrack){

        if(semaphoreList.get(lastTrack).availablePermits() == 1){

            System.out.println("WARNING, DOUBLE RELEASE!");
            return false;

        }else{

            semaphoreList.get(lastTrack).release();
            return true;

        }

    }

    private void initSensorPositions() {

        sensorPositions[1] = new Point(3, 13);
        sensorPositions[2] = new Point(5, 11);
        sensorPositions[3] = new Point(2, 11);
        sensorPositions[4] = new Point(3, 9);
        sensorPositions[5] = new Point(6, 10);
        sensorPositions[6] = new Point(6, 9);
        sensorPositions[7] = new Point(13, 9);
        sensorPositions[8] = new Point(17, 9);
        sensorPositions[9] = new Point(18, 7);
        sensorPositions[10] = new Point(16, 8);
        sensorPositions[11] = new Point(15, 7);
        sensorPositions[12] = new Point(9, 7);
        sensorPositions[13] = new Point(9, 8);
        sensorPositions[14] = new Point(6, 7);
        sensorPositions[15] = new Point(8, 6);
        sensorPositions[16] = new Point(13, 10);

    }

    private void initSemaphore(){

        for(int i = 0; i < 11; i++){
            semaphoreList.add(new Semaphore(1));
        }

    }

    private void initTrackToTrack() {

        trackToTrack.put(new int[]{1, 3}, 1);
        trackToTrack.put(new int[]{2, 3}, 1);
        trackToTrack.put(new int[]{3, 2}, 1);
        trackToTrack.put(new int[]{3, 1}, 2);

        trackToTrack.put(new int[]{3, 4}, 2);
        trackToTrack.put(new int[]{3, 5}, 1);
        trackToTrack.put(new int[]{5, 3}, 1);
        trackToTrack.put(new int[]{4, 3}, 2);

        trackToTrack.put(new int[]{4, 6}, 1);
        trackToTrack.put(new int[]{5, 6}, 2);
        trackToTrack.put(new int[]{6, 5}, 2);
        trackToTrack.put(new int[]{6, 4}, 1);

        trackToTrack.put(new int[]{6, 7}, 2);
        trackToTrack.put(new int[]{6, 8}, 1);
        trackToTrack.put(new int[]{8, 6}, 1);
        trackToTrack.put(new int[]{7, 6}, 2);

    }

    private void initSensorSwitch() {

        sensorSwitch.put(1, 0);
        sensorSwitch.put(2, 0);
        sensorSwitch.put(3, 0);
        sensorSwitch.put(4, 1);
        sensorSwitch.put(5, 1);
        sensorSwitch.put(6, 1);
        sensorSwitch.put(7, 2);
        sensorSwitch.put(8, 2);
        sensorSwitch.put(9, 3);
        sensorSwitch.put(10, 3);
        sensorSwitch.put(11, 3);
        sensorSwitch.put(16, 2);

    }

    private void initSwitches() {

        switchesPositions[0] = new Point(3, 11);
        switchesPositions[1] = new Point(4, 9);
        switchesPositions[2] = new Point(15, 9);
        switchesPositions[3] = new Point(17, 7);

    }

    private void initSensorDirection() {

        sensorDirection.put(1, 0);
        sensorDirection.put(2, 0);
        sensorDirection.put(3, 1);
        sensorDirection.put(4, 0);
        sensorDirection.put(5, 1);
        sensorDirection.put(6, 1);
        sensorDirection.put(7, 0);
        sensorDirection.put(8, 1);
        sensorDirection.put(9, 0);
        sensorDirection.put(10, 1);
        sensorDirection.put(11, 1);
        sensorDirection.put(12, 0);
        sensorDirection.put(13, 0);
        sensorDirection.put(14, 1);
        sensorDirection.put(15, 1);
        sensorDirection.put(16, 0);

    }

    private void initSensorExitTrack() {

        sensorExitTrack.put(1, 1);
        sensorExitTrack.put(2, 2);
        sensorExitTrack.put(3, 3);
        sensorExitTrack.put(4, 3);
        sensorExitTrack.put(5, 4);
        sensorExitTrack.put(6, 5);
        sensorExitTrack.put(7, 5);
        sensorExitTrack.put(8, 6);
        sensorExitTrack.put(9, 6);
        sensorExitTrack.put(10, 8);
        sensorExitTrack.put(11, 7);
        sensorExitTrack.put(12, 7);
        sensorExitTrack.put(13, 8);
        sensorExitTrack.put(14, 10);
        sensorExitTrack.put(15, 9);
        sensorExitTrack.put(16, 4);
    }


    private void initSensorTracks() {

        sensorWhichTracks.put(1, new int[]{3});
        sensorWhichTracks.put(2, new int[]{3});
        sensorWhichTracks.put(3, new int[]{1, 2});
        sensorWhichTracks.put(4, new int[]{4, 5});
        sensorWhichTracks.put(5, new int[]{3});
        sensorWhichTracks.put(6, new int[]{3});
        sensorWhichTracks.put(7, new int[]{6});
        sensorWhichTracks.put(8, new int[]{4, 5});
        sensorWhichTracks.put(9, new int[]{7, 8, 9, 10});
        sensorWhichTracks.put(10, new int[]{6});
        sensorWhichTracks.put(11, new int[]{6});
        sensorWhichTracks.put(12, new int[]{10});
        sensorWhichTracks.put(13, new int[]{9});
        sensorWhichTracks.put(14, new int[]{7});
        sensorWhichTracks.put(15, new int[]{8});
        sensorWhichTracks.put(16, new int[]{6});

    }



    public Track(){

        this.sensorPositions = new Point[17];
        initSensorPositions();
        initSemaphore();


        this.sensorWhichTracks = new HashMap<>();
        this.sensorExitTrack = new HashMap<>();
        this.sensorDirection = new HashMap<>();
        this.switchesPositions = new Point[4];
        this.sensorSwitch = new HashMap<>();
        this.trackToTrack = new HashMap<>();
        initTrackToTrack();
        initSensorTracks();
        initSensorExitTrack();
        initSensorDirection();
        initSwitches();
        initSensorSwitch();

    }

}
