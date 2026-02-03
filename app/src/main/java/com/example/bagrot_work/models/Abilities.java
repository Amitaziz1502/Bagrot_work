package com.example.bagrot_work.models;

    public enum Abilities{
        fastRun("fast_run"),
        doubleJump("double_jump"),
        defaultOp("default");


        public String name;
        Abilities(String name) {
            this.name =  name;
        }
    }

