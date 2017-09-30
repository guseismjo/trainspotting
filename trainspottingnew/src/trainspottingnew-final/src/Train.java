import TSim.CommandException;
import TSim.SensorEvent;
import TSim.TSimInterface;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Train extends Thread{

/**
     * @param sensorPosition
     * Position of 20 sensors on the map.
     * @param switchesPosition
     * Position of the switches.
     * @param sensorSwitch
     * Connection between sensors and switches
     * @param sensorDirection
     * Direction of switches depending on certain sensors
     * @param id
     * ID of the train
     * @param speed
     * Speed of the train
     * @param goingUp
     * Boolean that tells if the train is going up or not
     * @param doSomething
     * Decides if the current sensor should be used.
     * @param sensId
     * Actual id of the sensor
     * @param tsi
     * @param previousSensor
     * Previous sensor to prevent semaphores to be released that shouldn't
     * @param noSemaphore
     * Used on double-tracks to check if the train should release on leaving.
     * @param sensorExitTrack
     * Used in some cases to decide which track to release.
     */

    private Point[] sensorPositions = new Point[21];
    private Point[] switchesPositions = new Point[4];
    private HashMap<Integer, Integer> sensorSwitch = new HashMap<>();
    private HashMap<Integer, Integer> sensorDirection = new HashMap<>();
    private int id;
    private int speed;
    private boolean goingUp;
    private boolean doSomething;
    private HashMap<Integer, Integer> sensorExitTrack = new HashMap<>();
    private int sensId;
    private TSimInterface tsi;
    private boolean noSemaphore;

    private void initSensorExitTrack() {

        sensorExitTrack.put(1, 1);
        sensorExitTrack.put(2, 1);
        sensorExitTrack.put(3, 0);
        sensorExitTrack.put(4, 2);
        sensorExitTrack.put(5, 1);
        sensorExitTrack.put(6, 1);
        sensorExitTrack.put(7, 3);
        sensorExitTrack.put(8, 2);
        sensorExitTrack.put(9, 4);
        sensorExitTrack.put(10, 3);
        sensorExitTrack.put(11, 3);
        sensorExitTrack.put(12, 5);
        sensorExitTrack.put(13, 5);
        sensorExitTrack.put(14, 5);
        sensorExitTrack.put(15, 5);
        sensorExitTrack.put(16, 3);
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
        sensorDirection.put(17, 1);
        sensorDirection.put(18, 1);
        sensorDirection.put(19, 0);
        sensorDirection.put(20, 0);

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

    private void initSensorPositions() {

        sensorPositions[1] = new Point(4, 13);
        sensorPositions[2] = new Point(6, 11);
        sensorPositions[3] = new Point(1, 11);
        sensorPositions[4] = new Point(1, 9);
        sensorPositions[5] = new Point(7, 10);
        sensorPositions[6] = new Point(7, 9);
        sensorPositions[7] = new Point(13, 9);
        sensorPositions[8] = new Point(17, 9);
        sensorPositions[9] = new Point(19, 7);
        sensorPositions[10] = new Point(14, 8);
        sensorPositions[11] = new Point(14, 7);
        sensorPositions[12] = new Point(11, 7);
        sensorPositions[13] = new Point(11, 8);
        sensorPositions[14] = new Point(6, 6);
        sensorPositions[15] = new Point(9, 5);
        sensorPositions[16] = new Point(13, 10);
        sensorPositions[17] = new Point(15, 5);
        sensorPositions[18] = new Point(15, 3);
        sensorPositions[19] = new Point(15, 11);
        sensorPositions[20] = new Point(15, 13);

    }

    private void update(int semaphoreId, int sensorId, int switchDir, boolean splitTrack){

        //SplitTrack indicates a double-track

        if(splitTrack){

            if(Lab1.semaphores.get(semaphoreId).tryAcquire()){

                noSemaphore = false;

                int zwitch = sensorSwitch.get(sensorId);
                try {
                    tsi.setSwitch(switchesPositions[zwitch].x, switchesPositions[zwitch].y, switchDir);
                } catch (CommandException e) {
                    e.printStackTrace();
                }

            }else{

                noSemaphore = true;

                int switchDir2 = (switchDir == 1) ? 2 : 1;

                int zwitch = sensorSwitch.get(sensorId);
                try {
                    tsi.setSwitch(switchesPositions[zwitch].x, switchesPositions[zwitch].y, switchDir2);
                } catch (CommandException e) {
                    e.printStackTrace();
                }

            }

        }else{

            if(!Lab1.semaphores.get(semaphoreId).tryAcquire()){

                try{
                    tsi.setSpeed(id, 0);

                    Lab1.semaphores.get(semaphoreId).acquire();
                    System.err.println("============ " + switchDir + "     " + TSimInterface.SWITCH_RIGHT);


                    tsi.setSpeed(id, speed);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            if(switchDir != 0){

                int zwitch = sensorSwitch.get(sensorId);
                try {
                    tsi.setSwitch(switchesPositions[zwitch].x, switchesPositions[zwitch].y, switchDir);
                } catch (CommandException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    public Train(int id, boolean goingUp, int speed, boolean noSemaphore){
        initSensorSwitch();
        initSensorPositions();
        initSwitches();
        initSensorDirection();
        initSensorExitTrack();
        this.id = id;
        this.sensId = 0;
        this.speed = speed;
        this.goingUp = goingUp;
        this.doSomething = true;
        tsi = TSimInterface.getInstance();
        this.noSemaphore = noSemaphore;

    }

    public void run(){

        while(true){

            SensorEvent sensorEvent = null;

            try {
                sensorEvent = tsi.getSensor(id);
            } catch (CommandException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(sensorEvent.getStatus() == SensorEvent.ACTIVE){

                int sensorId = 0;

                for(int i = 1; i < sensorPositions.length; i++){

                    if(sensorEvent.getXpos() == sensorPositions[i].x && sensorEvent.getYpos() == sensorPositions[i].y){
                        sensorId = i;
                        this.sensId = i;
                    }

                }

                //sensorDirection and goingUp combined gives us which sensors to be used at certain moments.

                if(sensorDirection.get(sensorId) == 1){

                    if(goingUp){
                        doSomething = false;
                    }else{
                        doSomething = true;
                    }

                }else{

                    if(goingUp){
                        doSomething = true;
                    }else{
                        doSomething = false;
                    }

                }

                if(doSomething){

                    if(sensorId == 1){

                        update(1, 1, 2, false);

                    }else if(sensorId == 2){

                        update(1, 2, 1, false);

                    }else if(sensorId == 3){

                        update(0, 3, 1, true);

                    }else if(sensorId == 4){

                        update(2, 4, 1, true);

                    }else if(sensorId == 5){

                        update(1, 5, 2, false);

                    }else if(sensorId == 6){

                        update(1, 6 , 1, false);

                    }else if(sensorId == 7){

                        update(3, 7, 2, false);

                    }else if(sensorId == 8){

                        update(2, 8, 2, true);

                    }else if(sensorId == 9){

                        update(4, 9, 1, true);

                    }else if(sensorId == 10){

                        update(3, 10, 1, false);

                    }else if(sensorId == 11){

                        update(3, 11, 2, false);

                    }else if(sensorId == 12){

                        update(5, 12, 0, false);

                    }else if(sensorId == 13){

                        update(5, 13, 0, false);

                    }else if(sensorId == 14){

                        update(5, 14, 0, false);

                    }else if(sensorId == 15){

                        update(5, 15, 0, false);

                    }else if(sensorId == 16){

                        update(3, 16, 1, false);

                    }


                }

                if(!doSomething){

                    boolean inList = false;

                    for (Map.Entry<Integer, Integer> entry : sensorExitTrack.entrySet()) {

                        if(sensId == entry.getKey()) inList = true;

                    }

                    //release if last track had semaphore
                    if(sensorId == 3 || sensorId == 4 || sensorId == 8 || sensorId == 9){

                        if(inList && !noSemaphore){

                            int releaseId = sensorExitTrack.get(sensId);
                            Lab1.semaphores.get(releaseId).release();

                        }

                    }else{

                        if(inList){

                            int releaseId = sensorExitTrack.get(sensId);
                            Lab1.semaphores.get(releaseId).release();

                        }

                    }

                    //turn around

                    if(sensId == 17 || sensId == 18 || sensId == 19 || sensId == 20){

                        int s = speed;
                        try{

                            tsi.setSpeed(id, 0);
                            Thread.sleep(1000 + (20 * Math.abs(s)));
                            tsi.setSpeed(id, -s);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        speed = -s;
                        goingUp = !goingUp;

                    }


                }

            }

        }

    }

}
