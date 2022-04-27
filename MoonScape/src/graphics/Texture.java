import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {
    public int[] pixels;
    private String location;
    public final int SIZE;

    public Texture(String location, int size){
        this.location = location;
        this.SIZE = size;
        this.pixels = new int[SIZE * SIZE];
        load();

    }
    private void load(){
        try{
            BufferedImage image = ImageIO.read(new File(location));
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
    public static Texture wood = new Texture("res/wood.png", 64);
    public static Texture brick = new Texture("res/redbrick.png", 64);
    public static Texture bluestone = new Texture("res/bluestone.png", 64);
    public static Texture stone = new Texture("res/greystone.png", 64);
    public static Texture moon = new Texture("res/moon.png", 64);

}
