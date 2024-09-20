package com.homeapp.backend.models;

/**
 * The DTOJoke object. Used as a Data Transfer Object by the Logger system to record issues from the FE.
 */
public class DTOJoke {

    private String category;
    private String type;
    private String setup;
    private String delivery;
    private String joke;

    public DTOJoke() {
    }

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getSetup() {
        return setup;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "DTOJoke{" +
                "category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", setup='" + setup + '\'' +
                ", delivery='" + delivery + '\'' +
                ", joke='" + joke + '\'' +
                '}';
    }
}