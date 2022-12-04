package org.firstinspires.ftc.teamcode.robot.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.Constants;
import org.firstinspires.ftc.teamcode.util.CachingMotor;
import org.firstinspires.ftc.teamcode.util.PIDController;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.ArrayList;
import java.util.Map;

import com.acmerobotics.dashboard.config.Config;

@Config
public class Lift {

    public static final class LiftConstants {
        //encoder positions
        private static final int BOTTOM_ENCODER_TICKS = 0;
        private static final int LOW_POLE_ENCODER_TICKS = 330;
        private static final int MIDDLE_POLE_ENCODER_TICKS = 530;
        private static final int HIGH_POLE_ENCODER_TICKS = 720;
        private static final int JUNCTION_ENCODER_TICKS = 25;
        public static int COLLECTING_ENCODER_TICKS = 25;
        private static final int LIFT_POSITION_TOLERANCE = 8;
        private static final int CONE_CYCLE_POSITION_TOLERANCE = 3;

        //auto stack heights
        private static final int STACK_5_ENCODER_TICKS = 115;
        private static final int STACK_4_ENCODER_TICKS = 70;
        private static final int STACK_3_ENCODER_TICKS = 60;
        private static final int STACK_2_ENCODER_TICKS = 40; //FIXME
        private static final int STACK_1_ENCODER_TICKS = COLLECTING_ENCODER_TICKS;

        //cone cycle adjustments
        private static final int LIFT_ADJUSTMENT_LOW = -30;
        private static final int LIFT_ADJUSTMENT_HIGH = -60;

        //timings
        private static final int CYCLE_LIFT_DOWN_TIME_BOTTOM_MS = 200;
        private static final int CYCLE_LIFT_UP_TIME_BOTTOM_MS = 200;

        private static final int CYCLE_LIFT_DOWN_TIME_TOP_MS = 250;
        private static final int CYCLE_LIFT_UP_TIME_TOP_MS = 250;

        //motor id's
        private static final String LIFT_MOTOR_1_ID = "Lift-1";
        private static final String LIFT_MOTOR_2_ID = "Lift-2";
        private static final String LIFT_MOTOR_3_ID = "Lift-3";
        private static final String LIFT_MOTOR_4_ID = "Lift-4";

        //lift motor power
        private final double STAY_AT_POSITION_BOTTOM_POWER = 0.1;
        private final double STAY_AT_POSITION_TOP_POWER = 0.45;
        private final double GO_UP_LIFT_MOTOR_POWER = 1;
        private final double GO_UP_SLOW_LIFT_POWER = 0.8;
        private final double GO_DOWN_LIFT_POWER = -1;
        private final double GO_UP_CONE_CYCLE_BOTTOM_POWER = 1;
        private final double GO_DOWN_CONE_CYCLE_BOTTOM_POWER = -1;
        private final double GO_UP_CONE_CYCLE_TOP_POWER = 1;
        private final double GO_DOWN_CONE_CYCLE_TOP_POWER = -1;

        //lift motors reversed
        private static final boolean LIFT_MOTOR_1_REVERSED = false;
        private static final boolean LIFT_MOTOR_2_REVERSED = false;
        private static final boolean LIFT_MOTOR_3_REVERSED = false;
        private static final boolean LIFT_MOTOR_4_REVERSED = false;

        //lift PID constants
        private static final double PROPORTIONAL_COLLECTING_TO_HIGH = 0.015;
        private static final double INTEGRAL_COLLECTING_TO_HIGH = 0;
        private static final double DERIVATIVE_COLLECTING_TO_HIGH = 0.2;

        private static final double PROPORTIONAL_COLLECTING_TO_LOW = 0;
        private static final double INTEGRAL_COLLECTING_TO_LOW = 0;
        private static final double DERIVATIVE_COLLECTING_TO_LOW = 0;

    }

    public enum LiftHeight {
        //constants with encoder values
        BOTTOM(LiftConstants.BOTTOM_ENCODER_TICKS), LOW(LiftConstants.LOW_POLE_ENCODER_TICKS),
        MIDDLE(LiftConstants.MIDDLE_POLE_ENCODER_TICKS), HIGH(LiftConstants.HIGH_POLE_ENCODER_TICKS),
        JUNCTION(LiftConstants.JUNCTION_ENCODER_TICKS), COLLECTING(LiftConstants.COLLECTING_ENCODER_TICKS),
        STACK_5(LiftConstants.STACK_5_ENCODER_TICKS), STACK_4(LiftConstants.STACK_4_ENCODER_TICKS), STACK_3(LiftConstants.STACK_3_ENCODER_TICKS),
        STACK_2(LiftConstants.STACK_2_ENCODER_TICKS), STACK_1(LiftConstants.STACK_1_ENCODER_TICKS), TRANSITIONING(null);

        private Integer ticks;

        LiftHeight(Integer ticks) {
            this.ticks = ticks;
        }


        public Integer getTicks() {
            return this.ticks;
        }

    }

    //telemetry
    private Telemetry telemetry;

    //lift height state
    private LiftHeight desiredLiftHeight = LiftHeight.COLLECTING;

    //declare lift motors
    public DcMotorEx liftMotor1;
    public DcMotorEx liftMotor2;
    public DcMotorEx liftMotor3;
    public DcMotorEx liftMotor4;

    Constants constants = new Constants();

    public final String LIFT_SYSTEM_NAME = "Lift";
    public final String LIFT_PICKUP = "PICKUP";
    public final String LIFT_POLE_GROUND = "GROUND";
    public final String LIFT_POLE_DEPOSIT = "DEPOSIT";

    public final String LIFT_POLE_LOW = "POLE_LOW";
    public final String LIFT_POLE_MEDIUM = "POlE_MEDIUM";
    public final String LIFT_POLE_HIGH = "POLE_HIGH";
    public final String STACK_5 = "STACK_5";
    public final String STACK_4 = "STACK_4";
    public final String STACK_3 = "STACK_3";
    public final String REMOVE_STACK = "REMOVE_STACK";
    public final String LIFT_TARGET_HEIGHT = "LIFT TARGET HEIGHT";
    public final String APPROACH_HEIGHT = "APPROACH_HEIGHT";
    public final String PLACEMENT_HEIGHT = "PLACEMENT_HEIGHT";
    public final String LIFT_SUBHEIGHT = "SUB_HEIGHT";
    public final String LIFT_FINEADJ_DOWN = "LIFT_FINEADJ_DOWN";
    public final String LIFT_FINEADJ_UP = "LIFT_FINEADJ_UP";
    public final String LIFT_CYCLE_COLLECT_1 = "LIFT_CYCLE_COLLECT_1";
    public final String LIFT_CYCLE_COLLECT_2 = "LIFT_CYCLE_COLLECT_2";
    public final String LIFT_CYCLE_COLLECT_3 = "LIFT_CYCLE_COLLECT_3";
    public final String LIFT_CYCLE_COLLECT_4 = "LIFT_CYCLE_COLLECT_4";
    public final String LIFT_CYCLE_COLLECT_5 = "LIFT_CYCLE_COLLECT_5";
    public final String LIFT_DEPOSIT_IN_AUTO = "LIFT_DEPOSIT_IN_AUTO";

    public final String TRANSITION_STATE = "TRANSITION";
    public final int HEIGHT_TOLERANCE = 3;
    public final String LIFT_CURRENT_STATE = "LIFT CURRENT STATE";

    //declaring list of lift motors
    private ArrayList<DcMotor> liftMotors;

    //autonomous variable
    private boolean isAuto;

    public int LIFT_POSITION_GROUND = LiftConstants.COLLECTING_ENCODER_TICKS;

    //PID controller
    PIDController liftPIDController;

    private Map stateMap;

    public Lift(HardwareMap hardwareMap, Telemetry telemetry, Map stateMap, boolean isAuto) {

        //telemetry
        this.telemetry = telemetry;

        this.stateMap = stateMap;

        this.isAuto = isAuto;

        //initialize lift motors
        liftMotor1 = new CachingMotor(hardwareMap.get(DcMotorEx.class, LiftConstants.LIFT_MOTOR_1_ID));
        liftMotor2 = new CachingMotor(hardwareMap.get(DcMotorEx.class, LiftConstants.LIFT_MOTOR_2_ID));
        liftMotor3 = new CachingMotor(hardwareMap.get(DcMotorEx.class, LiftConstants.LIFT_MOTOR_3_ID));
        liftMotor4 = new CachingMotor(hardwareMap.get(DcMotorEx.class, LiftConstants.LIFT_MOTOR_4_ID));

        initializeLiftMotor(liftMotor1);
        initializeLiftMotor(liftMotor2);
        initializeLiftMotor(liftMotor3);
        initializeLiftMotor(liftMotor4);


        //creating list of lift motors for iteration
        liftMotors = new ArrayList<>();

        //lift PID controller
        this.liftPIDController = new PIDController(LiftConstants.PROPORTIONAL_COLLECTING_TO_HIGH, 0, 0);
        liftPIDController.setOutputBounds(0, 1);
        //setPIDControllerTransition(LIFT_POSITION_GROUND, LiftHeight.HIGH.getTicks());
        liftPIDController.setInputBounds(LiftHeight.COLLECTING.getTicks(), LiftHeight.HIGH.getTicks());
        resetPID();

        //add lift motors to list
        liftMotors.add(liftMotor1);
        liftMotors.add(liftMotor2);
        liftMotors.add(liftMotor3);
        liftMotors.add(liftMotor4);

        //setting lift behaviors
        for (DcMotor liftMotor : liftMotors) {
            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //setting directions
        liftMotor1.setDirection(LiftConstants.LIFT_MOTOR_1_REVERSED ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
        liftMotor2.setDirection(LiftConstants.LIFT_MOTOR_2_REVERSED ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
        liftMotor3.setDirection(LiftConstants.LIFT_MOTOR_3_REVERSED ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
        liftMotor4.setDirection(LiftConstants.LIFT_MOTOR_4_REVERSED ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
    }

    //////////////////
    //LIFT FUNCTIONS//
    //////////////////

    public void resetEncoders() {
        for (DcMotor liftMotor : liftMotors) {
            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

    public void resetPID() {
        liftPIDController.reset();
    }


    private void initializeLiftMotor(DcMotor liftMotor) {
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setDirection(DcMotor.Direction.FORWARD);
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void updateConeCycleState() {
        if (getPosition() > 400) {
            if (isCycleInProgress(constants.CYCLE_LIFT_DOWN) && isSubheightPlacement()) {
                if (haveLiftMotorsReachedTheirDesiredPositions(4) || isCycleExpired(LiftConstants.CYCLE_LIFT_DOWN_TIME_TOP_MS)) {
                    stateMap.put(constants.CYCLE_LIFT_DOWN, constants.STATE_COMPLETE);
                }
            } else if (isCycleInProgress(constants.CYCLE_LIFT_UP) && (haveLiftMotorsReachedTheirDesiredPositions(4) || isCycleExpired(LiftConstants.CYCLE_LIFT_UP_TIME_TOP_MS))) {
                stateMap.put(constants.CYCLE_LIFT_UP, constants.STATE_COMPLETE);
            }
        } else {
            if (isCycleInProgress(constants.CYCLE_LIFT_DOWN) && isSubheightPlacement()) {
                if (haveLiftMotorsReachedTheirDesiredPositions(4) || isCycleExpired(LiftConstants.CYCLE_LIFT_DOWN_TIME_BOTTOM_MS)) {
                    stateMap.put(constants.CYCLE_LIFT_DOWN, constants.STATE_COMPLETE);
                }
            } else if (isCycleInProgress(constants.CYCLE_LIFT_UP) && (haveLiftMotorsReachedTheirDesiredPositions(4) || isCycleExpired(LiftConstants.CYCLE_LIFT_UP_TIME_BOTTOM_MS))) {
                stateMap.put(constants.CYCLE_LIFT_UP, constants.STATE_COMPLETE);
            }
        }

    }

    private void selectTransition(String desiredLevel, String subheight, String currentState) {
        switch (desiredLevel) {
            case LIFT_POLE_LOW: {
                transitionToLiftPosition(LiftHeight.LOW.getTicks() + deliveryHeight(subheight));
                break;
            }
            case LIFT_POLE_MEDIUM: {
                transitionToLiftPosition(LiftHeight.MIDDLE.getTicks() + deliveryHeight(subheight));
                break;
            }
            case LIFT_POLE_HIGH: {
                transitionToLiftPosition(LiftHeight.HIGH.getTicks() + deliveryHeight(subheight));
                break;
            }
            case LIFT_POLE_GROUND: {
                transitionToLiftPosition(LIFT_POSITION_GROUND + deliveryHeight(subheight));
                break;
            }
            case LIFT_POLE_DEPOSIT: {
                transitionToLiftPosition(LIFT_POSITION_GROUND + deliveryHeight(subheight));
                break;
            }
            case LIFT_FINEADJ_UP: {
                transitionToLiftPosition(getAvgLiftPosition() + deliveryHeight(subheight));
                break;
            }
            case LIFT_FINEADJ_DOWN: {
                transitionToLiftPosition(getAvgLiftPosition() - deliveryHeight(subheight));
                break;
            }
            case STACK_5: {
                transitionToLiftPosition(LiftHeight.STACK_5.getTicks());
            }
            case STACK_4: {
                transitionToLiftPosition(LiftHeight.STACK_4.getTicks());
            }
            case STACK_3: {
                transitionToLiftPosition(LiftHeight.STACK_3.getTicks());
            }

        }

    }

    private void transitionToLiftPosition(int ticks) {
        raiseHeightTo(ticks);
    }

    public int deliveryHeight(String subheight) {
        int height = 0;
        if (subheight.equalsIgnoreCase(PLACEMENT_HEIGHT)) {
            height += LiftConstants.LIFT_ADJUSTMENT_LOW;
        }
        return height;
    }

    private double heightFactor(int heightInTicks) {
        double factor = Math.abs(0.0003 * heightInTicks) + 0.15;
        telemetry.addData("heightFactor", factor);
        return factor;
    }

    public void runAllMotorsToPosition(int position, double power) {
        for (DcMotor liftMotor : liftMotors) {
            liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            liftMotor.setTargetPosition(position);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftMotor.setPower(power);
        }
    }

    public void raiseHeightTo(int heightInTicks) {
        telemetry.addData("raiseHeightCalled", true);
        telemetry.addData("heightInTicks", heightInTicks);

        int position = getAvgLiftPosition();

        telemetry.addData("position", position);

        // calculate the error
        int error = heightInTicks - position;

        telemetry.addData("ERROR", error);
        telemetry.addData("Current Positions: ", getLiftPositions());
        telemetry.addData("Lift Power (M1): ", liftMotor1.getPower());
        telemetry.addData("Lift Power (M2): ", liftMotor2.getPower());
        telemetry.addData("Lift Power (M3): ", liftMotor3.getPower());
        telemetry.addData("Lift Power (M4): ", liftMotor4.getPower());

        telemetry.addData("Target Positions: ", getLiftTargetPositions());
        telemetry.update();


        if (isCycleInProgress(constants.CYCLE_LIFT_DOWN)) {
            telemetry.addData("Setting Raw Power; ", "NOOOO");
            telemetry.addData("Using Run To Position; ", "YESSSS");
            telemetry.update();
            if (getAvgLiftPosition() < 400) {
                setAllMotorPowers(-0.3);
            } else {
                runAllMotorsToPosition(heightInTicks + LiftConstants.LIFT_ADJUSTMENT_HIGH, 1);
            }
        } else if (isCycleInProgress(constants.CYCLE_LIFT_UP)) {
            telemetry.addData("Setting Raw Power; ", "NOOOO");
            telemetry.addData("Using Run To Position; ", "YESSSS");
            telemetry.update();
            runAllMotorsToPosition(heightInTicks, 1);
        } else if (position >= heightInTicks - 5 && position <= heightInTicks + 5) {
            if (heightInTicks > 400) {
                telemetry.addData("Setting Raw Power; ", "YESS");
                telemetry.addData("Using Run To Position; ", "NOO");
                telemetry.update();
                setAllMotorPowers(0.45);
            } else {
                telemetry.addData("Setting Raw Power; ", "YESS");
                telemetry.addData("Using Run To Position; ", "NOOO");
                telemetry.update();
                setAllMotorPowers(0.2);
            }
        } else if (position > heightInTicks) {
            if (position > heightInTicks + 200) {
                telemetry.addData("Setting Raw Power; ", "YES");
                telemetry.addData("Using Run To Position; ", "NO");
                telemetry.update();
                setAllMotorPowers(-0.1);
            } else if (position < 35 && heightInTicks < 35) {
                telemetry.addData("Setting Raw Power; ", "YES");
                telemetry.addData("Using Run To Position; ", "NO");
                telemetry.update();
                setAllMotorPowers(0);
            } else {
                telemetry.addData("Setting Raw Power; ", "NOOOO");
                telemetry.addData("Using Run To Position; ", "YESSSS");
                telemetry.update();
                runAllMotorsToPosition(heightInTicks, 1);
            }
        } else {
            /*if (position < heightInTicks - 15) {
                telemetry.addData("Setting Raw Power; ", "YESSS");
                telemetry.addData("Using Run To Position; ", "NOOOO");
                telemetry.update();
                setAllMotorPowers(1);
            } else {
                telemetry.addData("Using Run To Position; ", "YESSS");
                telemetry.addData("Setting Raw Power; ", "NOOOOO");
                telemetry.update();
                runAllMotorsToPosition(heightInTicks, 1);
            }*/
            //setPIDControllerTransition(LiftHeight.LOW.getTicks(), LiftHeight.HIGH.getTicks());
            setAllMotorPowers(liftPIDController.updateWithError(error) + 0.45);
        }

        telemetry.addData("Lift Motor Powers: ", getLiftMotorPowers());
    }

    /////////////////////
    //GETTERS & SETTERS//
    /////////////////////

    private void setPIDControllerTransition(int initialHeight, int desiredHeight) {
        if(initialHeight == liftPIDController.getLowerInputBound() && desiredHeight == liftPIDController.getUpperInputBound()){
            return;
        }

        double proportional = 0;
        double integral = 0;
        double derivative = 0;
        if (initialHeight < 200 && desiredHeight > 600) {
            proportional = LiftConstants.PROPORTIONAL_COLLECTING_TO_HIGH;
            integral = LiftConstants.INTEGRAL_COLLECTING_TO_HIGH;
            derivative = LiftConstants.DERIVATIVE_COLLECTING_TO_HIGH;
        } else if (initialHeight < 120 && desiredHeight > 200) {
            proportional = LiftConstants.PROPORTIONAL_COLLECTING_TO_LOW;
            integral = LiftConstants.INTEGRAL_COLLECTING_TO_LOW;
            derivative = LiftConstants.DERIVATIVE_COLLECTING_TO_LOW;
        } else {
            proportional = LiftConstants.PROPORTIONAL_COLLECTING_TO_LOW;
            integral = LiftConstants.INTEGRAL_COLLECTING_TO_LOW;
            derivative = LiftConstants.DERIVATIVE_COLLECTING_TO_LOW;
        }
        liftPIDController.setPIDValues(proportional, integral, derivative);
        liftPIDController.setInputBounds(initialHeight, desiredHeight);
    }

    private int getStateValue() {
        int position = 0;
        switch ((String) stateMap.get(LIFT_CURRENT_STATE)) {
            case LIFT_POLE_HIGH: {
                position = LiftHeight.HIGH.getTicks();
                break;
            }
            case LIFT_POLE_MEDIUM: {
                position = LiftHeight.MIDDLE.getTicks();
                break;
            }
            case STACK_5: {
                position = LiftHeight.STACK_5.getTicks();
                break;
            }
            case LIFT_POLE_LOW: {
                position = LiftHeight.LOW.getTicks();
                break;
            }
            case LIFT_POLE_GROUND: {
                position = LIFT_POSITION_GROUND;
                break;
            }
        }
        return position;
    }

    public String getCurrentState(String subheight) {
        String state = TRANSITION_STATE;
        double currentPosition = getPosition();
        if (inHeightTolerance(currentPosition, LIFT_POSITION_GROUND + deliveryHeight(subheight))) {
            state = LIFT_POLE_GROUND;
        } else if (inHeightTolerance(currentPosition, LiftHeight.LOW.getTicks() + deliveryHeight(subheight))) {
            state = LIFT_POLE_LOW;
        } else if (inHeightTolerance(currentPosition, LiftHeight.MIDDLE.getTicks() + deliveryHeight(subheight))) {
            state = LIFT_POLE_MEDIUM;
        } else if (inHeightTolerance(currentPosition, LiftHeight.HIGH.getTicks() + deliveryHeight(subheight))) {
            state = LIFT_POLE_HIGH;
        }
        return state;
    }

    public void setAllMotorPowers(double power) {
        for (DcMotor liftMotor : liftMotors) {
            liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            liftMotor.setPower(power);
        }
    }

    public int getPosition() {
        return liftMotor3.getCurrentPosition();
    }

    public void setState() {
        String subheight = (String) stateMap.get(LIFT_SUBHEIGHT);
        String currentState = getCurrentState(subheight);
        String level = (String) stateMap.get(LIFT_SYSTEM_NAME);

        stateMap.put(LIFT_CURRENT_STATE, currentState);

        updateConeCycleState();
        if (shouldLiftMove(level, currentState)) {
            if (stateMap.get(constants.LIFT_COMPLETE_TIME) == "0") {
                stateMap.put(constants.LIFT_START_TIME, String.valueOf(System.currentTimeMillis()));
            }
            selectTransition(level, subheight, currentState);
        } else {
            stateMap.put(constants.LIFT_COMPLETE_TIME, "0");
            stateMap.put(constants.LIFT_INTEGRAL_SUM, "0.0");
            setAllMotorPowers(heightFactor(getPosition()));
        }
    }

    public int getAvgLiftPosition() {
        double positionSum = 0;
        for (DcMotor liftMotor : liftMotors) {
            positionSum += liftMotor.getCurrentPosition();
        }
        return (int) (positionSum / liftMotors.size());
    }

    public ArrayList<Double> getLiftMotorPowers() {
        ArrayList<Double> liftMotorPowers = new ArrayList<>();
        for (DcMotor liftMotor : liftMotors) {
            liftMotorPowers.add(liftMotor.getPower());
        }
        return liftMotorPowers;
    }

    public ArrayList<Integer> getLiftTargetPositions() {
        ArrayList<Integer> liftTargetPositions = new ArrayList<>();
        for (DcMotor liftMotor : liftMotors) {
            liftTargetPositions.add(liftMotor.getTargetPosition());
        }
        return liftTargetPositions;
    }

    public ArrayList<Integer> getLiftPositions() {
        ArrayList<Integer> liftPositions = new ArrayList<>();
        for (DcMotor liftMotor : liftMotors) {
            liftPositions.add(liftMotor.getCurrentPosition());
        }
        return liftPositions;
    }

    //////////////
    //PREDICATES//
    //////////////

    private boolean isCycleExpired(int cycleTime) {
        return (System.currentTimeMillis() > Long.valueOf(String.valueOf(stateMap.get(constants.CONE_CYCLE_START_TIME))) + cycleTime);
    }

    private boolean isCycleInProgress(String cycleName) {
        return ((String) stateMap.get(cycleName)).equalsIgnoreCase(constants.STATE_IN_PROGRESS);
    }

    private boolean isSubheightPlacement() {
        return ((String) stateMap.get(LIFT_SUBHEIGHT)).equalsIgnoreCase(PLACEMENT_HEIGHT);
    }

    public boolean haveLiftMotorsReachedTheirDesiredPositions(int tolerance) {
        for (DcMotor liftMotor : liftMotors) {
            int currentPosition = liftMotor.getCurrentPosition();
            int desiredPosition = liftMotor.getTargetPosition();
            if (!(currentPosition <= desiredPosition + tolerance && currentPosition >= desiredPosition - tolerance)) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldLiftMove(String level, String currentState) {
        return ((String) stateMap.get(constants.CYCLE_LIFT_DOWN)).equalsIgnoreCase(constants.STATE_IN_PROGRESS) ||
                ((String) stateMap.get(constants.CYCLE_LIFT_UP)).equalsIgnoreCase(constants.STATE_IN_PROGRESS) ||
                !level.equalsIgnoreCase(currentState);
    }

    public boolean inHeightTolerance(double heightPosition, double targetHeight) {
        return (heightPosition > targetHeight - HEIGHT_TOLERANCE) && (heightPosition < targetHeight + HEIGHT_TOLERANCE);
    }

    public boolean hasLiftReachedPosition(int targetHeight, int tolerance) {
        return (getAvgLiftPosition() <= targetHeight + tolerance && getAvgLiftPosition() >= targetHeight - tolerance);
    }


}