package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp (name= "NewerTeleOp Use This" )
public class TeliOpExample extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime landingTime = new ElapsedTime();

    /*



     */

    DcMotor frontRightDrive;
    DcMotor frontLeftDrive;
    DcMotor collector;
    DcMotor extender;
    DcMotor elbow;
    DcMotor lander; // positive : down, negative : up
    DcMotor turret;


    double leftPower;
    double rightPower;

    double collect;
    double spew;
    double turretCount=0;
    double power = 0.8;

    long timeSinceExtendingLander=0;





    @Override
    public void runOpMode(){

        //Assign hardwares
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        collector = hardwareMap.get(DcMotor.class, "collector");
        extender = hardwareMap.get(DcMotor.class, "extender");
        elbow= hardwareMap.get(DcMotor.class, "elbow");
        lander = hardwareMap.get(DcMotor.class, "lander");
        turret = hardwareMap.get(DcMotor.class, "turret");

        //Set correct directions to the motors
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        extender.setDirection(DcMotor.Direction.REVERSE);
        elbow.setDirection(DcMotor.Direction.FORWARD);


        elbow.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);







        telemetry.addData("Status", "Initialized");
        telemetry.addData(">", "Press Play to start Teleop");
        telemetry.update();




        // Do not use waitForStart() if you have Motorola phones.
        //waitForStart();
        while (!opModeIsActive() && !isStopRequested()) {
            telemetry.addData("status", "waiting for start command...");
            telemetry.update();
        }

        runtime.reset();
        landingTime.reset();

        //double GoBilda_TICKS_PER_REV = 1425.2;


        while(opModeIsActive()) {


            //assign controller buttons to actions
            leftPower = -gamepad1.left_stick_y;
            rightPower = -gamepad1.right_stick_y;

            collect = gamepad2.right_trigger;
            spew = gamepad2.left_trigger;

            //Set extender power
            //Will This work as stick will it work when held?\
            //Will this also work with two gamepads
            //Coach wants it this way??
            //Will it interfere with turrent see below code
            if (gamepad2.right_stick_y > 0.5 || gamepad1.dpad_down ) { // CONTRACTING
                extender.setPower(-0.8);
            } else if (gamepad2.right_stick_y < -0.5 || gamepad1.dpad_up ) { //EXTENDING
                extender.setPower(0.8);
            } else {
                extender.setPower(0);
            }


            //set power to motors
            frontRightDrive.setPower(rightPower);
            frontLeftDrive.setPower(leftPower);

            //if right trigger is pressed, we collect
            //if left trigger is pressed, we spew
            if (collect > 0) {
                collector.setPower(collect);
            } else if (spew > 0) {
                collector.setPower(-spew);
            } else {
                collector.setPower(0);
            }


            //dont know if this will work yet this is moving our turret clockwise for dpad right and counter clockwise for Dpad left
            //Will Dpad act as stick or will it only work when pressed not held?
            //I set limits to the turrets so it doesn't exceed a certain point
            //But we don't know the specific value of the point so we have to test
            if ( gamepad2.dpad_right || gamepad1.dpad_right) {
                //if(turretCount <= 5000) {
                    turret.setPower(1);
                    turretCount++;
                //}
            } else if ( gamepad2.dpad_left || gamepad1.dpad_left){
                //if(turretCount >= -5000) {
                    turret.setPower(-1);
                    turretCount--;
                //}
            } else {
                turret.setPower(0);
            }

            //set power to elbow
            if(gamepad2.left_stick_y < -0.5){
                //elbow.setTargetPosition(100);

                //when we lift th.e elbow the extender extends as well
                elbow.setPower(0.75);
                //extender.setPower(-0.25);

            }else if (gamepad2.left_stick_y > 0.5){
                //elbow.setTargetPosition(0);

                //when we lower the elbow the extender contracts also
                elbow.setPower(-0.4);
                //extender.setPower(0.2);
            } else {
                elbow.setPower(0);
                //extender.setPower(0);
            }




            //set power to lander


            if ( gamepad2.a || gamepad1.a ){//positive contracts, negative extends
                lander.setPower(1);

            } else if (gamepad2.y || gamepad1.y ){
               lander.setPower(-1);

               //Code for checking how much time does it take to adjust the lander when sensing
               if(landingTime.time() > 0.1)
                   timeSinceExtendingLander += landingTime.time();

               landingTime.reset();

            } else {
                lander.setPower(0);

                //Code for checking how much time does it take to adjust the lander when sensing
                if(landingTime.time() > 0.1)
                    timeSinceExtendingLander += landingTime.time();

                landingTime.reset();

            }



            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Time since extending lander:", timeSinceExtendingLander);
            //telemetry.addData("turretCount", turretCount + "\nturretCount cannot exceed +-5000");

            telemetry.update();


        }



    }

    void latch(){

        //if we automate the latching process, there will be less mistakes in latching
        //the robot must face back the lander in order the code to work


        goBackwards(500);
        turnRight(400);

        lander.setPower(-0.3);
        sleep(1500);
        lander.setPower(0);



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


}


