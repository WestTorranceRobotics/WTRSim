package simulation.launch;

 class SimTable {}

// package frc5124.robot2021;

// import edu.wpi.first.networktables.NetworkTable;
// import edu.wpi.first.networktables.NetworkTableEntry;
// import edu.wpi.first.networktables.NetworkTableInstance;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.OutputStream;
// import java.net.ServerSocket;
// import java.net.Socket;
// import java.util.Arrays;
// import java.net.InetAddress;



// /**
//  * Network Table for Simulation
//  */
// public class SimTable extends Thread {
//     NetworkTableInstance simTable;
//     RobotContainer robotContainer;
//     Thread simSocket;
//     SimTable (RobotContainer robotContainer) {
//         this.robotContainer = robotContainer;
//     }

//       public void run() {
      
//         NetworkTableInstance simTable = NetworkTableInstance.create();
//         NetworkTable table = simTable.getTable("SimTable");
//         NetworkTableEntry entryRightLeaderPower = table.getEntry("RightLeaderPower");
//         NetworkTableEntry entryLeftLeaderPower = table.getEntry("LeftLeaderPower");
//         simTable.startClientTeam(5124);  // where TEAM=190, 294, etc, or use inst.startClient("hostname") or similar
//         simTable.startDSClient();  // recommended if running on DS computer; this gets the robot IP from the DS
//         simSocket = new SimSocket(simTable);
//         simSocket.start();

//         while (true) {
//           try {
//             Thread.sleep(10);
//           } catch (InterruptedException ex) {
//             System.out.println("interrupted");
//             return;
//           }

//           entryRightLeaderPower.setDouble(robotContainer.driveTrain.rightLeader.get());
//           entryLeftLeaderPower.setDouble(robotContainer.driveTrain.leftLeader.get());
//         }
//       }
// }

