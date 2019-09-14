/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name="Crator with Sampling. Not tested!!", group="New Codes")

public class AutoExample extends LinearOpMode {
    // Detector object
    private SamplingOrderDetector detector;
    private SamplingOrderDetector detector2;
    //private GoldDetector goldDetector;

    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor extender;
    DcMotor collector;
    DcMotor elbow;
    DcMotor lander;
    DcMotor turret;





    double collectorPower = 0.4;
    double power = 0.7;
    double fastPower = 1;
    ElapsedTime passedTime;


    @Override
    public void runOpMode() {

        //we spend 20.4 seconds for everything except sensing

        // Setup detector
        detector = new SamplingOrderDetector(); // Create the detector
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance()); // Initialize detector with app context and camera
        detector.useDefaults(); // Set detector to use default settings

        detector.downscale = 0.4; // How much to downscale the input frames

        // Optional tuning
        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector.maxAreaScorer.weight = 0.001;

        detector.ratioScorer.weight = 15;
        detector.ratioScorer.perfectRatio = 1.0;

        // Setup detector2
        detector2 = new SamplingOrderDetector(); // Create the detector
        detector2.init(hardwareMap.appContext, CameraViewDisplay.getInstance()); // Initialize detector with app context and camera
        detector2.useDefaults(); // Set detector to use default settings

        detector2.downscale = 0.4; // How much to downscale the input frames

        // Optional tuning
        detector2.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector2.maxAreaScorer.weight = 0.001;

        detector2.ratioScorer.weight = 15;
        detector2.ratioScorer.perfectRatio = 1.0;




        ElapsedTime runtime = new ElapsedTime();



        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        collector = hardwareMap.get(DcMotor.class, "collector");
        extender = hardwareMap.get(DcMotor.class, "extender");
        elbow = hardwareMap.get(DcMotor.class, "elbow");
        lander = hardwareMap.get(DcMotor.class, "lander");
        turret = hardwareMap.get(DcMotor.class, "turret");


        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        extender.setDirection(DcMotor.Direction.REVERSE);
        elbow.setDirection(DcMotor.Direction.FORWARD);

        elbow.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



        telemetry.addData("Status", "Initialized");
        telemetry.addData(">", "Press Play to start Auto");
        telemetry.update();


        // Do not use waitForStart() if you have Motorola phones.
        //waitForStart();
        while (!opModeIsActive() && !isStopRequested()) {
            telemetry.addData("status", "waiting for start command...");
            telemetry.update();
        }
        runtime.reset();


        if(opModeIsActive()) {

            //Get the gold position (UNKNOWN, LEFT, RIGHT, CENTER)
            String goldPosition;



            //we land
            land();


            //Turn around 180 degrees for sensing
            turnLeft(1100);
            stopRobot();

            //Also adjust the height of the lander
            //must test the best height








            passedTime = new ElapsedTime();
            passedTime.reset();
            int leftCount = 0;
            int rightCount = 0;
            int centerCount = 0;
            int unknownCount = 0;

            boolean isInLeft = false;
            boolean isInRight = false;


            detector.enable();

            while(passedTime.time() < 2){
                if(detector.goldIsFound()) {
                    isInRight = true;
                }else{
                    isInRight = false;
                }

                telemetry.addData("IsInRight", isInRight);
            }

            detector.disable();


            turnLeft(300);
            stopRobot();
            detector2.enable();

            while(passedTime.time() < 4){
                if(detector2.goldIsFound()) {
                    isInLeft = true;
                }else{
                    isInRight = false;
                }
                telemetry.addData("IsInRight", isInRight);
                telemetry.addData("IsInLeft", isInLeft);
            }



            if(isInLeft && isInRight){
                goldPosition = "CENTER";
            } else if (isInLeft && !isInRight){
                goldPosition = "RIGHT";
            } else if (!isInLeft && isInRight){
                goldPosition = "LEFT";
            } else { // UNKNOWN
                goldPosition = "CENTER";// default
            }

            turnRight(100);

            //we sensed everything so we don't need doge cv
            detector2.disable();



            //start contracting the lander
            lander.setPower(1);

            goBackwards(200);
            turnRight(1300);//1500 match4


            passedTime.reset();



            //go forward and get in a position where we can see all three minerals

            if (goldPosition == "LEFT") {
                turnLeft(400);
                goFoward(900);

            } else if (goldPosition == "RIGHT") {
                turnRight(400);
                goFoward(900);

            } else if (goldPosition == "CENTER") {

                goFoward(1000);
                stopRobot();
                sleep(300);

            }


            //go forward to push the gold after sensing.
            //Note that we don't have to turn if the gold is in the middle-
            //We assume that we always detect the gold (we don't think about when goldposition = undetected)



            //move back to original position
            if(goldPosition == "LEFT"){

                goBackwards(900);
                turnRight(400);

            }else if (goldPosition == "RIGHT"){

                goBackwards(900);
                turnLeft(400);

            } else if (goldPosition == "CENTER") {

                goBackwards(1000);
                stopRobot();
                sleep(300);

            }

            stopRobot();

//20.3s
            //4300

            //now we are at the starting position. Now we have to claim

            //go forward

            turnLeft(530);
            goFoward(1800);//1750 match1
            turnRight(850);
            goFoward(2050);
//+4930



            stopRobot();

            sleep(70);
            lander.setPower(0);







//code for no sensing
            /*
            goFoward(1000);
            dash(400);
            dashBack(400);
            goBackwards(500);

            turnLeft(800);
            goFoward(1500);
            turnLeft(300);
            goFoward(1200);
            stopRobot();
*/



        }
    }







    void goFoward(long milliseconds) {

        frontLeftDrive.setPower(power);
        frontRightDrive.setPower(power);


        sleep(milliseconds);
    }

    void dash(long milliseconds) {

        frontLeftDrive.setPower(fastPower);
        frontRightDrive.setPower(fastPower);


        sleep(milliseconds);
    }


    void dashBack(long milliseconds) {

        frontLeftDrive.setPower(-fastPower);
        frontRightDrive.setPower(-fastPower);


        sleep(milliseconds);
    }



    void turnRight(long milliseconds) {

        frontLeftDrive.setPower(power);
        frontRightDrive.setPower(-power);


        sleep(milliseconds);
    }

    void turnLeft(long milliseconds) {

        frontLeftDrive.setPower(-power);
        frontRightDrive.setPower(power);

        sleep(milliseconds);
    }

    void goBackwards(long milliseconds) {

        frontLeftDrive.setPower(-power);
        frontRightDrive.setPower(-power);
        sleep(milliseconds);
    }

    void stopRobot(){
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
    }



    void land(){


        //descend
        lander.setPower(-1);
        sleep(9600);


        //We assume we land


        //Stop movement of lander
        lander.setPower(0);

        turnRight(300);
        goFoward(200);
        turnLeft(300);
        stopRobot();
    }

    void extendLander(long ms){
        lander.setPower(-1);
        sleep(ms);
        lander.setPower(0);
    }

    void contractLander(long ms){
        lander.setPower(1);
        sleep(ms);
        lander.setPower(0);
    }








}
