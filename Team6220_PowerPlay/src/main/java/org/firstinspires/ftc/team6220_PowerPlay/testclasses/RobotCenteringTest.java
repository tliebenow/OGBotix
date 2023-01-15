package org.firstinspires.ftc.team6220_PowerPlay.testclasses;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.team6220_PowerPlay.Constants;

@Autonomous(name = "RobotCenteringTest", group = "Test")
public class RobotCenteringTest extends ConeDetection
{
    int[] lowerRed = {100, 150, 20};
    int[] upperRed = {140, 255, 255};
    @Override
    public void runOpMode() throws InterruptedException
    {
        detectGrab(lowerRed,upperRed);
        initialize();
        waitForStart();
        driveSlidesAutonomous(400);
        driveGrabber(Constants.GRABBER_OPEN_POSITION);
        while(opModeIsActive()){
            if(Math.abs(coneDetectionPipeline.distance) > 100){
                telemetry.addData("distance", coneDetectionPipeline.distance);
                telemetry.update();
                driveWithIMU(0.4*Math.signum(coneDetectionPipeline.distance),0,0);
            }
            driveWithIMU(0, 0, 0);
            if(coneDetectionPipeline.coneSize < 54000){
                driveWithIMU(0,0.4,0);
            }
            driveSlidesAutonomous(Constants.SLIDE_LOW);
            driveGrabber(Constants.GRABBER_CLOSE_POSITION);
        }
    }
}
