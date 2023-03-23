// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IntakeArm.Feedforward;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Arm;

public class GoToAngleSmartWithPIDFeedforward extends CommandBase {

  private static final class Config{
    public static final double kP = 0.0125;
    public static final double kI = 0;
    public static final double kD = 0;

    public static final double kS = 0.0125;
    public static final double kG = 0;
    public static final double kV = 0;
    public static final double kA = 0;
  }

  private Arm m_arm;
  private PIDController m_pid = new PIDController(Config.kP, Config.kI, Config.kD);
  private ArmFeedforward m_feedforward = new ArmFeedforward(Config.kS, Config.kG, Config.kV, Config.kA);

  private double m_setpoint;
  private double m_encoderTicks;
  private double m_speed;
  private double m_default;
  private double m_velocity;
  private double m_accel;

  /** Creates a new GoToAngleSmartWithPIDFeedforward. */
  public GoToAngleSmartWithPIDFeedforward(Arm arm) {
    m_arm = arm;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_arm);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    SmartDashboard.putNumber("Arm/setpointTicks", m_default);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_setpoint = SmartDashboard.getNumber("Arm/setpointTicks", m_default);
    m_encoderTicks = m_arm.getEncoderTicks();
    m_speed = m_feedforward.calculate(m_setpoint, m_velocity, m_accel);
    if (m_speed > .5) m_speed = 0.5;
    SmartDashboard.putNumber("Calculated Speed", m_speed);

    
    m_arm.setSpeed(m_feedforward.calculate(m_setpoint, m_velocity, m_accel) + m_pid.calculate(m_arm.getEncoderTicks(), m_setpoint));
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_arm.setSpeed(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_arm.getEncoderTicks() >= m_setpoint;
  }
}
