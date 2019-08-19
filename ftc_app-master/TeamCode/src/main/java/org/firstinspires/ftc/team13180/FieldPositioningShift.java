package org.firstinspires.ftc.team13180;

import android.renderscript.ScriptGroup;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.Locale;
@TeleOp(name="FieldPositioningShift", group="manualmode")
public class FieldPositioningShift extends LinearOpMode {
    private RoboNavigator robonav;
    BNO055IMU imu;
    Orientation pos;
    double Turnpower=0.9;
    double MovePower=0.5;
    final double JoystickMargin=10;
    @Override
    public void runOpMode(){
        robonav=new RoboNavigator(this);
        robonav.init();

        BNO055IMU.Parameters param = new BNO055IMU.Parameters();
        param.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        param.calibrationDataFile = "BNO055IMUCalibration.json";
        param.loggingEnabled      = true;
        param.loggingTag          = "IMU";
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(param);
        waitForStart();
        while (opModeIsActive()){
            telemetry.update();
            double x=gamepad1.left_stick_x;
            double y=gamepad1.left_stick_y;
            if(Math.abs(x)>0 || Math.abs(y)>0) {          //when direction inputted
                double res = robonav.getAngle(x, y); //gets principal angle of joystick
                pos = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES); //get robot position
                double cur = Double.parseDouble(formatAngle(pos.angleUnit, pos.firstAngle)); //gets z angle (heading) in double format
                double mult=Math.sqrt(x*x+y*y);
                double ang=ImuToPrincipal(cur);
                double finalangle=ReceiveDifference(ang,res);
                robonav.AngAccMecanum(finalangle,mult,0);


            }
        }

    }
    double ImuToPrincipal(double ang){
        ang=360-ang;
        if(ang>270){
            ang-=270;
        }
        else{
            ang+=90;
        }
        return ang;
    }
    double ReceiveDifference(double CurPos,double FinPos){
        if(CurPos>FinPos){
            CurPos-=FinPos;
        }
        else{
            double diff=FinPos;
            CurPos+=FinPos;
        }
        return CurPos;
    }
    String formatAngle(AngleUnit angUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angUnit, angle));
    }

    String formatDegrees(double degrees){
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }
}
