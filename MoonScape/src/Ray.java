public class Ray {

    double rayAngle;
    double wallHitX, wallHitY, distance;
    int hitWallColor;
    boolean wasHitVertical, isRayFacingDown, isRayFacingUp,
            isRayFacingRight, isRayFacingLeft;



    public Ray(double rayAngle){
        this.rayAngle = normalizeAngle(rayAngle);
        this.wallHitX = 0;
        this.wallHitY = 0;
        this.distance = 0;
        this.hitWallColor = 0;
        this.wasHitVertical = false;



        this.isRayFacingDown = this.rayAngle > 0 && this.rayAngle < Math.PI;
        this.isRayFacingUp = !this.isRayFacingDown;

        this.isRayFacingRight = this.rayAngle < 0.5 * Math.PI || this.rayAngle > 1.5 * Math.PI;
        this.isRayFacingLeft = !this.isRayFacingRight;
    }

    public static double normalizeAngle(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) {
            angle = (2 * Math.PI) + angle;
        }
        return angle;
    }

    public void cast(Player player){
        double xintercept, yintercept;
        double ystep, xstep;


        /////////////////////////////////////
        // HORIZONTAL GRID CHECK (DDA)    ///
        /////////////////////////////////////
        boolean foundHorzWallHit = false;
        double horzWallHitX = 0;
        double horzWallHitY = 0;
        int horzWallColor = 0;

        //Finding y-coordinate of the closest horizontal grid intersection
        yintercept = Math.floor(player.y/Game.TILE_SIZE) * Game.TILE_SIZE;
        yintercept += this.isRayFacingDown ? Game.TILE_SIZE : 0;

        //Finding x-coordinate
        xintercept = player.x + (yintercept - player.y)/Math.tan(this.rayAngle);

        //Calculating Delta X and Delta Y for DDA checking
        ystep = Game.TILE_SIZE;
        ystep*= this.isRayFacingUp ? -1 : 1;

        xstep = Game.TILE_SIZE/Math.tan(this.rayAngle);
        xstep *= (this.isRayFacingLeft && xstep > 0) ? -1 : 1;
        xstep *= (this.isRayFacingRight && xstep < 0) ? -1 : 1;

        double nextHorzTouchX = xintercept;
        double nextHorzTouchY = yintercept;

        while(nextHorzTouchX >= 0 && nextHorzTouchX <= Game.WINDOW_WIDTH &&
                nextHorzTouchY >=0 && nextHorzTouchY <= Game.WINDOW_HEIGHT){

            int wallGridContent = Game.mapIndexAt(nextHorzTouchX, (nextHorzTouchY - (this.isRayFacingUp ? 1 : 0)));

            if(Game.hasWallAt(nextHorzTouchX, (nextHorzTouchY - (this.isRayFacingUp ? 1 : 0)))){

                foundHorzWallHit = true;
                horzWallHitX = nextHorzTouchX;
                horzWallHitY = nextHorzTouchY;

                if(wallGridContent != 0){
                    horzWallColor = wallGridContent;
                }
                break;
            }
            else{
                nextHorzTouchX += xstep;
                nextHorzTouchY += ystep;
            }
        }
        /////////////////////////////////////
        // VERTICAL GRID INTERSECTION DDA  //
        /////////////////////////////////////

        boolean foundVertWallHit = false;
        double vertWallHitX = 0;
        double vertWallHitY = 0;
        int vertWallColor = 0;

        //Finding x-coordinate of the closest horizontal grid intersection
        xintercept = Math.floor(player.x/Game.TILE_SIZE) * Game.TILE_SIZE;
        xintercept += this.isRayFacingRight ? Game.TILE_SIZE : 0;

        //Finding y-coordinate
        yintercept = player.y + (xintercept - player.x) * Math.tan(this.rayAngle);

        //Calculating xstep and ystep
        xstep = Game.TILE_SIZE;
        xstep *= this.isRayFacingLeft ? -1 : 1;

        ystep = Game.TILE_SIZE * Math.tan(this.rayAngle);
        ystep *= (this.isRayFacingUp && ystep > 0) ? -1 : 1;
        ystep *= (this.isRayFacingDown && ystep < 0) ? -1 : 1;

        double nextVertTouchX = xintercept;
        double nextVertTouchY = yintercept;

        while(nextVertTouchX >= 0 && nextVertTouchX <= Game.WINDOW_WIDTH &&
                nextVertTouchY >= 0 && nextVertTouchY <= Game.WINDOW_HEIGHT){

            int wallGridContent = Game.mapIndexAt((nextVertTouchX - (this.isRayFacingLeft ? 1 : 0)),
                    nextVertTouchY);

            if (Game.hasWallAt((nextVertTouchX - (this.isRayFacingLeft ? 1 : 0)),
                    nextVertTouchY)){

                foundVertWallHit = true;
                vertWallHitX = nextVertTouchX;
                vertWallHitY = nextVertTouchY;

                if(wallGridContent != 0){
                    vertWallColor = wallGridContent;
                }

                break;
            }else{
                nextVertTouchX += xstep;
                nextVertTouchY += ystep;
            }
        }
        // Calculating the size of both horizontal and vertical interception
        double horzHitDistance = (foundHorzWallHit) ? distanceBetweenPoints(player.x,
                player.y, horzWallHitX, horzWallHitY) : 34535;
        double vertHitDistance = (foundVertWallHit)
                ? distanceBetweenPoints(player.x, player.y,
                vertWallHitX, vertWallHitY) : 9045834;

        //store the smalles values
        this.wallHitX = (horzHitDistance < vertHitDistance) ? horzWallHitX : vertWallHitX;
        this.wallHitY = (horzHitDistance < vertHitDistance) ? horzWallHitY : vertWallHitY;
        this.distance = (horzHitDistance < vertHitDistance) ? horzHitDistance : vertHitDistance;
        this.hitWallColor = (horzHitDistance < vertHitDistance) ? horzWallColor : vertWallColor;
        this.wasHitVertical = (vertHitDistance < horzHitDistance);

        //this.wallIndex = Game.mapIndexAt(wallHitX,wallHitY);

    }

    public static double distanceBetweenPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
}