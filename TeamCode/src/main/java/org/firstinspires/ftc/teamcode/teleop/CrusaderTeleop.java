package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.hardware.CrusaderHardware;

@TeleOp(name="Teleop", group="Pushbot")
//@Disabled
public class CrusaderTeleop extends OpMode {

    /* Declare OpMode members. */
    CrusaderHardware robot = new CrusaderHardware(); // use the class created to define a Pushbot's hardware
    final double COUNTS_PER_MOTOR_REV = 480;    // eg: TETRIX Motor Encoder
    final double DRIVE_GEAR_REDUCTION = 1.0;     // This is < 1.0 if geared UP
    final double WHEEL_DIAMETER_INCHES = 4;     // For figuring circumference
    final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    final double DRIVE_SPEED = 2;
    boolean collectorDropped = false;
    boolean raisingStatus = false;
    int height;
    //int MAX_EXSTENSION= -3429;
    int MAX_ROTATION = -937;



    int driveMode= 0;

    //Code +to run ONCE when the driver hits INIT

    @Override
    public void init() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Mr.Driver");    //
        robot.armExtension.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.armRotate.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftRearDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.armExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.armRotate.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        robot.rightRearDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftRearDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        robot.armRotate.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); //automatically will ty to hold it
    }

    //Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY

    @Override
    public void init_loop() {

        //Code to run ONCE when the driver hits PLAY

//        @Override
//        public void start() {
//        }

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
//        @Override
    public void loop() {
        // Mechinum Wheel Code Written by Zack
        double drive = -0.8 * gamepad1.left_stick_y;   //0.75
        double strafe = 0.8 * gamepad1.left_stick_x; //opposite on ROTC
        double twist = 0.7 * gamepad1.right_stick_x; //Opposite on ROTC
        // Formula to allow Mechinum wheels to strafe and spin
        double[] speeds = {
                (drive + strafe + twist), // + + // - -
                (drive - strafe + twist), // - - // + -
                (drive - strafe - twist), // - + // + +
                (drive + strafe - twist) // + - // - +
        };

        //makes the speed controlled by each wheel
        double max = Math.abs(speeds[0]);
        for (int i = 0; i < speeds.length; i++) {
            if (max < Math.abs(speeds[i])) max = Math.abs(speeds[i]);
        }

        if (max > 1) {
            for (int i = 0; i < speeds.length; i++) speeds[i] /= max;
        }
        // Assigning each wheel to a item in the speed list
        robot.leftFrontDrive.setPower(speeds[0]); // originally -speeds[0]
        robot.leftRearDrive.setPower(speeds[1]); // originally -speeds[1]
        robot.rightFrontDrive.setPower(speeds[2]); // originally -speeds[2]
        robot.rightRearDrive.setPower(speeds[3]); // originally -speeds[3]
        //robot.armExtension.setPower (speeds [4]);

      /*(robot.armExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


       if ((gamepad2.right_stick_y) < -0.3 && robot.armExtension.getCurrentPosition() < 3429){
           robot.armExtension.setPower(.5);
               robot.armExtension.setPower(gamepad2.right_stick_y);

       }
       */



        telemetry.addData("Pivot Position", robot.armRotate.getCurrentPosition());


        if(Math.abs(robot.armRotate.getCurrentPosition()) < 200 && robot.armExtension.getCurrentPosition() >= 2000 &&
                gamepad2.right_stick_y < 0){
            telemetry.addData("at limit", 1);
            robot.armExtension.setPower(0);
        }

        else if (Math.abs(gamepad2.right_stick_y) > 0 )
        {
            robot.armExtension.setPower(gamepad2.right_stick_y * 0.5 );
        }


        else{
            robot.armExtension.setPower(0);
        }


        if(Math.abs(gamepad2.left_stick_y) > 0)//if the joystick is in use then we want to use manual power
        {
            robot.armRotate.setPower(gamepad2.left_stick_y * 0.2); //joystick with dampening because it is too fast
        }
        else{ //if joystick not in use we need to still hold power for the arm rotate motor
            robot.armRotate.setPower(0);
        }

// Claw controller code:
        if (gamepad2.b == true ) { //partial open claw
            robot.claw.setPosition(0.15);
        }

        if (gamepad2.right_bumper == true ) { //makes right trigger boolean and sets claw to fully open
            robot.claw.setPosition(0.5);
        }


        if (gamepad2.left_bumper == true ) { //closes claw fully
            robot.claw.setPosition(0);
        }




       /*if (gamepad2.b == true) {
           robot.claw.setPosition (0.5);

           robot.armExtension.setTargetPosition(1151);
           robot.armExtension.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           robot.armExtension.setPower(-0.5);
           robot.armRotate.setTargetPosition(0);
           robot.armRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           robot.armRotate.setPower(0);
           robot.armExtension.setPower(gamepad2.right_stick_y);

       }

       if (gamepad2.a== true) {
           robot.claw.setPosition (0.5);
           robot.armExtension.setTargetPosition(1151);
           robot.armExtension.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           robot.armExtension.setPower(0.7);
           robot.armRotate.setTargetPosition(0);
           robot.armRotate.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           robot.armRotate.setPower(0);
           robot.armExtension.setPower(gamepad2.right_stick_y);

       }
*/

// telemetry data:

        //Shows position of robot and extremities
        telemetry.addData("Left Drive Encoder position:", robot.leftFrontDrive.getCurrentPosition());
        telemetry.addData("Right Drive Encoder position:", robot.rightFrontDrive.getCurrentPosition());
        telemetry.addData("Rear Drive Encoder position:", robot.rightRearDrive.getCurrentPosition());
        telemetry.addData("Lift position:", robot.armRotate.getCurrentPosition());
        telemetry.addData("Extension position:", robot.armExtension.getCurrentPosition());

        telemetry.update();



    }
    @Override
    public void stop () {
    }
}



//Collector Arm Rotate Control

       /*if (gamepad2.dpad_up)
           robot.armExtension.setPosition(.45);
       if (gamepad2.dpad_up)
           robot.collectorArmRotateTwo.setPosition(.75);

       if (gamepad2.a)
           robot.bottomCollectorSpinChain.setPosition(2);
       if (gamepad1.a)
           robot.bottomCollectorSpinChain.setPosition(2);
       */

// Crane Control
      /* if (gamepad1.right_trigger > 0) {
           robot.armExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

           robot.armExtension.setPower(gamepad2.right_trigger);
       } else {
           robot.armExtension.setPower(0);}

       //    if (gamepad2.right_trigger > 0) {
       //        robot.craneTwo.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       //        robot.craneTwo.setPower(-gamepad2.right_trigger);
       //    } else {
       //        robot.craneTwo.setPower(0);
       //    }



       if (gamepad1.left_trigger > 0) {
           robot.armExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

           robot.armExtension.setPower(-gamepad1.left_trigger);
       } else {
           robot.armExtension.setPower(0);
       }



       //if (gamepad2.left_trigger > 0) {
       //    robot.craneTwo.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       //    robot.craneTwo.setPower(gamepad2.left_trigger);
       //} else {
       //    robot.craneTwo.setPower(0);
       //}

       //crane rotation
       //if (gamepad2.left_stick_x > 0) {
       //    robot.craneRotateChain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       //    robot.craneRotateChain.setPower(gamepad2.left_stick_x);}
       //else {
       //    robot.craneRotateChain.setPower(0);
       //}
       //if (gamepad2.left_stick_x < 0) {
         //  robot.craneRotateChain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
           //robot.craneRotateChain.setPower(gamepad2.left_stick_x);}
       //else {
         //  robot.craneRotateChain.setPower(0);}


        //Collector Arm Rotate Control

      if (gamepad2.dpad_up)
          robot.armExtension.setPosition(.45);
       if (gamepad2.dpad_up)
          robot.collectorArmRotateTwo.setPosition(.75);

       if (gamepad2.a)
           robot.bottomCollectorSpinChain.setPosition(2);
       if (gamepad1.a)
           robot.bottomCollectorSpinChain.setPosition(2);
*/



//if (gamepad2.dpad_left)
      /* robot.bottomCollectorWholeSpin.setPosition(.39);
       if (gamepad1.dpad_left)
           robot.bottomCollectorWholeSpin.setPosition(.40);
       //if (gamepad2.dpad_up){
       //    robot.clawRight.setPosition(.5);} //.4
       //else{
       //    robot.clawRight.setPosition(.75);} //.25

       //if (gamepad2.dpad_down){


       if (gamepad2.dpad_up)
           robot.bottomCollectorWholeSpin.setPosition(.25);
       if (gamepad1.dpad_up)
           robot.bottomCollectorWholeSpin.setPosition(.25);
*/
//    robot.clawLeft.setPosition(.4); //.25
//}
//else {
//    robot.clawLeft.setPosition(.25); //.5
//}
//plane launch code
//if (gamepad2.dpad_right){
//    robot.planeLaunch.setPosition(0);}
//else {
//    robot.planeLaunch.setPosition(1);
//}



//Heights for crane achieved by dpad inputsj
//Height to stack on small towers
//if (gamepad1.right_trigger > 0) {robot.craneRotateChain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//  robot.craneRotateChain.setPower(- gamepad1.right_trigger);}
//else {
//  robot.craneRotateChain.setPower(0);}

//if (gamepad1.left_trigger > 0) {robot.craneRotateChain.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//      robot.craneRotateChain.setPower(gamepad1.left_trigger);}
//else {
//  robot.craneRotateChain.setPower(0);}

// telemetry.addLine("Value: " + (robot.swing.getCurrentPosition()));
// telemetry.update();
//}
//Approach height for collecting
// if (gamepad2.x) {
// robot.crane.setTargetPosition(-3220+height);
//robot.crane.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//robot.claw.setPosition(.2);
// robot.crane.setPower(10);


// if (gamepad2.a) {
//robot.swing.setTargetPosition(0);
// robot.swing.setMode(DcMotor.RunMode.RUN_TO_POSITION);
// robot.swing.setPower(50);
// else if (gamepad2.y) {
// robot.swing.setTargetPosition(44);
//robot.swing.setMode(DcMotor.RunMode.RUN_TO_POSITION);
// robot.swing.setPower(50);
// else {robot.swing.setPower(0);}

//Height for short tower
//if (gamepad2.dpad_down){
//robot.craneOne.setTargetPosition(-5620+height);
//robot.craneOne.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//robot.craneOne.setPower(10);}

//if (gamepad2.dpad_down){
//  robot.craneTwo.setTargetPosition(-5620+height);
//    robot.craneTwo.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//  robot.craneTwo.setPower(10);}


//Height to stack on tallest tower
//if (gamepad2.dpad_up) {
//  robot.crane.setTargetPosition(-8300+height);
//robot.crane.setMode(DcMotor.RunMode.RUN_TO_POSITION);
// robot.crane.setPower(10);


//Height to stack on medium tower
//if (gamepad2.dpad_left){
//  robot.crane.setTargetPosition(-7715+height);
// robot.crane.setMode(DcMotor.RunMode.RUN_TO_POSITION);
// robot.crane.setPower(10);


//Height to collect off of ground
//if (gamepad2.dpad_right){
//  robot.crane.setTargetPosition(-2610+height);
// robot.crane.setMode(DcMotor.RunMode.RUN_TO_POSITION);
// robot.crane.setPower(10);


//        telemetry.addData("Starting at",  "%7d :%7d",
//                robot.crane.getCurrentPosition(),
//        telemetry.update();
//
//
//            int targetHeight;
//
//            targetHeight = robot.crane.getCurrentPosition() + (int)(craneInches * COUNTS_PER_INCH);
//
//            robot.crane.setTargetPosition(targetHeight);
//
//            robot.crane.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//            robot.crane.setPower(Math.abs(speed));
//
//            robot.crane.setPower(0);
//
//            robot.crane.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//                }


//Heights for the crane
//        int height = robot.crane.getCurrentPosition();
//        robot.crane.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        if (gamepad2.b)
//            robot.crane.setPower(500);
//        if (gamepad2.y)
//            robot.crane.setPower(1000);
//        if (gamepad2.a)
//            robot.crane.setPower(1500);
//        if (gamepad2.x)
//            robot.crane.setPower(250);


//    int height = robot.crane.getCurrentPosition();
//    int height0 = 0;
//    int height1 = 5000;
//    int height2 = 10000;
//    int craneTarget;
//
//        robot.crane.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        robot.crane.setPower(DRIVE_SPEED);
//
//    }
//
//    @Override
//    public void loop() {
//
//    }
//
//    // Height 1
//         if (gamepad1.b) {
//        int craneTarget = robot.crane.getCurrentPosition() + (int);
//        robot.crane.setTargetPosition(craneTarget);
//        robot.crane.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        robot.crane.setPower(DRIVE_SPEED);
//                telemetry.addLine("Level: 1 (Middle)\n Target: " + (height0 - height));
//
//    }
//
//    // Height 2
//        if (gamepad1.y) {
//        Object height;
//        int craneTarget = robot.crane.getCurrentPosition() + (int) ;
//        robot.crane.setTargetPosition(craneTarget);
//        robot.crane.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        robot.crane.setPower(DRIVE_SPEED);
//                telemetry.addLine("Level: 2 (Highest)\n Target: " + (height0 - height));
//    }

/*
 * Code to run ONCE after the driver hits STOP
 */



