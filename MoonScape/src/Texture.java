import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class Texture {
	
    public int[] pixels;
    public String location;
    public final int SIZE;
    protected BufferedImage image;
    
    public Texture(String location, int size){
        this.location = location;
        this.SIZE = size;
        this.pixels = new int[SIZE * SIZE];
        load();

    }
    private void load(){
        try{
            image = ImageIO.read(new File(location));
            int w = image.getWidth();
            int h = image.getHeight();
            image.getRGB(0,0,w,h,pixels,0,w);
//            for(int i = 0; i < pixels.length; i++){
//                System.out.println(pixels[i]);
//            }
        }catch(IOException e){
            System.out.println("sad days");
        }
    }
    
    // only the mob class is using this method and the size for all of them is 64
    public void changeTexture(Texture t) {
    	this.location = t.location;
    	// reload the updated image
    	load();  
    }
    
    public Image getImage() {
    	return image;
    }
       
    public static Texture blueBall = new Texture("res/blueBall.bmp", 64);
    public static Texture greyPanel2 = new Texture("res/greyPanel.bmp", 64);

    public static Texture spaceShip = new Texture("res/spaceShip.bmp", 64);
    public static Texture spaceShip1 = new Texture("res/spaceShip1.bmp", 64);
    public static Texture spaceShip2 = new Texture ("res/spaceShip2.bmp",64);


    public static Texture bigSpaceShip = new Texture ("res/bigSpaceShip.bmp",64);
    public static Texture bigSpaceShip1 = new Texture ("res/2132.bmp",64);
    public static Texture bigSpaceShip2 = new Texture ("res/2133.bmp",64);
    public static Texture bigSpaceShip3 = new Texture ("res/2135.bmp",64);
    public static Texture bigSpaceShip4 = new Texture ("res/2136.bmp",64);

    public static Texture escape = new Texture ("res/escape.bmp",64);
    public static Texture escapeDoor = new Texture ("res/escapeDoor.bmp",64);


    public static Texture bluestone = new Texture("res/bluestone.png", 64);
    public static Texture moon = new Texture("res/moon.png", 64);
    public static Texture door = new Texture("res/door.png", 64);
    public static Texture doorWall = new Texture("res/doorWall.png", 64);

}
