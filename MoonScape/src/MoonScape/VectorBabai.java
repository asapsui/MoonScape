package MoonScape;

public class VectorBabai {
    /*   :D    oPo */
    double x = 0;
    double y = 0;


    VectorBabai(double x,double y){
        this.x = x;
        this.y = y;

    }

    public static VectorBabai fromAngle(double angleInDegrees){
        double radians = angleInDegrees * (Math.PI/180);
        return new VectorBabai(Math.cos(radians),Math.sin(radians));
    }
    public void setMag(double magnitude){

        double direction = this.getDir(this.y,this.x);
        this.x = Math.cos(direction)*magnitude;
        this.y = Math.sin(direction)*magnitude;


    }

    public double getDir(double y, double x){
       double direct =  Math.atan2(this.y,this.x);
       return direct;
    }

    public void add(VectorBabai numzei){
        this.x+=numzei.x;
        this.y+=numzei.y;
    }

    public double getY() {
        return this.y;

    }

    public double getX() {
        return this.x;
    }

}