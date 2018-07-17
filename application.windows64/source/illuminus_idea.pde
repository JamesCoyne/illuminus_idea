import ddf.minim.*;
import ddf.minim.ugens.*;
 
Minim minim;
AudioOutput out;
ArrayList<line> lines = new ArrayList();
ArrayList<ball> balls = new ArrayList();
ArrayList<ball> trashBalls = new ArrayList();

//global variables for physics!
float gravity = 0.5;
float bounciness = 1;

void setup(){
  //initalize stuff
  minim = new Minim( this );
  out = minim.getLineOut( Minim.MONO, 2048 );
  strokeWeight(10);
  size(800,800);
}

void draw(){
  //draw background and all lines
  background(0);
  for(line l : lines) l.display();
  
  //draw all balls and update their positions
  for(ball b : balls){
    b.update();
    b.display();
  }
  
  //remove all balls offscreen
  balls.removeAll(trashBalls);
  trashBalls.clear();
}


//controls for making new balls and lines and stuff
boolean makingNewLine = false;
void mousePressed(){
  if(keyPressed) {
    lines.add(new line(mouseX, mouseY, mouseX, mouseY)); 
    makingNewLine = true;
  }
  else balls.add(new ball(mouseX,mouseY));
}

void mouseDragged(){
  if(makingNewLine)  lines.get(lines.size()-1).p2 = new PVector(mouseX, mouseY);
}

void mouseReleased() {
  if(makingNewLine) {
    lines.get(lines.size()-1).p2 = new PVector(mouseX, mouseY);
    makingNewLine = false;
  }
}

void keyPressed(){
if(key == 'd') {
  println("resetting everything");
  lines.clear();
  balls.clear();
  makingNewLine = false;
}
}
