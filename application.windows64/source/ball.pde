class ball{
  
  //constructor and object variables. Nothing fun here.
  PVector pos,vel, acc;
  ToneInstrument instrument = new ToneInstrument( 0.1, 0.1 );

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
    instrument.noteOn(0.001);
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
