package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.common.TeleopBotBasic;
import org.firstinspires.ftc.teamcode.common.TeleopBotFancy;

@Config
@TeleOp(name = "TeleOpSample", group = "Linear OpMode")

public class TeleOpBasic extends LinearOpMode {

    private TeleopBotFancy bot;

    public static boolean loggingOn = false;

    @Override
    public void runOpMode() {

        double driveAxial = 0.0;
        double driveStrafe = 0.0;
        double driveYaw = 0.0;
        double leftTrigger = 0.0;
        double rightTrigger = 0.0;
        double liftPowerFactor = 0.5;

        bot = new TeleopBotFancy(hardwareMap, telemetry);
        waitForStart();
        bot.handlerRetract();
        while (opModeIsActive() && !isStopRequested()) {
            if (gamepad1.dpad_up) {
                bot.creepDirection(1.0, 0.0, 0.0);
            } else if (gamepad1.dpad_down) {
                bot.creepDirection(-1.0, 0.0, 0.0);
            } else if (gamepad1.dpad_left) {
                bot.creepDirection(0.0, 1.0, 0.0);
            } else if (gamepad1.dpad_right) {
                bot.creepDirection(0.0, -1.0, 0.0);
            } else {
                driveAxial = gamepad1.left_stick_y;
                driveStrafe = gamepad1.left_stick_x;
                driveYaw = gamepad1.right_stick_x;
                if ((Math.abs(driveAxial) < 0.2) && (Math.abs(driveStrafe) < 0.2) && (Math.abs(driveYaw) < 0.2)) {
                    bot.stopDrive();
                } else
                    bot.moveDirection(-driveAxial, -driveStrafe, -driveYaw);
            }

            leftTrigger = gamepad1.left_trigger;
            rightTrigger = gamepad1.right_trigger;
            if (leftTrigger > 0.3) {
                bot.liftManualDown(leftTrigger * liftPowerFactor);
            } else if (rightTrigger > 0.3) {
                bot.liftManualUp(rightTrigger * liftPowerFactor);
            } else {
                bot.liftStop();
            }
        }
    }

}


