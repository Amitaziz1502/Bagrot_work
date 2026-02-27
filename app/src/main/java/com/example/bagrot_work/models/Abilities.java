package com.example.bagrot_work.models;

import java.util.ArrayList;

public enum Abilities{
        fastRun("fast_run",20),
        doubleJump("double_jump",30),
        defaultOp("default",0);


        public String name;
        public int price;

        Abilities(String name, int price) {
            this.name =  name;
            this.price =  price;
        }

    }

