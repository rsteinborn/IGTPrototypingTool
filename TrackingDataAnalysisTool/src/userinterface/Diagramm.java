package userinterface;

import java.util.List;

import algorithm.Measurement;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.*;
import algorithm.ToolMeasure;

public class Diagramm extends Application{

		
		Button add, start;
		//@SupressWarnings("unchecked")
		@Override public void start(Stage stage) throws InterruptedException {
			
			String x = "X-Achse";
			String y = "Y-Achse";
			String z = "Z-Achse";
			
			stage.setTitle("x-y-z-Ebene");
		
		final NumberAxis xAxis = new NumberAxis(0, 30, 1);
		final NumberAxis yAxis = new NumberAxis(0, 30, 1);
		
		final NumberAxis xAxis1 = new NumberAxis(0, 30, 1);
		final NumberAxis yAxis1 = new NumberAxis(0, 30, 1);
		
		final NumberAxis xAxis2 = new NumberAxis(0, 30, 1);
		final NumberAxis yAxis2 = new NumberAxis(0, 30, 1);
		
		
		xAxis.setLabel(x);
		yAxis.setLabel(y);
		
		xAxis1.setLabel(x);
		yAxis1.setLabel(z);
		
		xAxis2.setLabel(z);
		yAxis2.setLabel(y);
		
		final ScatterChart <Number, Number> s1 = 
				new ScatterChart<Number, Number>(xAxis, yAxis);
		final ScatterChart <Number, Number> s2 = 
				new ScatterChart<Number, Number>(xAxis1, yAxis1);
		final ScatterChart <Number, Number> s3 = 
				new ScatterChart<Number, Number>(xAxis2, yAxis2);
		
		s1.setTitle("XY-Ebene");
		s2.setTitle("XZ-Ebene");
		s3.setTitle("YZ-Ebene");
		
	
XYChart.Series series1 = new XYChart.Series();
XYChart.Series series2 = new XYChart.Series();
XYChart.Series series3 = new XYChart.Series();


s1.setPrefSize(400, 400);
s1.getData().addAll(series1);

s2.setPrefSize(400, 400);
s2.getData().addAll(series2);

s3.setPrefSize(400, 400);
s3.getData().addAll(series3);

Scene scene = new Scene(new Group());
final VBox vbox = new VBox();
final HBox hbox = new HBox();

//RadioButton radioB1 = new RadioButton("xyz");
start = new Button("Start");

	start.setOnAction((event)->{
	series1.getData().clear();
	
		String choice = "xyz";
		
//		if (radioB1.isSelected()) {
//    		choice = "xyz";
//            series1.setName("XY-Diagramm");
//            series2.setName("XZ-Diagramm");
//		}

		
		//List <String> line = null;
		// List<Measurement> line = algorithm.ToolMeasure.getMeasurement();
		
		ToolMeasure l = new ToolMeasure();
		l.getMeasurement();

		Coordinatesystem.drawAchsen("xy", (List<Measurement>) l, series1, xAxis, yAxis);
		Coordinatesystem.drawAchsen("xz", (List<Measurement>) l, series2, xAxis, yAxis);
		Coordinatesystem.drawAchsen("zy", (List<Measurement>) l, series3, xAxis, yAxis);

 	});



hbox.setSpacing(10);
hbox.getChildren().addAll(s1, s2, s3);

vbox.getChildren().addAll(start, hbox);
hbox.setPadding(new Insets(50, 10, 50, 20));

((Group)scene.getRoot()).getChildren().add(vbox);
stage.setScene(scene);
stage.show();
}



public static void main(String[]args){

launch(args);}}



