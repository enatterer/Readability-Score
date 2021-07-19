package readability;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static int numberOfSentences = 0;
    private static int numberOfWords = 0;
    private static int numberOfCharacters = 0;
    private static int totalNumberOfSyllables = 0;
    private static int totalNumberOfPolySyllables = 0;


    public static void main(String[] args) throws FileNotFoundException {
        File f = new File(args[0]);
        Scanner myReader = new Scanner(f);
        while (myReader.hasNext()) {
            String s = myReader.next();
            totalNumberOfSyllables += getSyllables(s);
            if (getSyllables(s) >= 3) {
                totalNumberOfPolySyllables++;
            }
            if (s.matches("[a-zA-Z]+[.!?]+")) {
                numberOfSentences++;
            }
            numberOfWords++;
            numberOfCharacters += s.length();
            if (!myReader.hasNext() && s.matches("[a-zA-Z]+[^.!?]+")) {
                numberOfSentences++;
            }
        }
        myReader.close();
        System.out.println("Words: " + numberOfWords);
        System.out.println("Sentences: " + numberOfSentences);
        System.out.println("Characters: " + numberOfCharacters);
        System.out.println("Syllables: " + totalNumberOfSyllables);
        System.out.println("Polysyllables: " + totalNumberOfPolySyllables);
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): all");
        System.out.println(" ");
        Scanner scanner = new Scanner(System.in);
        String action = scanner.next();
        performAction(action);
        scanner.close();
    }

    private static void performAction(String action){
        if (action.matches("ARI|all")) {
            System.out.printf("Automated Readability Index: %.2f %s \n", double2Decimal(automatedReadibility()), age2Text(score2Age(automatedReadibility())));
        }
        if (action.matches("FK|all")){
            System.out.printf("Flesch–Kincaid readability tests: %.2f %s \n", double2Decimal(fleschKincaid()),  age2Text(score2Age(fleschKincaid())));
        }
        if (action.matches("SMOG|all") ){
            System.out.printf("Simple Measure of Gobbledygook: %.2f %s \n", double2Decimal(smog()), age2Text(score2Age(smog())));
        }
        if (action.matches("CL|all")){
            System.out.printf("Coleman–Liau index: %.2f %s \n", double2Decimal(colemanLiau()), age2Text(score2Age(colemanLiau())));
        }
        if (action.matches("all")){
            double average = (score2Age(automatedReadibility()) + score2Age(fleschKincaid()) + score2Age(smog()) + score2Age(colemanLiau()))/ 4;
            System.out.printf("This text should be understood in average by %.2f-year-olds.", average);
        }
    }

    private static int getSyllables(String s){
        String regex = "[aAeEiIoOuUyY]+";
        Matcher matcher = Pattern.compile(regex).matcher(s);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        if (s.toCharArray()[s.length() -1] == 'e'){
            count--;
        }
        return (count == 0) ? 1 : count;
    }

    private static BigDecimal double2Decimal(double d){
        BigDecimal rounded = new BigDecimal(Double.toString(d));
        return rounded.setScale(2, RoundingMode.DOWN);
    }

    private static double score2Age(double score){
        int[] ages = new int[]{5,6,7,9,10,11,12,13,14,15,16,17,18,24};
        int scoreRoundedUp = (int) Math.ceil(score);
        return (scoreRoundedUp >= 14) ? 24 : ages[scoreRoundedUp];
    }

    private static String age2Text(double age){
        return (age == 24) ? " (about 24+-year-olds)." : " (about " + age + "-year-olds).";
    }

    private static double automatedReadibility(){
        return (4.71 * ((double) numberOfCharacters/numberOfWords)) + 0.5 * (((double) numberOfWords/ numberOfSentences)) - 21.43;
    }

    private static double fleschKincaid(){
        return 0.39 * (double) numberOfWords / numberOfSentences + 11.8 * (double) totalNumberOfSyllables/numberOfWords - 15.59;
    }

    private static double smog(){
        return 1.043 * Math.sqrt((double) totalNumberOfPolySyllables * (double) (30 / numberOfSentences)) + 3.1291;
    }

    private static double colemanLiau(){
        double l = (double) (numberOfCharacters / numberOfWords) * 100;
        double s = (double) (numberOfSentences  / numberOfWords) * 100;
        return 0.0588 * l - 0.296 * s - 15.8;
    }
}
