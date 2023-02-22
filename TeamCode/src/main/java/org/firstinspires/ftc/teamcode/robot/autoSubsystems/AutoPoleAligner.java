package org.firstinspires.ftc.teamcode.robot.autoSubsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.Subsystem;
import org.firstinspires.ftc.teamcode.util.CachingServo;

import java.util.Map;


public class AutoPoleAligner implements Subsystem {

    private static final class PoleAlignerConstants {
        private static final int UP_POSITION = 2002;
        private static final int DOWN_POSITION = 743;
    }

    private Telemetry telemetry;

    public ServoImplEx poleAlignerServo;


    public final String SYSTEM_NAME = "POLE_ALIGNER";
    public final String DOWN = "DOWN";
    public final String UP = "UP";


    private Map stateMap;

    private boolean isAuto;

    public AutoPoleAligner(HardwareMap hwMap, Telemetry telemetry, Map stateMap) {

        this.telemetry = telemetry;
        this.stateMap = stateMap;


        poleAlignerServo = new CachingServo(hwMap.get(ServoImplEx.class, "rightTurret"));
        poleAlignerServo.setPwmRange(new PwmControl.PwmRange(PoleAlignerConstants.UP_POSITION, PoleAlignerConstants.DOWN_POSITION));

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

    public void setState(String desiredState) {
        switch(desiredState){
            case UP: {
                poleAlignerServo.setPosition(PoleAlignerConstants.UP_POSITION);
                break;
            }
            case DOWN: {
                poleAlignerServo.setPosition(PoleAlignerConstants.DOWN_POSITION);
                break;
            }
        }
    }
}