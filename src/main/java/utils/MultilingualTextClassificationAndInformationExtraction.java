package utils;

import gate.Corpus;
import gate.Factory;
import gate.Gate;
import gate.util.GateException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import utils.classification.TextClassifier;
import utils.fileUtils.FileUtils;
import utils.wekaUtils.WekaUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static utils.classification.TextClassifier.analyze;

public class MultilingualTextClassificationAndInformationExtraction {

    private final static Logger log = Logger.getLogger(MultilingualTextClassificationAndInformationExtraction.class);

    public static void main(String[] args) {

        // Logging config
        BasicConfigurator.configure();

        log.info("Text Classification and Extraction App. " +
                "This App Recognizes texts belonging to the following two domain: terrorist attacks and aviation accidents");

        // Extracting text from text_files and merging to resultFile.txt
        Path resultFilePath = FileUtils.extractDataToFile();

        // Generating WEKA .arff file
        assert resultFilePath != null;

        Path arffFile = WekaUtils.generateArffFromFile(resultFilePath.toFile().getAbsoluteFile());

        // Getting instances from ARFF File
        //Instances dataTrain = WekaUtils.getInstancesFromFile(arffFile.toString());
        // Getting test instances in order to classify them
        //Instances dataTest = WekaUtils.getInstancesFromFile("/home/victor/IdeaProjects/AIW2019_P1_G9/src/main/resources/testing_classifier.txt");

        TextClassifier classifier = new TextClassifier();
        classifier.initClassifier();
        classifier.loadTrainingInstances(arffFile.toString());

        try {

            if(Gate.getGateHome() == null)
                Gate.setGateHome(new File("/home/victor/GATE_Developer_8.0"));
            if(Gate.getPluginsHome() == null)
                Gate.setPluginsHome(new File("/home/victor/GATE_Developer_8.0/plugins"));

            Gate.init();
            CallMyGATEApp myanalyser=new CallMyGATEApp();
            myanalyser.loadMyGappFirst("/home/victor/IdeaProjects/AIW2019_P1_G9/src/main/java/gapps/MyNLPApp.gapp");
            log.info("Loading the Multilingual Text Analyzer.....done!");

            CallMyGATEApp ieEnglish = new CallMyGATEApp();
            ieEnglish.loadMyGappEnglish("/home/victor/IdeaProjects/AIW2019_P1_G9/src/main/java/gapps/IE_AIRPLANE_EN.gapp");
            log.info("Loading IE System for aviation accidents (English).....done!");

            CallMyGATEApp ieSpanish = new CallMyGATEApp();
            ieSpanish.loadMyGappSpanish("/home/victor/IdeaProjects/AIW2019_P1_G9/src/main/java/gapps/IE_AIRPLANE_ES.gapp");
            log.info("Loading IE System for terrorist attacks (Spanish).....done!");


            analyze(classifier, myanalyser, ieEnglish, ieSpanish);

        } catch (GateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("END");
    }
}