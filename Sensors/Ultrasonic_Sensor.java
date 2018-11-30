package org.usfirst.frc.team5996.robot.Sensors;

import org.usfirst.frc.team5996.robot.Robot;

import edu.wpi.first.wpilibj.Ultrasonic;
public class Ultrasonic_Sensor extends Robot {
	
	Ultrasonic ultrasonic = new Ultrasonic(9,8);

	public Ultrasonic_Sensor() {
		ultrasonic.setAutomaticMode(true);
	}
	
	public double getDistanceCM() {
		return (ultrasonic.getRangeMM() / 10);
	}
}
