/**
 * Created by pedroquinones on 11/29/14.
 */

import jm.JMC;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Play;
import jm.util.Read;
import jm.util.Write;

import java.io.File;

import java.util.*;



public class DrumLearner {
    private HashMap<String, ArrayList<Score>> knowledgeBase;
    private HashMap<String, ArrayList<ArrayList<Double>>> timeKnowledgeBase;
    private HashMap<String, ArrayList<ArrayList<Double>>> drumParts;


    public DrumLearner(){
        createKnowledgeBase();
        //TODO convert knowledgeBase Scores to ArrayList of beat times
    }

    private void createKnowledgeBase(){
        knowledgeBase = new HashMap<String, ArrayList<Score>>();
        timeKnowledgeBase = new HashMap<String, ArrayList<ArrayList<Double>>>();

        File dir = new File("drumLib");
        File InstSampleLib;
        File[] instrumentListing = dir.listFiles();
        File[] InstSampleListing;
        Score score = new Score();
        ScoreParser scoreParser;


        if (instrumentListing != null) {
            for (File instrument : instrumentListing) {
                knowledgeBase.put(instrument.getName(), new ArrayList<Score>());
                timeKnowledgeBase.put(instrument.getName(), new ArrayList<ArrayList<Double>>());
                ArrayList<Double> beatTimes;

                InstSampleLib = new File(instrument.getAbsolutePath());
                InstSampleListing = InstSampleLib.listFiles();
                if (InstSampleListing != null) {
                    for (File sample : InstSampleListing) {
                        Read.midi(score, sample.getAbsolutePath());
                        knowledgeBase.get(instrument.getName()).add(score.copy());

                        scoreParser = new ScoreParser(score);

                        //handle empty score
                        if(scoreParser.getDrumBeatTimes().isEmpty()){
                            beatTimes = new ArrayList<Double>();
                        }
                        else{
                            beatTimes = scoreParser.getDrumBeatTimes().get(instrument.getName()).get(0);
                        }
                        timeKnowledgeBase.get(instrument.getName()).add(beatTimes);

                        score.empty();
                    }
                } else {
                    System.out.println("Error reading drum samples");
                }
            }
        } else {
            System.out.println("Error: Could not find drumLib");
        }
        //System.out.println(timeKnowledgeBase);
    }

    public Score mimicScore(Score origScore){
        Score newScore = new Score();

        Score tempScore;

        HashMap<String, Part> parts = new HashMap<String, Part>();
        parts.put("hh", new Part("hh", 0,9));
        parts.put("sd", new Part("sd", 0,9));
        parts.put("bd", new Part("bd", 0,9));

        Phrase currentPhrase;

        //to handle empty measures
        Phrase emptyMeasure = new Phrase(new Note(JMC.REST, JMC.WHOLE_NOTE));

        ScoreParser scoreParser = new ScoreParser(origScore);

        //contains beat time information for all measures of all instruments
        HashMap<String, ArrayList<ArrayList<Double>>> allBeatTimes = scoreParser.getDrumBeatTimes();

        int mIndex;

        for(String instrument: allBeatTimes.keySet()){
            mIndex = 0;

            //for all the instruments
            for(ArrayList<Double> measure: allBeatTimes.get(instrument)){
                //find the index of the nearest neighbor in knowledge base
                int nearestNeighbor = nearestNeighbor(measure, instrument);
                //get score from knowledge base
                tempScore = knowledgeBase.get(instrument).get(nearestNeighbor);

                //if the nearest neighbor is not and empty measure
                if(tempScore.getSize() > 0){
                    currentPhrase = tempScore.getPart(0).getPhrase(0).copy();
                    //increment the start time of phrase to current measure
                    currentPhrase.setStartTime(currentPhrase.getStartTime()+(mIndex*4));
                    //add the phrase to the instrument part
                    parts.get(instrument).add(currentPhrase);
                }
                else{
                    //if the nearest neighbor is an empty measure
                    currentPhrase = emptyMeasure.copy();
                    //increment the start time of phrase to current measure
                    currentPhrase.setStartTime(currentPhrase.getStartTime()+(mIndex*4));
                    //add the phrase to the instrument part
                    parts.get(instrument).add(currentPhrase);
                }
                mIndex ++;
            }
            //add the part to the new score
            newScore.add(parts.get(instrument));
        }

        //match tempo
        newScore.setTempo(origScore.getTempo());

        return newScore;
    }

    private int nearestNeighbor(ArrayList<Double> measure, String instrument){
        int index = 0;
        double maxVal = 0;
        double value;
        int nnIndex = 0;

        for(ArrayList<Double> sample: timeKnowledgeBase.get(instrument)){
            value = compareMeasures(measure, sample);
            if(value > maxVal){
                maxVal = value;
                nnIndex = index;
            }
            index ++;
        }
        return nnIndex;
    }

    private double compareMeasures(ArrayList<Double> m1, ArrayList<Double> m2){
        double value = 0;

        ArrayList<Double> similarBeats = new ArrayList<Double>(m1);
        similarBeats.retainAll(m2);

        ArrayList<Double> temp1 = new ArrayList<Double>(m1);
        ArrayList<Double> temp2 = new ArrayList<Double>(m2);
        temp1.removeAll(similarBeats);
        temp2.removeAll(similarBeats);

        temp1.addAll(temp2); //now temp1 contains dissimilar beats

        //if there are no dissimilar beats the beats are the same
        if(temp1.isEmpty()){
            value = Double.POSITIVE_INFINITY;
        }
        else{
            value = ((double)similarBeats.size())/temp1.size();
        }

        return value;
    }

    public static void main(String[] args){
        DrumLearner learner = new DrumLearner();
        Score origScore = new Score();
        Read.midi(origScore, args[0]);
        System.out.println("Playing original score");
        Play.midi(origScore);
        System.out.println("Replicating drum beat from original score");
        Score newScore = learner.mimicScore(origScore);
        Play.midi(newScore);
        Write.midi(newScore, "newDrumKit.mid");
        System.out.println("Combining Parts");
        for(Part part: origScore.getPartArray()){
            newScore.add(part);
        }
        Play.midi(newScore);
        Write.midi(newScore, "combinedDrumKit.mid");
    }

}
