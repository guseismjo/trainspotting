import TSim.CommandException;
import TSim.SensorEvent;
import TSim.TSimInterface;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Train extends Thread {

    private Point[] sensorPositions = new Point[21];
    private Point[] switchesPositions = new Point[4];
    private HashMap<Integer, Integer> sensorSwitch = new HashMap<>();
    private HashMap<Integer, Integer> sensorDirection = new HashMap<>();
    private int id;
    private int speed;
    private boolean goingUp;
    private boolean doSomething;
    //private HashMap<Integer, Integer> sensorExitTrack = new HashMap<>();
    private int sensId;
    private TSimInterface tsi;
    private int previousSensor;


    public Train(int id, boolean goingUp, int speed, boolean hasSemaphore) {
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
        this.previousSensor = 0;

    }


    private void initSensorExitTrack() {

        /*

        sensorExitTrack.put(1,1);
        sensorExitTrack.put(2, 1);
        sensorExitTrack.put(3, 0);
        sensorExitTrack.put(4, 2);
        sensorExitTrack.put(5,1);
        sensorExitTrack.put(6, 1);
        sensorExitTrack.put(7, 3);
        sensorExitTrack.put(8, 4);
        sensorExitTrack.put(9, 4);
        sensorExitTrack.put(10, 3);
        sensorExitTrack.put(11,3);
        sensorExitTrack.put(12, 5);
        sensorExitTrack.put(13, 5);
        sensorExitTrack.put(14, 5);
        sensorExitTrack.put(15, 5);
        */
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
        sensorPositions[12] = new Point(10, 7);
        sensorPositions[13] = new Point(9, 8);
        sensorPositions[14] = new Point(6, 7);
        sensorPositions[15] = new Point(9, 5);
        sensorPositions[16] = new Point(13, 10);

        sensorPositions[17] = new Point(15, 3);
        sensorPositions[18] = new Point(15, 5);
        sensorPositions[19] = new Point(15, 11);
        sensorPositions[20] = new Point(15, 13);

    }

    private void update(int semaphoreId, int sensorId, int switchDir, boolean splitTrack, int trackToRelease) {

        for (int i = 0; i < Lab1.semaphores.size(); i++) {
            System.out.println("sem " + i + " : " + Lab1.semaphores.get(i).availablePermits());
        }
        //System.out.println("Track to release is: "+trackToRelease +" and possible to release is: "+Lab1.semaphores.get(trackToRelease).tryAcquire());

        if (trackToRelease != -1 && (Lab1.semaphores.get(trackToRelease).availablePermits() == 0)) { //to prevent releasing tracks it shouldn't

            Lab1.semaphores.get(trackToRelease).release();

        }


        if (splitTrack && semaphoreId != -1) {



            if (Lab1.semaphores.get(semaphoreId).tryAcquire()){
                //int releaseId = sensorExitTrack.get(sensorId);
                //Lab1.semaphores.get(releaseId).release();


                int zwitch = sensorSwitch.get(sensorId);
                try {
                    System.out.println("I'm here 1");
                    tsi.setSwitch(switchesPositions[zwitch].x, switchesPositions[zwitch].y, switchDir);
                } catch (CommandException e) {
                    e.printStackTrace();
                }

            } else  {



                int switchDir2 = (switchDir == 1) ? 2 : 1;

                int zwitch = sensorSwitch.get(sensorId);
                try {
                    System.out.println("I'm here 2");
                    tsi.setSwitch(switchesPositions[zwitch].x, switchesPositions[zwitch].y, switchDir2);
                } catch (CommandException e) {
                    e.printStackTrace();
                }

            }



        } else if (!splitTrack && semaphoreId != -1) {

            if (!Lab1.semaphores.get(semaphoreId).tryAcquire()) {

                try {
                    tsi.setSpeed(id, 0);

                    //Lab1.semaphores.get(semaphoreId).tryAcquire(15000, TimeUnit.MILLISECONDS);

                    Lab1.semaphores.get(semaphoreId).acquire();
                    //hasSemaphore = true;

                    //int releaseId = sensorExitTrack.get(sensorId);

                    //Lab1.semaphores.get(releaseId).release();

                    //Thread.sleep(30000 / Math.abs(speed)); // Denna tiden måste egentligen justeras efter speed, så på låga hastigheter krockar den på vissa ställen

                    tsi.setSpeed(id, speed);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (switchDir != 0) {

                int zwitch = sensorSwitch.get(sensorId);
                try {
                    tsi.setSwitch(switchesPositions[zwitch].x, switchesPositions[zwitch].y, switchDir);
                } catch (CommandException e) {
                    e.printStackTrace();
                }

            }

        }


    }




    public void run() {

        while (true) {

            SensorEvent sensorEvent = null;

            try {
                sensorEvent = tsi.getSensor(id);
            } catch (CommandException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (sensorEvent.getStatus() == SensorEvent.ACTIVE) {
                System.out.println("prev sensor: "+previousSensor);

                int sensorId = 0;

                for (int i = 1; i < sensorPositions.length; i++) {

                    if (sensorEvent.getXpos() == sensorPositions[i].x && sensorEvent.getYpos() == sensorPositions[i].y) {
                        sensorId = i;
                        this.sensId = i;
                    }

                }
/*
                if (sensorDirection.get(sensorId) == 1) {

                    if (goingUp) {
                        doSomething = false;
                    } else {
                        doSomething = true;
                    }

                } else {

                    if (goingUp) {
                        doSomething = true;
                    } else {
                        doSomething = false;
                    }

                }

*/
                if (doSomething) {

                    System.out.println("Train nr: " + id + " passed sensor " + sensorId + " and is going " + goingUp);

                    if (sensorId == 1) {
                        if (!goingUp) {
                            update(-1, 1, 0, false, 1);
                        } else {
                            update(1, 1, 2, false, -1);
                        }

                    } else if (sensorId == 2) {
                        if (goingUp) {
                            update(1, 2, 1, false, -1);

                        } else {
                            update(-1, 2, 0, false, 1);

                        }


                    } else if (sensorId == 3) {
                        if (goingUp) {
                            if(previousSensor == 2) {
                                update(-1, 3, 0, false, 0);
                            }
                        } else {
                            update(0, 3, 1, true, -1);

                        }


                    } else if (sensorId == 4) {
                        if (goingUp) {
                            update(2, 4, 1, true, -1);
                        } else {
                            if(previousSensor == 6) {
                                update(-1, 4, 0, true, 2);
                            }
                        }


                    } else if (sensorId == 5) {
                        if (goingUp) {
                            update(-1, 5, 0, false, 1);
                        } else {
                            update(1, 5, 2, false, -1);
                        }


                    } else if (sensorId == 6) {
                        if (goingUp) {
                            update(-1, 6, 0, false, 1);
                        } else {
                            update(1, 6, 1, false, -1);
                        }


                    } else if (sensorId == 7) {
                        if (goingUp) {
                            update(3, 7, 2, false, -1);

                        } else {
                            update(-1, 7, 0, false, 3);
                        }


                    } else if (sensorId == 8) {
                        if (goingUp) {
                            if(previousSensor == 7) {
                                update(-1, 8, 0, true, 2);
                            }
                        } else {
                            update(2, 8, 2, true, -1);
                        }


                    } else if (sensorId == 9) {
                        if (goingUp) {
                            update(4, 9, 1, true, -1);
                        } else {
                            if(previousSensor == 10) {
                                update(-1, 9, 1, true, 4);
                            }
                        }


                    } else if (sensorId == 10) {
                        if (goingUp) {
                            update(-1, 10, 0, false, 3);
                        } else {
                            update(3, 10, 1, false, -1);
                        }


                    } else if (sensorId == 11) {
                        if (goingUp) {
                            update(-1, 11, 0, false, 3);
                        } else {
                            update(3, 11, 2, false, -1);
                        }


                    } else if (sensorId == 12) {
                        if (goingUp) {
                            update(5, 12, 0, false, -1);
                        } else {
                            update(-1, 12, 0, false, 5);
                        }


                    } else if (sensorId == 13) {
                        if (goingUp) {
                            update(5, 13, 0, false, -1);
                        } else {
                            update(-1, 13, 0, false, 5);
                        }


                    } else if (sensorId == 14) {
                        if (goingUp) {
                            update(-1, 14, 0, false, 5);
                        } else {
                            update(5, 14, 0, false, -1);
                        }


                    } else if (sensorId == 15) {
                        if (goingUp) {
                            update(-1, 15, 0, false, 5);
                        } else {
                            update(5, 15, 0, false, -1);
                        }


                    } else if (sensorId == 16) {
                        if (goingUp) {
                            update(3, 16, 1, false, -1);
                        } else {
                            update(-1, 16, 0, false, 3);
                        }


                    } else if ((sensorId == 17 || sensorId == 18)) {

                        if (goingUp) {
                            int s = speed;
                            try {

                                //Thread.sleep(7000 / Math.abs(s));
                                tsi.setSpeed(id, 0);
                                Thread.sleep(1000 + (20 * Math.abs(s)));
                                tsi.setSpeed(id, -s);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            speed = -s;
                            goingUp = !goingUp;


                        }
                    } else if ((sensorId == 19 || sensorId == 20)) {
                        if (!goingUp) {
                            int s = speed;
                            try {

                                //Thread.sleep(7000 / Math.abs(s));
                                tsi.setSpeed(id, 0);
                                Thread.sleep(1000 + (20 * Math.abs(s)));
                                tsi.setSpeed(id, -s);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            speed = -s;
                            goingUp = !goingUp;


                        }


                    }

                }


            } else if (sensorEvent.getStatus() == SensorEvent.INACTIVE) {

                previousSensor = sensId;


/*
        if(doSomething){

            boolean inList = false;
            for (Map.Entry<Integer, Integer> entry : sensorExitTrack.entrySet()) {

                if(sensId == entry.getKey()) inList = true;

            }

            if(inList){
                System.out.println("Sensor id för release: "+sensId);
                int releaseId = sensorExitTrack.get(sensId);

                Lab1.semaphores.get(releaseId).release();

            }

        }
*/


            }

        }

    }

}
