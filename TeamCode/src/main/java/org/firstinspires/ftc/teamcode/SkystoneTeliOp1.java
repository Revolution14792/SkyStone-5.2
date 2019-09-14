package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp (name= "NewerTeleOp Use This" )
public class SkystoneTeliOp1 extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime landingTime = new ElapsedTime();

    /*



     */

    DcMotor frontRightDrive;
    DcMotor frontLeftDrive;
    DcMotor backRightDrive;
    DcMotor backLeftDrive;


    double leftPower;
    double rightPower;








    @Override
    public void runOpMode(){

        //Assign hardwares
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");


        //Set correct directions to the motors
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);







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




        while(opModeIsActive()) {


            //assign controller buttons to actions
            leftPower = -gamepad1.left_stick_y;
            rightPower = -gamepad1.right_stick_y;






            //set power to motors
            frontRightDrive.setPower(rightPower);
            frontLeftDrive.setPower(leftPower);









            //set power to lander






            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Time since extending lander:", timeSinceExtendingLander);
            //telemetry.addData("turretCount", turretCount + "\nturretCount cannot exceed +-5000");

            telemetry.update();


        }



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


