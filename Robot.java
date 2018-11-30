/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5996.robot;

//imports needed for camera
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import org.omg.CORBA.TRANSACTION_UNAVAILABLE;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

//required dependencies
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//PID controllers
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SPI;

//Commands 
import org.usfirst.frc.team5996.robot.commands.ControlledRotate_Command;
import org.usfirst.frc.team5996.robot.commands.Drive_Command;

//subsystems and sensors
import org.usfirst.frc.team5996.robot.subsystems.Drive_Subsystem;
import org.usfirst.frc.team5996.robot.subsystems.Rotate_Subsystem;
import org.usfirst.frc.team5996.robot.Sensors.Ultrasonic_Sensor;
import com.kauailabs.navx.frc.AHRS;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot  {
	
	//creates and instantiates different subsystems in Robot as objects of the subsystem 
	public static final Drive_Subsystem driveSubsystem = new Drive_Subsystem();
	public static final Ultrasonic_Sensor ultrasonic = new Ultrasonic_Sensor();
	public static final Rotate_Subsystem rotate = new Rotate_Subsystem();
	
	public static OI m_oi;

	public static DriveExecutor driveExecutor = new DriveExecutor();
	
	public static String gameData;
	
	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_oi = new OI();
		// m_chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", m_chooser);
		new Thread(() -> {
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(640, 480);
			camera.setFPS(20);
			camera.setExposureAuto();
			//CvSink cvSink = CameraServer.getInstance().getVideo();
			//CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);	
			//Mat source = new Mat();
			//Mat output = new Mat();
		
			while(!Thread.interrupted()) {
				//cvSink.grabFrame(source);
				//Imgproc.cvtColor(source, output, Imgproc.COLOR_BayerBG2RGB);
				//outputStream.putFrame(output);
			}
		}).start();
		//CameraServer server = CameraServer.getInstance();
		//server.startAutomaticCapture();
		
		Robot.rotate.gyroInit();
		//Robot.camera.setDefaultAngle();
		try {
			gameData = DriverStation.getInstance().getGameSpecificMessage();
		} catch(Exception e) {
			System.err.println("DIDNT FIND GAME MESSAGE");
		}
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		m_autonomousCommand = m_chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		driveExecutor.execute();
		m_oi.periodic();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		SmartDashboard.putNumber("Distance in CM", ultrasonic.getDistanceCM());
//		SmartDashboard.putNumber("Gyro angle", rotate.getAngle());
		driveExecutor.execute();
		m_oi.periodic();
	}
		
		
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
