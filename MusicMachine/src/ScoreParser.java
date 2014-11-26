/**
 * Created by pedroquinones on 11/25/14.
 */

import jm.JMC;
import jm.music.data.*;
import jm.util.*;

import java.util.*;

import jm.music.tools.*;

public class ScoreParser {
    private Score score = new Score();

    public ScoreParser(String midiFile){
        Read.midi(score, midiFile); //read score from file
    }

    private ArrayList<Part> getDrumParts(){
        Part[] parts = score.getPartArray();
        ArrayList<Part> drumParts = new ArrayList<Part>();

        for(Part part: parts){
            //9 is drum channel for General Midi
            if(part.getChannel() == 9){
                drumParts.add(part);
            }
        }
        return drumParts;
    }

    public HashMap<String, ArrayList<ArrayList<Double>>> getDrumBeatTimes(){
        ArrayList<Part> unSplitParts = getDrumParts();
        ArrayList<Double> hhTimes = new ArrayList<Double>();
        ArrayList<Double> sdTimes = new ArrayList<Double> ();
        ArrayList<Double> bdTimes = new ArrayList<Double> ();
        HashMap<String, ArrayList<ArrayList<Double>>> beatTimes = new HashMap<String, ArrayList<ArrayList<Double>>>();

        for(Part unsplitPart: unSplitParts){
            for(Phrase unsplitPhrase: unsplitPart.getPhraseArray()){
                for(int i = 0; i < unsplitPhrase.getSize(); i++){
                    if(unsplitPhrase.getNote(i).getPitch() == JMC.CLOSED_HI_HAT){
                        hhTimes.add(unsplitPhrase.getNoteStartTime(i));
                    }
                    else if(unsplitPhrase.getNote(i).getPitch() == JMC.ELECTRIC_SNARE){
                        sdTimes.add(unsplitPhrase.getNoteStartTime(i));
                    }
                    else if(unsplitPhrase.getNote(i).getPitch() == JMC.BASS_DRUM_1){
                        bdTimes.add(unsplitPhrase.getNoteStartTime(i));
                    }

                }
            }

            Collections.sort(hhTimes);
            Collections.sort(sdTimes);
            Collections.sort(bdTimes);

            ArrayList<ArrayList<Double>> hhMesures = splitMeasures(hhTimes,4);
            ArrayList<ArrayList<Double>> sdMesures = splitMeasures(sdTimes,4);
            ArrayList<ArrayList<Double>> bdMesures = splitMeasures(bdTimes,4);

            beatTimes.put("hh", hhMesures);
            beatTimes.put("sd", sdMesures);
            beatTimes.put("bd", bdMesures);
        }

        return beatTimes;
    }

    public ArrayList<ArrayList<Double>> splitMeasures(ArrayList<Double> beatTimes, int measureLength){
        int currMeasureEnd = measureLength;
        ArrayList<ArrayList<Double>> measures = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> measure = new ArrayList<Double>();

        for(Double beat: beatTimes){
            if(beat >= currMeasureEnd){
                //append measure to measures
                measures.add(new ArrayList<Double>(measure));
                //increment currMeasureEnd
                currMeasureEnd = currMeasureEnd + measureLength;
                //clear measure
                measure.clear();

            }
            measure.add(beat);
        }
        //add last measure
        measures.add(new ArrayList<Double>(measure));
        return measures;
    }

    public void playScore(){
        Play.midi(score);
    }

    public static void main(String[] args){
        //ScoreParser scoreParser = new ScoreParser("MusicMachine/drumLib/hi_hat/hihat2.mid");

        ScoreParser scoreParser = new ScoreParser("DrumKit.mid");

        //scoreParser.playScore();

        HashMap<String, ArrayList<ArrayList<Double>>> drumBeatTimes = scoreParser.getDrumBeatTimes();

        System.out.println(drumBeatTimes);


    }
}
