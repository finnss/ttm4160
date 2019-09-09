package exercises.trafficlight;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TrafficLight extends JFrame {
	
	private Signal green = new Signal(Color.green);
	private Signal yellow = new Signal(Color.yellow);
	private Signal red = new Signal(Color.red);

	public TrafficLight(String title, boolean showYellow){
		super(title);
		getContentPane().setLayout(new GridLayout(1, 1));
		green.turnOn(false);
		yellow.turnOn(false);
		red.turnOn(false);

		JPanel p1;
		if(showYellow) {
			p1 = new JPanel(new GridLayout(3,1));
			p1.add(red);
			p1.add(yellow);
		} else {
			p1 = new JPanel(new GridLayout(2,1));
			p1.add(red);
		}
		p1.add(green);

		getContentPane().add(p1);
		pack();
	}
	
	public void showGreen() {
		red.turnOn(false);
		if(yellow!=null) yellow.turnOn(false);
		green.turnOn(true);
	}

	public void showRed() {
		red.turnOn(true);
		if(yellow!=null) yellow.turnOn(false);
		green.turnOn(false);
	}

	public void showRedYellow() {
		if(yellow==null) {
			throw new UnsupportedOperationException("Traffic light has no yellow light.");
		}
		red.turnOn(true);
		yellow.turnOn(true);
		green.turnOn(false);
	}

	public void showYellow() {
		if(yellow==null) {
			throw new UnsupportedOperationException("Traffic light has no yellow light.");
		}
		red.turnOn(false);
		yellow.turnOn(true);
		green.turnOn(false);
	}

	public void switchAllOff() {
		red.turnOn(false);
		if(yellow!=null) yellow.turnOn(false);
		green.turnOn(false);
	}
	
	
	private static class Signal extends JPanel{

		Color on;
		int radius = 40;
		int border = 10;
		boolean change;

		Signal(Color color){
			on = color;
			change = true;
		}

		public void turnOn(boolean a){
			change = a;
			repaint();        
		}

		public Dimension getPreferredSize(){
			int size = (radius+border)*2;
			return new Dimension( size, size );
		}

		public void paintComponent(Graphics g){
			g.setColor( Color.black );
			g.fillRect(0,0,getWidth(),getHeight());

			if (change){
				g.setColor( on );
			} else {
				g.setColor( on.darker().darker().darker() );
			}
			g.fillOval( border,border,2*radius,2*radius );
		}
	}
}     

