/**
 * Pedro Quinones
 */
import jm.JMC;
import jm.music.data.Note;
import jm.util.Play;

public class Rhythm {

    public static void main(String[] args) {

        Note note = new Note();
        note.setPitch(JMC.G4);
        note.setDynamic(JMC.MEZZO_FORTE);
        note.setDuration(JMC.QUARTER_NOTE);

        Play.midi(note);
    }
}
