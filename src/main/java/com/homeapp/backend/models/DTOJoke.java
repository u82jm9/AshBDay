package com.homeapp.backend.models;

/**
 * The DTOJoke object. Used as a Data Transfer Object by the Logger system to record issues from the FE.
 */
public class DTOJoke {

    private String setup;
    private String punchline;
    private String body;

    /**
     * Zero argument Constructor to Instantiate a new DTOJoke.
     */
    public DTOJoke() {
        this.setup = "";
        this.punchline = "";
        this.body = "";
    }


    public DTOJoke(String setup, String punchline, String body) {
        this.setup = setup;
        this.punchline = punchline;
        this.body = body;
    }


    public String getSetup() {
        return setup;
    }


    public void setSetup(String setup) {
        this.setup = setup;
    }


    public String getPunchline() {
        return punchline;
    }


    public void setPunchline(String punchline) {
        this.punchline = punchline;
    }


    public String getBody() {
        return body;
    }


    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "DTOJoke{" +
                "setup='" + setup + '\'' +
                ", punchline='" + punchline + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}