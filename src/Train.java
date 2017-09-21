import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import TSim.CommandException;
import TSim.SensorEvent;
import TSim.TSimInterface;
import com.sun.org.apache.xpath.internal.SourceTree;

public class Train extends Thread {

    private int id;
    private TSimInterface tsi;
    private Integer speed;
    private Track trackIns = new Track();
    private int goingUp;
    private int trackAcquired;
    private int lastTrack;
    private int sensorId;

    private void setSpeed(int id, int speed) {

        try {
            tsi.setSpeed(id, speed);
        } catch (CommandException e) {
            e.printStackTrace();
        }
        this.speed = speed;

    }

    private void updateSwitches(int sensorId, int lastTrack, int trackToEnterId) {

        if (trackIns.getSensorSwitch().containsKey(sensorId)) {

            int value = trackIns.getSensorSwitch().get(sensorId);

            int[] s = new int[2];
            s[0] = lastTrack;
            s[1] = trackToEnterId;


            for (Map.Entry<int[], Integer> entry : trackIns.getTrackToTrack().entrySet()) {

                if (entry.getKey()[0] == s[0] && entry.getKey()[1] == s[1]) {
                    int switchDir = entry.getValue();
                    System.out.println("Switch dir: "+switchDir);
                    try {
                        tsi.setSwitch(trackIns.getSwitchesPositions()[value].x, trackIns.getSwitchesPositions()[value].y, switchDir);
                    } catch (CommandException e) {
                        e.printStackTrace();
                    }
                }

            }


        }

    }

    public Train(int id, TSimInterface tsi, int speed, int goingUp, int startTrack) {

        this.id = id;
        this.tsi = tsi;
        this.speed = speed;
        this.goingUp = goingUp;
        this.trackAcquired = 0;
        this.lastTrack = startTrack;
        this.sensorId = 0;

    }

    public void run() {

        //int sensorId = 0;
        //int lastTrack = 0;
        //int trackAcquired = 0;


            setSpeed(id, speed);


            trackIns.acquireSemaphore(lastTrack);



        while (true) {

            SensorEvent sensor = null;

            try {
                sensor = tsi.getSensor(id);
            } catch (CommandException | InterruptedException e) {
                e.printStackTrace();
            }

            //---------------------------------Critical section--------------------------------

            if (sensor.getStatus() == SensorEvent.ACTIVE) {

                Point point = new Point(sensor.getXpos(), sensor.getYpos());

                for (int i = 1; i < 17; i++) {
                    if (trackIns.getSensorPositions()[i].equals(point)) {
                        sensorId = i;
                    }
                }

                System.out.println("Train " + id+ " crossed "+sensorId + " and going " + goingUp  + " at position "+point.x+", "+point.y);


                if (goingUp == 1 && trackIns.getSensorDirection().get(sensorId) == 1) { //if the train is going up
                    continue;
                } else if (goingUp == 0 && trackIns.getSensorDirection().get(sensorId) == 0) {
                    continue;
                }


                permissionEvent();

                // -----------------------------------------------------------------------------


            }
            if (sensor.getStatus() == SensorEvent.INACTIVE && sensor.getTrainId() == id) {

                int releaseId = trackIns.getSensorExitTrack().get(sensorId);
                lastTrack = releaseId;

                if (goingUp == 0 && trackIns.getSensorDirection().get(sensorId) == 1 && (Track.semaphoreList.get(releaseId).availablePermits()
                 == 0)) {

                    trackIns.releaseSemaphore(lastTrack);
                    System.out.println("Semaphore " + releaseId + " released" + " by train " + id);

                } else if (goingUp == 1 && trackIns.getSensorDirection().get(sensorId) == 0 && (Track.semaphoreList.get(releaseId).availablePermits() == 0)) {

                    trackIns.releaseSemaphore(lastTrack);
                    System.out.println("Semaphore " + releaseId + " released" + " by train " + id);

                }

                // -----------------------------------------------------------------------------------

                if ((trackAcquired == 10&& sensorId == 14 && goingUp == 1) || (trackAcquired == 9&& sensorId == 15 && goingUp == 1)) {

                    //SET SPEED MINUS
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int s = speed;
                    setSpeed(id, 0);
                    try {
                        Thread.sleep(1000+(20 * Math.abs(speed)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setSpeed(id, -s);
                    //if(goingUp == 0) goingUp = 1;
                    //if (goingUp == 1)
                        goingUp = 0;
                    System.out.println("Is Train " + id + " going up: " + goingUp);


                } else if ((trackAcquired == 1 && sensorId == 1 && goingUp == 0)|| (trackAcquired == 2&& sensorId == 2 && goingUp == 0)) {

                    //SET SPEED MINUS
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int s = speed;
                    setSpeed(id, 0);
                    try {
                        Thread.sleep(1000 + (20 * Math.abs(speed)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setSpeed(id, -s);
                    //if (goingUp == 0)
                        goingUp = 1;
                    //if(goingUp == 1) goingUp = 0;
                    System.out.println("Is Train " + id + " going up: " + goingUp);

                }


                for (int z = 1; z < 11; z++) {
                    System.out.println("semaphore " + z + " have " + Track.semaphoreList.get(z).availablePermits() + " permits");
                }


            }


        }

    }

    private void permissionEvent() {

        for (int i = 0; i < trackIns.getSensorWhichTracks().get(sensorId).length; i++) {

            int trackToEnterId = trackIns.getSensorWhichTracks().get(sensorId)[i];

            if (Track.semaphoreList.get(trackToEnterId).availablePermits() == 1) {




                    System.out.println("Semaphore " + trackToEnterId + " aquired by train " + id);
                    trackAcquired = trackToEnterId;
                    //Track.semaphoreList.get(trackToEnterId).acquire();

                    trackIns.acquireSemaphore(trackToEnterId);


                // ---------------------


                // ---------------------

                //switches

                updateSwitches(sensorId, lastTrack, trackToEnterId);

                if (Track.semaphoreList.get(10).availablePermits() == 0) {
                    updateSwitches(sensorId, lastTrack, 8);
                } else if (Track.semaphoreList.get(9).availablePermits() == 0) {
                    updateSwitches(sensorId, lastTrack, 7);
                }

                break;
            }

            // if last round in the loop and it didnt break
            if (i + 1 == trackIns.getSensorWhichTracks().get(sensorId).length && (Track.semaphoreList.get(lastTrack).availablePermits() == 0)) {
                //System.out.println("Last track "+(i + 1 == trackIns.getSensorWhichTracks().get(sensorId).length));

                System.out.println("sleep on sensor " + sensorId);
                int s = speed;

                setSpeed(id, 0);
                while (Track.semaphoreList.get(trackToEnterId).availablePermits() == 0) {
                    //Do nothing :D
                    //System.out.println(trackToEnterId);
                    //System.out.println("Track to enter occupied " + (Track.semaphoreList.get(trackToEnterId).availablePermits() == 0));

                    //permissionEvent();
                }

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateSwitches(sensorId, lastTrack, trackToEnterId);

                System.out.println("S = "+s);
                setSpeed(id, s);



                try {
                    Track.semaphoreList.get(trackToEnterId).acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            updateSwitches(sensorId, lastTrack, trackToEnterId);
        }


    }



}
