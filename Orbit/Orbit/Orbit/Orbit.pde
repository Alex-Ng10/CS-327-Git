import java.util.*;
import java.awt.*;

Orbiter sun; // The root orbiter 
int lastMillis;
double[][] originM = {{0},{0},{1}};
Matrix origin = new Matrix(originM);

// A few fun parameters
boolean clearBackground = true; 
double speedModifier = 1.0; // Set in some of the scenes to change the speed. 

// One default scene (selected in the setup() function
void setupScene1() {
  
  sun = new Orbiter(null, 0, 0, 0, Orbiter.Type.CIRCLE, Color.yellow); 
  
  Orbiter earth = new Orbiter(sun, 50, 0, 1, Orbiter.Type.CIRCLE, Color.blue);
  Orbiter moon = new Orbiter(earth, 30, 0, 1, Orbiter.Type.CIRCLE, Color.gray);
  Orbiter moonSatellite = new Orbiter(moon, 20, 0, 1, Orbiter.Type.CIRCLE, Color.gray);
  
  Orbiter jupiter = new Orbiter(sun, 200, 0, 0.5, Orbiter.Type.CIRCLE, Color.red);
  Orbiter jupiterMoon = new Orbiter(jupiter, 75, 0, 2, Orbiter.Type.CIRCLE, Color.green);
  Orbiter jupiterExplorer = new Orbiter(jupiterMoon, 40, 0, -1, Orbiter.Type.SQUARE, Color.orange);
  Orbiter jupiterExplorerRobot = new Orbiter(jupiterExplorer, 20, 0, -3, Orbiter.Type.TRIANGLE, Color.magenta);
}

// A second default scene
void setupScene2() {
  speedModifier = 0.25;
  sun = new Orbiter(null, 0, 0, 0, Orbiter.Type.TRIANGLE, Color.yellow); 
  
  Orbiter earth = new Orbiter(sun, 50, 0, 10, Orbiter.Type.TRIANGLE, Color.blue);
  Orbiter moon = new Orbiter(earth, 30, 0, 20, Orbiter.Type.TRIANGLE, Color.gray);
  Orbiter moonSatellite = new Orbiter(moon, 20, 0, 30, Orbiter.Type.TRIANGLE, Color.gray);
  
  Orbiter jupiter = new Orbiter(sun, 200, 0, 5, Orbiter.Type.TRIANGLE, Color.red);
  Orbiter jupiterMoon = new Orbiter(jupiter, 75, 0, 2, Orbiter.Type.TRIANGLE, Color.green);
  Orbiter jupiterExplorer = new Orbiter(jupiterMoon, 40, 0, 8, Orbiter.Type.TRIANGLE, Color.orange);
  Orbiter jupiterExplorerRobot = new Orbiter(jupiterExplorer, 20, 0, -6, Orbiter.Type.TRIANGLE, Color.magenta);
}

// The setup. You don't need to edit this other than to switch scenes by commenting out
// the setupScene1() and uncommenting setupScene2(). 
void setup() {
  size(800, 800);
  background(0);
  setupScene1();
  //setupScene2();  // Run this one with clearBackground set to false
  lastMillis = millis(); 
}

// The draw function
// DO NOT EDIT
void draw() {
  if (clearBackground) background(0); // Make the background black. 
  
  int currentMillis = millis(); // Get the current number of milliseconds
  int elapsedMillis = currentMillis - lastMillis; // Get the number of milliseconds elapsed since last call
  double timeDelta = elapsedMillis / 1000.0; 
  
  updateOrbiters(timeDelta * speedModifier); 
  pushMatrix(); 
  scale(1, -1);
  translate(width / 2, - height / 2);
  drawOrbiters();
  popMatrix();
  
  lastMillis = currentMillis;
}

void updateOrbiters(double timeDelta) {
    
  // TODO
  // This code should traverse the orbiters (in BFS or DFS, but I used BFS) 
  // order using a stack or a queue (your choice),  and call updateRotation 
  // on each one using the timeDelta parameter. 
  //
  // Recall that Java has a Queue<T> data type and a Stack<T> interface

  Queue<Orbiter> queue = new LinkedList<>();
    queue.add(sun); // Start with the root orbiter
    while (!queue.isEmpty()) {
        Orbiter current = queue.poll();
        current.updateRotation(timeDelta);
        queue.addAll(current.getChildren());
    }
}

void drawOrbiters() {
  
  Queue<Orbiter> queue = new LinkedList<>();
    queue.add(sun); // Start with the root orbiter
    while (!queue.isEmpty()) {
        Orbiter current = queue.poll();
        drawOrbiter(current);
        queue.addAll(current.getChildren());
    }
}

// The code for drawing an orbiter. This is called from your drawOrbiters() method
// but you should not have to edit it. 
void drawOrbiter(Orbiter orbiter) {
  try {
    Matrix position = orbiter.getMatrix().dot(origin);
    
    int px = (int) Math.round(position.entry(0,0) / position.entry(2,0));
    int py = (int) Math.round(position.entry(1,0) / position.entry(2,0));
    
    // Draw the orbiter
    noStroke();
    fill(orbiter.getFillColor().getRed(), orbiter.getFillColor().getGreen(), orbiter.getFillColor().getBlue());
    switch (orbiter.getType()) {
      case CIRCLE: 
        ellipse(px, py, 16, 16);
      break;
      case SQUARE:
        rect(px-4, py-4, 8, 8);
      break;
      case TRIANGLE:
        triangle(px, py+3, px-2, py-1, px+2, py-1);
      break;
    }
    noFill();
    
    // Draw the orbit path
    if (clearBackground) {
      stroke(60);
      for (Orbiter child : orbiter.getChildren()) {
        int radius = (int) (2*child.getOrbitRadius());
        ellipse(px, py, radius, radius);
      }
    }
  } catch (UndefinedMatrixOpException umoe) {
  }
}