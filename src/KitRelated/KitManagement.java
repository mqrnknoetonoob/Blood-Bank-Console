package KitRelated;

import java.io.*;
import Message.*;

public class KitManagement implements Serializable {
    private int numberOfBag;
    private int numberOfNeedle;
    private int numberOfPipe;
    private int numberOfTestingKit;
    public KitManagement()
    {
        loadKitData();
    }
    public KitManagement(int x, int y, int z, int w) {
        numberOfBag = x;
        numberOfNeedle = y;
        numberOfPipe = z;
        numberOfTestingKit = w;
    }

    public void loadKitData() {
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data\\KitManagement.csv"))) {
            String line = br.readLine();
            String[] parts = line.strip().split(",");
            int bag = Integer.parseInt(parts[0]);
            int needle = Integer.parseInt(parts[1]);
            int pipe = Integer.parseInt(parts[2]);
            int testKit = Integer.parseInt(parts[3]);

            this.setNumberOfBag(bag);
            this.setNumberOfNeedle(needle);
            this.setNumberOfPipe(pipe);
            this.setNumberOfTestingKit(testKit);
            System.out.println("Kit data loaded successfully.");
        } catch (Exception e) {
            System.out.println("Error loading kit data: " + e.getMessage());
        }
    }

    public void setNumberOfBag(int numberOfBag) {
        this.numberOfBag = numberOfBag;
    }
    public void setNumberOfNeedle(int numberOfNeedle) {
        this.numberOfNeedle = numberOfNeedle;
    }

    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data\\KitManagement.csv"))) {
            writer.write(numberOfBag + "," + numberOfNeedle + "," + numberOfPipe + "," + numberOfTestingKit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setNumberOfPipe(int numberOfPipe) {
        this.numberOfPipe = numberOfPipe;
    }
    public void setNumberOfTestingKit(int numberOfTestingKit) {
        this.numberOfTestingKit = numberOfTestingKit;
    }
    public int getNumberOfBag() {
        return numberOfBag;
    }
    public int getNumberOfNeedle() {
        return numberOfNeedle;
    }
    public int getNumberOfPipe() {
        return numberOfPipe;
    }
    public  int getNumberOfTestingKit(){return numberOfTestingKit;}

    public void showInfo()
    {
        System.out.println("Number of bag: " + numberOfBag + "\n" + "Number of Needle" + numberOfNeedle + "\n" + "Number of pipe" + numberOfPipe + "\n" + "Number of Kit" + numberOfTestingKit);
    }
    public KitMessage Alarm(KitManagement x)
    {
        KitMessage y = new KitMessage(x);
        return y;
    }
}
///KitManagement.csv file writing format

/// 12,25,43,61