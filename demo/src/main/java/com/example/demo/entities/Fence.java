package com.example.demo.entities;

import lombok.Getter;

import java.util.Random;

@Getter
public class Fence {
    private int startX, startY, endX, endY;
    private int a = 2;

    public Fence(int startX, int startY, int prevX, int prevY) {
        this.startX = startX;
        this.startY = startY;

        int dx = 24;
        int dy = 24;

        if (prevX - startX > 0) {
            dx *= -1;
        }
        if (prevY - startY > 0) {
            dy *= -1;
        }

        //set default values for end point.
        endY = startY + dy;
        endX = startX + dx;

        //get random direction (turn clockwise, continue straight ahead, turn counter-clockwise
        Random r = new Random();
        int direction = (r.nextInt(0, 2) - 1);

        try {
            a = (prevX - startX) / (prevY - startY); //calculate slope (a) of prev segment.

            calculateEndPoint(dx, direction);

        } catch (ArithmeticException e) {//divide by 0. If line is horizontal.
            endY = startY + dy * direction; //only y can change.
        }
    }

    public void calculateEndPoint(int dx, int direction){

        switch(a){
            case -1:
                if (direction == -1){ //counter-clockwise --> horizontal.
                    endY = startY;
                }else if (direction == 1){ //clockwise --> vertical.
                    endX = startX;
                }
                break;
            case 0: //vertical
                endX = startX + dx * direction; //only x can change.
                break;
            case 1:
                if (direction == -1){ //counter-clockwise --> vertical
                    endX = startX;
                }else if (direction == 1){ //clockwise --> horizontal.
                    endY = startY;
                }
                break;
            default:
                break;
        }//eo switch.
    }

}
