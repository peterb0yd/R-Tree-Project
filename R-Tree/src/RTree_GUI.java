import processing.core.PApplet;

public class RTree_GUI extends PApplet {

	String words = "Click Somewhere!";

	public void setup() {
	  size(640, 360);
	  // Create the font
	  textFont(createFont("Georgia", 36));
	}

	public void draw() {
	  background(0); // Set background to black

	  textSize(36);
	  text(words, 50, 120, 540, 300);
	}

	public void mousePressed() {
	 if (mousePressed) {
	   words = "X = " + mouseX + "\n " + "Y = " + mouseY;
	  }
	}
}