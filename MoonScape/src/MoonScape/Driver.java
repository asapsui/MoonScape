package MoonScape;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.Random;

public class Driver implements KeyListener {

//    private double rotationSpeed = 2 * (Math.PI / 180);
//    private double moveSpeed = 2.0;
//    private int turnDirect = 0;
//    private int walkDirect = 0;
//    private Canvas player;

    private int nusei = 0;

    private int heading  = 0;


    private static final int WIDTH = 800, HEIGHT = 600;

    private static final int numLines = 10;

    private static final Random random = new Random(100);

    private VectorBabai pos = new VectorBabai(50,50);
    //private int mouseX = 0, mouseY = 0;

    private Canvas canvas;


    //Line2D represents a line segment, a list of lines
    private LinkedList<Line2D.Float> lines;


    private Driver() {
        lines = buildLines();

        //Swing Basic of building a frame
        JFrame frame = new JFrame("Java Raycasting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //Canvas
        frame.add(canvas = new Canvas());
        //canvas.addMouseMotionListener(this);
        canvas.addKeyListener(this);
        //frame.add(player);


        frame.setSize(WIDTH, HEIGHT);

        //frame.setResizable(false);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        new Thread(() -> {
            while (true) {
                render();
            }
        }).start();

    }

    private LinkedList<Line2D.Float> buildLines() {
        LinkedList<Line2D.Float> lines = new LinkedList<>();
        for (int i = 0; i < numLines; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);

            lines.add(new Line2D.Float(x1, y1, x2, y2));
        }
        return lines;
    }

    
    private void render() {
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(2);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());


        g.setColor(Color.RED);
        for (Line2D.Float line : lines) {
            g.drawLine((int) line.x1, (int) line.y1, (int) line.x2, (int) line.y2);
        }
        g.setColor(new Color(255, 255, 255, 80));
        LinkedList<Line2D.Float> rays = calcRays(lines, (int)pos.getX(),(int) pos.getY(), 1000, 3000);
        for (Line2D.Float ray : rays) {
            g.drawLine((int) ray.x1, (int) ray.y1, (int) ray.x2, (int) ray.y2);
        }

        g.setColor(Color.BLUE);

        VectorBabai v2 = VectorBabai.fromAngle(heading);
        v2.setMag(30);
        v2.add(pos);
        g.drawLine((int)pos.getX(),(int)pos.getY(),(int)v2.getX(),(int)v2.getY());


        g.setColor(Color.green);
        g.fillOval((int)pos.getX()-15, (int)pos.getY()-15, 30, 30);

        g.dispose();
        bs.show();

    }

    private LinkedList<Line2D.Float> calcRays(LinkedList<Line2D.Float> lines, int x, int y, int resolution, int maxDist) {
        //int offset = ((heading) + (heading/2));


        LinkedList<Line2D.Float> rays = new LinkedList<>();
        for (int i = 0; i < resolution ; i++) {
            double bb = (heading + i) % 60 + heading - 30;

           // double aa = ((Math.PI/180) * (double) i / resolution);
            double dir = (bb * (Math.PI/180)) ;

           //System.out.println(dir);
            float minDist = maxDist;
            for (Line2D.Float line : lines) {
                float dist = getRayCast(x, y, x + (float) Math.cos(dir) * maxDist, y + (float) Math.sin(dir) * maxDist, line.x1, line.y1, line.x2, line.y2);
                if (dist < minDist && dist > 0) {
                    minDist = dist;
                }

            }
            rays.add(new Line2D.Float(x, y, x + (float) Math.cos(dir) * minDist, y + (float) Math.sin(dir) * minDist));
        }
        return rays;
    }


    public static void main(String[] args) {
        new Driver();
    }

    //Pythagoras theorem
    public static float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public static float getRayCast(float p0_x, float p0_y, float p1_x, float p1_y, float p2_x, float p2_y, float p3_x, float p3_y) {
        float s1_x, s1_y, s2_x, s2_y;
        s1_x = p1_x - p0_x;
        s1_y = p1_y - p0_y;
        s2_x = p3_x - p2_x;
        s2_y = p3_y - p2_y;

        float s, t;
        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = (s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            float x = p0_x + (t * s1_x);
            float y = p0_y + (t * s1_y);

            return dist(p0_x, p0_y, x, y);
        }

        return -1; // No collision
    }


//    @Override
//    public void mouseDragged(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseMoved(MouseEvent e) {
//        mouseX = e.getX();
//        mouseY = e.getY();
//    }



    public void keyTyped(KeyEvent e) {

    }


    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyChar());
        switch(e.getKeyChar()){
            case'a': heading-=7;
            break;
            case'd': heading+=7;
        }
        if(e.getKeyChar()=='w'){
            VectorBabai angle = VectorBabai.fromAngle(heading);
            angle.setMag(10);
            pos.add(angle);
        }
        
        if(e.getKeyChar()=='s') {
        	 VectorBabai angle = VectorBabai.fromAngle(heading);
             angle.setMag(-10);
             pos.add(angle);
        }
        
    }

    // 
    public void keyReleased(KeyEvent e) {
    	
    }
}
