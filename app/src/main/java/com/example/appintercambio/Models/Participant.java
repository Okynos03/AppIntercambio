package com.example.appintercambio.Models;

public class Participant
{
    private int id;
    private String name, email;
    private Participant match;

    public Participant() {
    }

    public Participant(int id, String name, String email, Participant match) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.match = match;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Participant getMatch() {
        return match;
    }

    public void setMatch(Participant match) {
        this.match = match;
    }
}
