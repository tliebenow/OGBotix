package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.teamUtil.robotConstants.poleHeightValues;

import static java.lang.Double.isNaN;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.teamUtil.configNames;
import org.firstinspires.ftc.teamcode.teamUtil.robotConfig;
import org.firstinspires.ftc.teamcode.teamUtil.robotConstants;

public class lift {
    robotConfig r;

    private static DcMotorEx lift0;
    private static DcMotorEx lift1;
    private static double liftPos;

    private static double currentTime; //needs to be set to runtime.time()
    private static double previousTime; //needs to be given a value of 0 at the start
    private static double previousError; //needs to be given a value of 0 at the start
    private static double I; //needs to be given a value of 0 at the start
    private static double targetPos; //needs to be set whenever you need it to be changed
    private static double spoolHoldPosition;
    private static boolean spoolHold;

    public lift(robotConfig r) {
        this.r = r;

        lift0 = r.hardwareMap.get(DcMotorEx.class, configNames.lift0);
        lift1 = r.hardwareMap.get(DcMotorEx.class, configNames.lift1);
        lift0.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        lift1.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        lift0.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        lift1.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        currentTime = 0;
        previousTime = 0;
        previousError = 0;
        I = 0;
        targetPos = 0;
        spoolHoldPosition = 0;
        spoolHold = false;
    }

    public static void readEncoder(){
        double lift0Pos = lift0.getCurrentPosition();
        double lift1Pos = lift1.getCurrentPosition();

        if(((lift0Pos + lift1Pos) / 2)<-1000) liftPos = Math.min(lift0Pos, lift1Pos);
        else if(((lift0Pos + lift1Pos) / 2)>=-1000) liftPos = Math.max(lift0Pos, lift1Pos);
        else liftPos = 0;
    }

    public void resetEncoder(){
        lift0.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        lift1.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        lift0.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        lift1.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void update(double time, double liftPositioner, boolean limitIsPressed, int button_pressed){
        currentTime = time;

        positionControl(liftPositioner, limitIsPressed, button_pressed);

        double currentError = (targetPos - liftPos);
        final double kP = 0.003;
        final double kI = 0.07;
        final double kD = 0.0003;

        double P = kP*currentError;
        I = I + (kI*(currentError*(currentTime-previousTime)));
        if (I < -0.5) {
            I = -0.5;
        }
        else if (I > 0){
            I = 0;
        }

        double D = kD*((currentError-previousError)/(currentTime-previousTime));

        double finalPower = (P + I + D);
        lift0.setPower(finalPower);
        lift1.setPower(finalPower);

        previousError = currentError;
        previousTime = currentTime;
    }

    private void positionControl(double liftPositioner, boolean limitIsPressed, int button_pressed){
        lift.targetPos = lift.targetPos +((int) (liftPositioner*100));
        if (isNaN(liftPositioner) && !spoolHold){
            spoolHoldPosition = lift.liftPos;
            spoolHold = true;
        }
        if(isNaN(liftPositioner)){
            lift.targetPos = spoolHoldPosition;
        }
        if (!isNaN(liftPositioner)){
            spoolHold = false;
        }
        if((lift.targetPos > lift.liftPos) && limitIsPressed){
            lift.targetPos = lift.liftPos;
        }
        if (lift.targetPos>0){
            lift.targetPos = 0;
        }
        if(lift.targetPos<poleHeightValues.getOrDefault(robotConstants.poleHeightNames.HIGH, -2750)-100) {
            lift.targetPos = poleHeightValues.getOrDefault(robotConstants.poleHeightNames.HIGH, -2750)-100;
        }
        switch (button_pressed){
            case 1:
                lift.targetPos = poleHeightValues.getOrDefault(robotConstants.poleHeightNames.HIGH, 0);
                spoolHoldPosition = poleHeightValues.getOrDefault(robotConstants.poleHeightNames.HIGH_DROP, 0);
                break;
            case 2:
                lift.targetPos = poleHeightValues.getOrDefault(robotConstants.poleHeightNames.MEDIUM, 0);
                spoolHoldPosition = poleHeightValues.getOrDefault(robotConstants.poleHeightNames.MEDIUM_DROP, 0);
                break;
            case 3:
                lift.targetPos = poleHeightValues.getOrDefault(robotConstants.poleHeightNames.LOW, 0);
                spoolHoldPosition = poleHeightValues.getOrDefault(robotConstants.poleHeightNames.LOW_DROP, 0);
                break;
            case 4:
                lift.targetPos = poleHeightValues.getOrDefault(robotConstants.poleHeightNames.GROUND, 0);
                spoolHoldPosition = poleHeightValues.getOrDefault(robotConstants.poleHeightNames.GROUND, 0);
                break;
            default:
                break;
        }
    }

    public int buttonAnalysis(boolean top, boolean mid, boolean low, boolean ground){
        if (top) return 1;
        if (mid) return 2;
        if (low) return 3;
        if (ground) return 4;
        return 0;
    }
}
