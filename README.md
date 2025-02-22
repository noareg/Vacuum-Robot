# 🤖 GurionRock Pro Max Vacuum Robot

This Java project simulates the perception and mapping system of an advanced vacuum robot. Developed as part of a systems programming course at Ben-Gurion University.

## 🚀 Features
- Concurrency and synchronization using Java threads and microservices.
- Simultaneous Localization and Mapping (SLAM) implementation.
- Data fusion from multiple sensors (Camera, LiDAR, IMU, GPS).
- Robust error handling and state tracking.

## 🛠️ How to Run
1. Clone the repository: git clone https://github.com/noareg/Vacuum-Robot.git
2. Build with Maven: mvn clean install
3. Run the simulation: mvn exec:java -Dexec.mainClass="bgu.spl.mics.Main" -Dexec.args="path/to/config.json"

## 📁 Project Structure
- `src/main/java/`: Core classes and microservices.
- `src/test/java/`: Unit tests (JUnit).
- `src/main/resources/`: Input JSON files for simulation.

## ✅ Requirements
- Java 8+
- Maven 3.6+

## 📄 License
For educational purposes only.
