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

    public void setSpeed(int id, int speed) {

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
                    try {
                        tsi.setSwitch(trackIns.getSwitchesPositions()[value].x, trackIns.getSwitchesPositions()[value].y, switchDir);
                    } catch (CommandException e) {
                        e.printStackTrace();
                    }
                }

            }


        }

    }

    public Train(int id, TSimInterface tsi, int speed, int goingUp, int lastTrack) {

        this.id = id;
        this.tsi = tsi;
        this.speed = speed;
        this.goingUp = goingUp;
        this.trackAcquired = 0;
        this.lastTrack = lastTrack;
        this.sensorId = 0;

    }

    public void run() {

        //int sensorId = 0;
        //int lastTrack = 0;
        //int trackAcquired = 0;

        setSpeed(id, speed);
        trackIns.acquireSemaphore(this, lastTrack, speed, id, false);

        while (true) {

            SensorEvent sensor = null;

            try {
                sensor = tsi.getSensor(id);
            } catch (CommandException | InterruptedException e) {
                e.printStackTrace();
            }

            if (sensor.getStatus() == SensorEvent.ACTIVE) {

                Point point = new Point(sensor.getXpos(), sensor.getYpos());

                for (int i = 1; i < 17; i++) {
                    if (trackIns.getSensorPositions()[i].equals(point)) {
                        sensorId = i;
                    }
                }

                System.out.println(sensorId + " crossed and going " + goingUp + " --train " + id);


                if (goingUp == 1 && trackIns.getSensorDirection().get(sensorId) == 1) { //if the train is going up
                    continue;
                } else if (goingUp == 0 && trackIns.getSensorDirection().get(sensorId) == 0) {
                    continue;
                }

                // --------------------------------------------------------------------------

                permissionEvent();


            }
            if (sensor.getStatus() == SensorEvent.INACTIVE) {


                int releaseId = trackIns.getSensorExitTrack().get(sensorId);
                lastTrack = releaseId;

                if (goingUp == 0 && trackIns.getSensorDirection().get(sensorId) == 1) {

                    if(trackIns.releaseSemaphore(releaseId))
                        System.out.println("Semaphore " + releaseId + " released" + " by train " + id);


                } else if (goingUp == 1 && trackIns.getSensorDirection().get(sensorId) == 0) {

                    if(trackIns.releaseSemaphore(releaseId))
                        System.out.println("Semaphore " + releaseId + " released" + " by train " + id);

                }

                // -----------------------------------------------------------------------------------

                if (trackAcquired == 10 || trackAcquired == 9) {

                    //SET SPEED MINUS
                    int s = speed;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setSpeed(id, 0);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setSpeed(id, -s);
                    //if(goingUp == 0) goingUp = 1;
                    if (goingUp == 1) goingUp = 0;
                    System.out.println("Is Train " + id + " going up: " + goingUp);


                } else if (trackAcquired == 1 || trackAcquired == 2) {

                    //SET SPEED MINUS
                    int s = speed;
                    try {
                        Thread.sleep(2000 + (20 * Math.abs(speed)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setSpeed(id, 0);
                    try {
                        Thread.sleep(1000 + (20 * Math.abs(speed)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setSpeed(id, -s);
                    if (goingUp == 0) goingUp = 1;
                    //if(goingUp == 1) goingUp = 0;
                    System.out.println("Is Train " + id + " going up: " + goingUp);

                }


                for (int z = 1; z < 11; z++) {
                    System.out.println("semaphore " + z + " have " + Track.semaphoreList.get(z).availablePermits() + " permits");
                }


            }


        }

    }

    private boolean acquire(int trackToEnterId, int s, int id, boolean lastiteration){

        if (trackIns.acquireSemaphore(this, trackToEnterId, s, id, lastiteration)) {

            System.out.println("Semaphore " + trackToEnterId + " aquired by train " + id);
            trackAcquired = trackToEnterId;

            // ----------------------------------------------------------------------------

            updateSwitches(sensorId, lastTrack, trackToEnterId);
            return true;
        }
        return false;

    }

    private void permissionEvent() {

        int s = speed;

        for (int i = 0; i < trackIns.getSensorWhichTracks().get(sensorId).length; i++) {

            int trackToEnterId = trackIns.getSensorWhichTracks().get(sensorId)[i];

            if (sensorId == 9 && Track.semaphoreList.get(10).availablePermits() == 0){

                if(acquire(8, s, id, false))
                    break;

            }else if (sensorId == 9 && Track.semaphoreList.get(9).availablePermits() == 0) {

                if(acquire(7, s, id, false))
                        break;
            }

            if (i + 1 == trackIns.getSensorWhichTracks().get(sensorId).length){

                if(acquire(trackToEnterId, s, id, true))
                    setSpeed(id, s);
                    break;

            }

            if(acquire(trackToEnterId, s ,id, false))
                break;

        }


    }

}
