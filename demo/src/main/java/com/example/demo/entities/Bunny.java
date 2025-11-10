package com.example.demo.entities;

import lombok.Getter;

import java.util.Random;

@Getter
public class Bunny extends Animal {
    static final int SPEED = 5;

    public Bunny(int x, int y){
        super(x, y);

    }


}
