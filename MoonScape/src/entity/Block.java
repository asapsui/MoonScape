package entity;

import java.util.ArrayList;
import java.util.List;
import graphics.Texture;

// testing how to add objects
public class Block extends Sprite1 {
	
	// there is no change in velocity, so its zero
	public Block(double x, double y) {
		super(x,y);
		
		load();
	}
	
	private void load() {
		loadTexture(new Texture("res/103.bmp", 64));
	}

}
