import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.ugens.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class illuminus_idea extends PApplet {



 
Minim minim;
AudioOutput out;
ArrayList<line> lines = new ArrayList();
ArrayList<ball> balls = new ArrayList();
ArrayList<ball> trashBalls = new ArrayList();

//global variables for physics!
float gravity = 0.5f;
float bounciness = 1;

public void setup(){
  //initalize stuff
  minim = new Minim( this );
  out = minim.getLineOut( Minim.MONO, 2048 );
  strokeWeight(10);
  
}

public void draw(){
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
public void mousePressed(){
  if(keyPressed) {
    lines.add(new line(mouseX, mouseY, mouseX, mouseY)); 
    makingNewLine = true;
  }
  else balls.add(new ball(mouseX,mouseY));
}

public void mouseDragged(){
  if(makingNewLine)  lines.get(lines.size()-1).p2 = new PVector(mouseX, mouseY);
}

public void mouseReleased() {
  if(makingNewLine) {
    lines.get(lines.size()-1).p2 = new PVector(mouseX, mouseY);
    makingNewLine = false;
  }
}

public void keyPressed(){
if(key == 'd') {
  println("resetting everything");
  lines.clear();
  balls.clear();
  makingNewLine = false;
}
}
class ball{
  
  //constructor and object variables. Nothing fun here.
  PVector pos,vel, acc;
  ToneInstrument instrument = new ToneInstrument( 0.1f, 0.1f );

  ball(float x, float y){
    pos = new PVector(x,y);
    vel = new PVector(0,0);
    acc = new PVector(0,gravity);
  }
  
  //function that draws ball
  public void display(){
    stroke(255,0,0);
    point(pos.x, pos.y);
  }
  
  //function to check collision and stuff!
  public void update(){
    
    //if the ball is offscreen, remove it
    if(!checkRemove()){
      
    //move ball
    vel.add(acc);
    pos.add(vel);
    
    //iterate through all lines to check for collision
    for(line l : lines){
      
      //calculate if the ball is intersecting with the line (this part is glitchy!)
      if(
      abs(this.pos.y - calculateY(this.pos.x, l)) < vel.mag() 
      &&(
      (this.pos.x > l.p1.x && this.pos.x < l.p2.x) ||
      (this.pos.x > l.p2.x && this.pos.x < l.p1.x)
      )
      ){
        //if it is, move the ball above the line
        pos.y = calculateY(this.pos.x, l) - vel.y;
        
        //calculate the new velocity
        vel.y = -vel.y * bounciness;
        vel.x = vel.x * bounciness;
        
        //try to calculate the angle of velocity
        vel.x = (l.p1.y - l.p2.y) / (l.p1.x - l.p2.x) * 10;
        
        //beep boop
        playSound();
      }
    }
    }
  }
  
  //calculate coorosponding Y coordinate from X using the two point graphing formula
  private float calculateY(float x, line l){
    return ((l.p2.y - l.p1.y)/(l.p2.x - l.p1.x)) * (x - l.p1.x) + l.p1.y;
  }
  
  //plays a sound based on current position and velocity
  private void playSound(){
    // set frequency to current position
    instrument.setFreq(pos.x);
    // arguments: max amplitude, attack time, decay time, sustain level, release time
    instrument.sineOsc.setAmplitude(map(vel.mag(),0,10,0,1));
    // arguments: total time note is on
    instrument.noteOn(0.001f);
  }
  
  //function to check if the ball is offscreen, add it to the list to be removed if true. Returns true if offscreen.
  private boolean checkRemove(){
    if(pos.x > width || pos.x < 0 || pos.y > height) {
      println("removing a ball");
      instrument.noteOff();
      trashBalls.add(this);
      return true;
    }
    return false;
  }
  
}
class ToneInstrument implements Instrument
{
  // create all variables that must be used througout the class
  Oscil sineOsc;
  ADSR  adsr;
  
  // constructor for this instrument
  ToneInstrument( float frequency, float amplitude )
  {    
    // create new instances of any UGen objects as necessary
    sineOsc = new Oscil( frequency, amplitude, Waves.SINE );
    adsr = new ADSR( 1, 0.01f, 0.3f, 0.0f, 0.5f , 0.01f);
    
    // patch everything together up to the final output
    sineOsc.patch( adsr );
  }
  
  public void setFreq(float freq){
    sineOsc.setFrequency(freq);
  }
  
  // every instrument must have a noteOn( float ) method
  public void noteOn( float dur )
  {
    // turn on the ADSR
    adsr.noteOn();
    // patch to the output
    adsr.patch( out );
   }
  
  // every instrument must have a noteOff() method
  public void noteOff()
  {
    // tell the ADSR to unpatch after the release is finished
    adsr.unpatchAfterRelease(out);
    // call the noteOff 
    adsr.noteOff();
  }
}
class line{
  PVector p1, p2;
  
  line(float x1, float y1, float x2, float y2){
    p1 = new PVector(x1, y1);
    p2 = new PVector(x2, y2);
  }
  
  public void display(){
    stroke(255);
    line(p1.x, p1.y, p2.x, p2.y);
  }
}
  public void settings() {  size(800,800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "illuminus_idea" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
