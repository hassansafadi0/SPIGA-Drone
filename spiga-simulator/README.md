# SPIGA Simulator (Simulateur de Planification et de Gestion d’Actifs Mobiles)

SPIGA is a Java-based simulator for managing a fleet of heterogeneous mobile assets (Aerial Drones, Marine Vehicles, Land Vehicles) in a dynamic 3D environment.

## Prerequisites

To run this project, you need:
*   **Java Development Kit (JDK) 17** or higher.
*   **Maven** (Build tool).

## Installation Guide

### 1. Install Java 17+ and Maven

#### 🪟 Windows
Using [Chocolatey](https://chocolatey.org/):
```powershell
choco install openjdk17 maven
```
Or download manually from [Adoptium](https://adoptium.net/) and [Apache Maven](https://maven.apache.org/download.cgi).

#### 🍎 macOS
Using [Homebrew](https://brew.sh/):
```bash
brew install openjdk@17 maven
```

#### 🐧 Linux (Debian/Ubuntu/Mint)
```bash
sudo apt update
sudo apt install openjdk-17-jdk maven
```

#### 🎩 Linux (RedHat/Fedora/CentOS)
```bash
sudo dnf install java-17-openjdk-devel maven
```

#### 🏹 Linux (Arch Linux/Manjaro)
```bash
sudo pacman -S jdk17-openjdk maven
```

### 2. Clone the Repository
```bash
git clone <repository-url>
cd spiga-simulator
```

## Building the Project

Run the following command in the project root to clean and build the application:

```bash
mvn clean package
```

## Running the Application

### 🖥️ Graphical User Interface (JavaFX)
This is the main interactive mode with visualization.

```bash
mvn javafx:run
```

**Controls:**
*   **Left-Click** on a vehicle to select it (Red ring).
*   **Left-Click** on the map to move the selected vehicle.
*   **Right-Click** on the map to move the selected vehicle.
*   **Dashboard**: Use the panel on the right to add new assets or set a global target.

### ⌨️ Command Line Interface (CLI)
A text-based mode for testing core logic.

```bash
mvn exec:java -Dexec.mainClass="com.spiga.ui.ConsoleInterface"
```

## Vehicle Types & Constraints

*   **DroneReconnaissance / DroneLogistique**: Aerial drones. Can fly over **Land** and **Sea**.
*   **VehiculeSurface (Boat)**: Marine vehicle. Can only operate in **Sea** (Blue area).
*   **VehiculeSousMarin (Submarine)**: Underwater vehicle. Can only operate in **Sea** (Blue area).
*   **VehiculeTerrestre (Car)**: Land vehicle. Can only operate on **Land** (Green area).

## Troubleshooting

*   **"Glitching" or Lag**: Ensure you are not running the CLI and GUI simultaneously in a way that conflicts, though they are separate. The simulation runs at ~10 FPS for stability.
*   **Marine Assets Stuck**: Ensure they are spawned in the Sea (X > 500).
*   **JavaFX Issues**: Ensure your JDK includes JavaFX or that Maven downloads the dependencies correctly (handled automatically by `pom.xml`).
