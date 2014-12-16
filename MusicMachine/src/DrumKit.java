import jm.JMC;
import jm.music.data.*;
import jm.util.*;
import jm.music.tools.*;

/**
 * A short example which generates a drum kit pattern
 * and writes to a MIDI file called DrumKit.mid
 * It displays the separation of code into small methods.
 * @author Andrew Brown & Pedro Quinones=
 */

public final class DrumKit implements JMC{
    /////////////
    //Attributes
    /////////////
    private Score pattern1 = new Score("MusicMachine - Kit");
    // "DrumKit" = title, 0 = instrument (kit), 9 = MIDI channel 10
    private Part drums = new Part("DrumKit", 0, 9);
    private Phrase phrBD = new Phrase("BD", 0.0);
    private Phrase phrSD = new Phrase("SD", 0.0);
    private Phrase phrHHC = new Phrase("HH", 0.0);
    //define repeatedly used rests
    private Note restW = new Note(REST, WHOLE_NOTE);
    private Note restH = new Note(REST, HALF_NOTE);
    private Note restQ = new Note(REST, QUARTER_NOTE);
    private Note restE = new Note(REST, EIGHTH_NOTE);
    private Note restS = new Note(REST, SIXTEENTH_NOTE);

    ////////////
    //main
    ////////////
    public static void main(String[] args){
        //create an instance of this class
        DrumKit ek = new DrumKit();
    }
    //////////////
    //constructor
    //////////////
    public DrumKit() {
        //Let us know things have started
        System.out.println("Creating drum patterns . . .");

        // make bass drum
        this.doBassDrum();

        //snare drum
        this.doSnare();

        // make hats
        this.doHiHats();

        //Assemble the score
        this.doScore();

        Mod.repeat(pattern1, 2);

        // write the score to a MIDIfile
        Write.midi(pattern1, "DrumKit.mid");
        System.out.println("Tempo: " + pattern1.getTempo());
        Play.midi(pattern1);
    }

    ////////////
    //Methods
    ////////////
    private void doBassDrum() {
        //Create basic kick pattern
        System.out.println("Doing kick drum. . .");
//        for(short i=0;i<2;i++){
//            Note note = new Note(BASS_DRUM_1, QUARTER_NOTE);
//            phrBD.addNote(note);
//            phrBD.addNote(restQ);
//        }
        Note note = new Note(ACOUSTIC_BASS_DRUM, SIXTEENTH_NOTE);
        phrBD.addNote(note);//1
        phrBD.addNote(restS);//e
        phrBD.addNote(note);//&
        phrBD.addNote(note);//a
        phrBD.addNote(restS);//2
        phrBD.addNote(restS);//e
        phrBD.addNote(restS);//&
        phrBD.addNote(note);//a
        phrBD.addNote(restS);//3
        phrBD.addNote(note);//e
        phrBD.addNote(restS);//&
        phrBD.addNote(note);//a
        phrBD.addNote(restS);//4
        phrBD.addNote(restS);//e
        phrBD.addNote(restS);//&
        phrBD.addNote(restS);//a

        //phrBD.addNote(restW);
    }

    private void doSnare() {
        // make snare drum
        System.out.println("Doing snare. . .");
//        for(short i=0;i<2;i++){
//            phrSD.addNote(restQ);
//            Note note = new Note(ACOUSTIC_SNARE, QUARTER_NOTE);
//            phrSD.addNote(note);
//        }
        Note note = new Note(ACOUSTIC_SNARE, SIXTEENTH_NOTE);
        phrSD.addNote(restS);//1
        phrSD.addNote(restS);//e
        phrSD.addNote(restS);//&
        phrSD.addNote(restS);//a
        phrSD.addNote(note);//2
        phrSD.addNote(note);//e
        phrSD.addNote(restS);//&
        phrSD.addNote(restS);//a
        phrSD.addNote(restS);//3
        phrSD.addNote(restS);//e
        phrSD.addNote(restS);//&
        phrSD.addNote(restS);//a
        phrSD.addNote(note);//4
        phrSD.addNote(note);//e
        phrSD.addNote(restS);//&
        phrSD.addNote(restS);//a

        //phrSD.addNote(restW);
    }

    private void doHiHats() {
        System.out.println("Doing Hi Hats. . .");
//        for(short i=0;i<8;i++){
//            Note note = new Note(CLOSED_HI_HAT, EIGHTH_NOTE);
//            phrHHC.addNote(note);
//        }
        Note note = new Note(CLOSED_HI_HAT, SIXTEENTH_NOTE);
        phrHHC.addNote(note);//1
        phrHHC.addNote(note);//e
        phrHHC.addNote(note);//&
        phrHHC.addNote(restS);//a
        phrHHC.addNote(note);//2
        phrHHC.addNote(note);//e
        phrHHC.addNote(note);//&
        phrHHC.addNote(restS);//a
        phrHHC.addNote(note);//3
        phrHHC.addNote(note);//e
        phrHHC.addNote(note);//&
        phrHHC.addNote(restS);//a
        phrHHC.addNote(note);//4
        phrHHC.addNote(restS);//e
        phrHHC.addNote(note);//&
        phrHHC.addNote(note);//a

        //phrHHC.addNote(restW);
    }
    private void doScore() {
        // add phrases to the instrument (part)
        System.out.println("Assembling. . .");
        drums.addPhrase(phrBD);
        drums.addPhrase(phrSD);
        drums.addPhrase(phrHHC);

        // add the drum parts to a score.
        pattern1.addPart(drums);
    }
}