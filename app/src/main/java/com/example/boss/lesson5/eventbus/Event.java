package com.example.boss.lesson5.eventbus;

/**
 * Created by BOSS on 11.11.2016.
 */

public class Event {
    private EventMessage eventMessage;

    private int position = -1;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public EventMessage getEventMessage() {
        return eventMessage;
    }

    public Event setEventMessage(EventMessage eventMessage) {
        this.eventMessage = eventMessage;
        return this;
    }
}
