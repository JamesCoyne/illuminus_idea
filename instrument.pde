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
    adsr = new ADSR( 1, 0.01, 0.3, 0.0, 0.5 , 0.01);
    
    // patch everything together up to the final output
    sineOsc.patch( adsr );
  }
  
  public void setFreq(float freq){
    sineOsc.setFrequency(freq);
  }
  
  // every instrument must have a noteOn( float ) method
  void noteOn( float dur )
  {
    // turn on the ADSR
    adsr.noteOn();
    // patch to the output
    adsr.patch( out );
   }
  
  // every instrument must have a noteOff() method
  void noteOff()
  {
    // tell the ADSR to unpatch after the release is finished
    adsr.unpatchAfterRelease(out);
    // call the noteOff 
    adsr.noteOff();
  }
}