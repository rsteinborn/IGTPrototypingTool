package userinterface;

import java.util.List;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import java.util.List;



@SuppressWarnings("unused")	
public class Coordinatesystem {

	
	/*public Coordinatesystem () {
		init();
		}
		
	private void init(){
	  
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
      setDefaultCloseOperation(EXIT_ON_CLOSE); 
      this.setSize(new Dimension(400, 300));
      this.setTitle("x-y-z-Ebene"); }
	
	protected void processWindowEvenet(WindowEvent e){
		super.processWindowEvent(e);
		if(e.getID() == WindowEvent.WINDOW_CLOSING){
			System.exit(0);
		}
          }*/

@SupressWarnings({"unchecked", "rawtypes"})

public static void drawAchsen(String choice, List<String>I,
		XYChart.Series s, NumberAxis xAxis, NumberAxis yAxis) ){
			int x, y, z;
			
			
xAxis.setLabel(arg0);
yAxis.setLabel("Y-Achse");
		for (int i=0; i<l.size(); i=i+3) {
			x = Integer.parseInt(l.get(i));
			//System.out.println(x);
		
			for(int j=1; j<l.size(); j=j+3) {
				y = Integer.parseInt(l.get(j));
				System.out.println(y);
				s.getData().add(new XYChart.Data(x, y));
			}
		}

		xAxis.setLabel("X-Achse");                
        yAxis.setLabel("Z-Achse");
		for (int i=0; i<l.size(); i=i+3) {
			x = Integer.parseInt(l.get(i));
			System.out.println(x);
		
			for(int j=2; j<l.size(); j=j+3) {
				z = Integer.parseInt(l.get(j));
				s.getData().add(new XYChart.Data(x, z));
			}
		}

		xAxis.setLabel("Z-Achse");                
        yAxis.setLabel("Y-Achse");
		for (int i=1; i<l.size(); i=i+3) {
			y = Integer.parseInt(l.get(i));
			System.out.println(y);
		
			for(int j=2; j<l.size(); j=j+3) {
				z = Integer.parseInt(l.get(j));
				s.getData().add(new XYChart.Data(z, y));
			}
		}
		}

