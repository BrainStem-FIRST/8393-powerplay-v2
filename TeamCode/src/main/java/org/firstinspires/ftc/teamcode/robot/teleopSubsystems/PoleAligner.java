package org.firstinspires.ftc.teamcode.robot.teleopSubsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.Constants;
import org.firstinspires.ftc.teamcode.robot.Subsystem;
import org.firstinspires.ftc.teamcode.robot.teleopSubsystems.Lift;
import org.firstinspires.ftc.teamcode.util.CachingServo;

import java.util.Map;


public class PoleAligner implements Subsystem {
    private Telemetry telemetry;

    // Three servos (plus the turret) work together to place cone to desired location
    public ServoImplEx poleAligner;

    static final double MM_TO_INCHES = 0.0393700787;
    static final double MINIMUM_CLEARANCE_DISTANCE = 95.875 * MM_TO_INCHES;
    public final int LIFT_MIN_HEIGHT_TO_MOVE_EXTENSION = 75;


    // Servo Positions

    public final double EXTENSION_POSITION_HOME = 1880;
    public double EXTENSION_POSITION_MAX  = 2372;

    public double EXTENSION_EDITABLE_POSITION = 0.4;
    // extension statemap values
    public final String SYSTEM_NAME = "EXTENSION"; //statemap key
    public final String DEFAULT_VALUE = "RETRACTED";
    public final String FULL_EXTEND = "EXTENDED";
    public final String AUTO_EXTENSION_DEPOSIT = "AUTO_EXTEND_DEPOSIT";
    public final String TRANSITION_STATE = "TRANSITION";
    Constants constants = new Constants();

    private Map stateMap;

    private boolean isAuto;

    public PoleAligner(HardwareMap hwMap, Telemetry telemetry, Map stateMap, boolean isAuto) {
        this.telemetry = telemetry;
        this.stateMap = stateMap;
        this.isAuto = isAuto;

        poleAligner = new CachingServo(hwMap.get(ServoImplEx.class, "extension"));

        poleAligner.setPwmRange(new PwmControl.PwmRange(EXTENSION_POSITION_HOME, EXTENSION_POSITION_MAX));

        // Scale the operating range of Servos and set initial position
        extendHome();


    }

    @Override
    public void reset() {

    }

    @Override
    public void update() {

    }

    @Override
    public String test() {
        return null;
    }

    /************************* EXTENSION ARM UTILITIES **************************/

    // This method is intended for Teleop mode getting speed value coming from controller (-1..1)
    // Negative speed values will retract the extension arm.

    public void extend(double speed) {
        double currentPosition = poleAligner.getPosition();

        //scale speed value so the extension moves in increments of 10% of the range at max speed
        double targetPosition = Range.clip(currentPosition + speed*0.10, 0, 1);
        poleAligner.setPosition(targetPosition/EXTENSION_POSITION_MAX);
        //Send telemetry message for debugging purposes
    }


    // Pulls the extension arm to its starting position (it is NOT in clear)
    public void extendHome() {
        poleAligner.setPosition(0.01);
    }

    // Extends the arm to its maximum reach
    public void extendMax() {
        poleAligner.setPosition(EXTENSION_EDITABLE_POSITION);
    }

    public void extendToTarget() {
        poleAligner.setPosition(EXTENSION_EDITABLE_POSITION);
    }

    public void extendInAuto(double pos){
        poleAligner.setPosition(pos);
    }

    public void setState(String desiredState, Lift lift){
        if(isLiftTooLow(lift)){
            selectTransition((String) DEFAULT_VALUE);
        }
        else{
            selectTransition(desiredState);
        }

    }

    private void selectTransition(String desiredLevel){
        switch(desiredLevel) {
            case DEFAULT_VALUE: {
                extendHome();
                break;
            }
            case FULL_EXTEND: {
                extendToTarget();
                break;
            }
            case AUTO_EXTENSION_DEPOSIT: {
                extendInAuto(0.6);
                break;
            }
        }
    }

    public double getExtensionPosition() {
        return poleAligner.getPosition();
    }

    public boolean isLiftTooLow(Lift lift) {
        return lift.getPosition() < LIFT_MIN_HEIGHT_TO_MOVE_EXTENSION;
    }

}