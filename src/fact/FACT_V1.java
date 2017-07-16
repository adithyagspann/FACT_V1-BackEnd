/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fact;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Ravindra
 */
public class FACT_V1 extends Application {

    Boolean flag = false;

    @Override
    public void start(Stage primaryStage) {
        Button btn1 = new Button();
        Button btn2 = new Button();
        btn1.setMaxSize(150, 50);
        btn2.setMaxSize(150, 50);
        btn1.setText("Start the Schedular");
        btn2.setText("Run in the Background");
        btn2.setDisable(true);

        //calling the scheduler                    
        FACTScheduler fs = new FACTScheduler();

        btn1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                try {
                    if (!btn1.getText().equalsIgnoreCase("stop the schedular")) {
                        System.out.println("Starting the Schedular");
                        btn1.setText("Starting the Schedular");
                        fs.run();
                        btn1.setText("Stop the Schedular");
                        btn2.setDisable(false);
                    } else {
                        System.out.println("Stopping the Schedular");
                        fs.stop();
                        btn1.setText("Start the Schedular");
                    }

                } catch (Exception ex) {
                    btn1.setText("Error in Starting");
                    Logger.getLogger(FACT_V1.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        btn2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Running the Scheduler in Background");
                try {
                    flag = true;
                    primaryStage.close();
                    //System.exit(0);

                } catch (Exception ex) {
                    btn1.setText("Error in Starting");
                    Logger.getLogger(FACT_V1.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        VBox root = new VBox();

        root.getChildren().add(btn1);
        root.getChildren().add(btn2);

        Scene scene = new Scene(root, 150, 55);

        primaryStage.setTitle("FACT_V1");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        if (!flag) {
            System.out.println("Exiting the Application");
            System.exit(0);
        }
    }

}
