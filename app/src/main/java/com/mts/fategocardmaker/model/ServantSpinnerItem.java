package com.mts.fategocardmaker.model;

public class ServantSpinnerItem {

    private String name;
    private int id;
    private ServantType type;
    
    public ServantSpinnerItem(String name, int id, ServantType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ServantType getType() {
        return type;
    }

    public void setType(ServantType type) {
        this.type = type;
    }
}
