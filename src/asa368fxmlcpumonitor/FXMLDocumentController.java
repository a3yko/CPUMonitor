/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asa368fxmlcpumonitor;

import static java.lang.Math.round;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
/**
 *
 * @author a3yko
 */
public class FXMLDocumentController implements Initializable {
    private double anglePerTick = 8.0;
    private KeyFrame key;
    private Timeline timeline = new Timeline(Animation.INDEFINITE);
    private DecimalFormat df = new DecimalFormat("0.00");
    private Date date = new Date();  
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");  
    private int record = 0;

    
    
    
    @FXML
    Button startButton = new Button();
    
    @FXML
    private Button recordButton = new Button();
    
  
    @FXML
    private StackPane images = new StackPane();
    
    @FXML
    private ImageView hand = new ImageView();
    
    @FXML
    private Label cpu = new Label();
    
    @FXML
    private TextArea output;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCpu();
    }    
    
    
    public void setupCpu(){
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), event ->{
           start();      
        }));
    }
    
    
    @FXML
    public void handleStart(ActionEvent event){
        if(!isRunning()){
            start();
            recordButton.setText("Record");
            startButton.setText("Stop");
        }
        else if(isRunning())
        {
            stop();
        }
    }
    
    public void start(){
        timeline.play();
        update();
    }
    
    public void reset(){
        cpu.setText("0.00");
        timeline.stop();
        startButton.setText("Start");
        output.clear();
        hand.setRotate(-90);
    }
    
    public void stop(){
        timeline.stop();
        startButton.setText("Start");
        recordButton.setText("Reset"); 
    }
    
    
    public void handleRecord(ActionEvent event){
        if(isRunning()){
        String test = output.getText();
        output.clear();
        output.appendText("Record " + record + ": " + df.format(getCPUUsage()*100)+ "%" + " at " + formatter.format(date)+ "\n" + test);
        record++;
        }
        else
        {
            reset();
        }
    }
    
    public double getCPUUsage() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        double value = 0;
        
        for(Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            
            if (method.getName().startsWith("getSystemCpuLoad") && Modifier.isPublic(method.getModifiers())) {
                try {
                    value = (double) method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = 0;
                }
                return value;
            }
        }
        return value;
    }
    
    public void update(){
      if(isRunning()){
          double cp = getCPUUsage();
          this.cpu.setText(df.format((cp* 100)));
          double rotation = (round((cp*100) % 4));
          hand.setRotate(-90 + (rotation*5));
      }
}
    
    public boolean isRunning(){
               if(timeline != null){
            if(timeline.getStatus() == Animation.Status.RUNNING){
                return true;
            }
            else{
                return false;
            }
        }
        return false;
        }
    }
