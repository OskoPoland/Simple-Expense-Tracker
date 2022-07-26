import Data_Controls.*;
import javax.swing.*;
import java.awt.*;


public class App {
    public static void main(String[] args) {
        //Data Controls Instantiation
        DataControls dc = new DataControls();

        //Layout
        GridLayout logLay = new GridLayout(0,2);
        //decide on layout for the expense part

        //Frames
        JFrame logFrame = new JFrame();
        logFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        logFrame.setSize(400,400);
        logFrame.setLayout(logLay);

        JFrame expFrame = new JFrame();
        expFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        expFrame.setSize(600,600);
        //expFrame.setLayout(???)

        //Labels

        //Buttons

        //Text Fields

        //Action Listeners


    }
}
