/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.examples.princessrescue;

import java.io.Serializable;

/**
 * represents a room in the game. All configuration is done via XML
 *
 * @author stuart
 */
public class GameRoom implements Serializable {

    public GameRoom() {

    }

    /**
     * message that is displayed when the player enters the room.
     */
    String message;
    /**
     * message that is displayed when the player is adjacent to the room
     */
    String adjacentMessage;
    /**
     * What happens if an arrow is fired into the room
     */
    ShootEffect shootEffect = ShootEffect.NOTHING;
    /**
     * Message that is displayed when the arrow hits something, even if it is
     * just annoyed
     */
    String shootMessage;
    /**
     * Message that is display when a player enters a room that has something
     * they just shot.
     */
    String killedRoomMessage;

    /**
     * if the monster in the room has been killed.
     */
    boolean monsterKilled = false;

    /**
     * what happens when the player enters the room. There is no difference
     * between dying and winning, only a different message is displayed
     */
    RoomType roomType;

    GameRoom north, south, east, west;

    public void reset() {
        monsterKilled = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdjacentMessage() {
        return adjacentMessage;
    }

    public void setAdjacentMessage(String adjacentMessage) {
        this.adjacentMessage = adjacentMessage;
    }

    public ShootEffect getShootEffect() {
        return shootEffect;
    }

    public void setShootEffect(ShootEffect shootEffect) {
        this.shootEffect = shootEffect;
    }

    public String getShootMessage() {
        return shootMessage;
    }

    public void setShootMessage(String shootMessage) {
        this.shootMessage = shootMessage;
    }

    public String getKilledRoomMessage() {
        return killedRoomMessage;
    }

    public void setKilledRoomMessage(String killedRoomMessage) {
        this.killedRoomMessage = killedRoomMessage;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public GameRoom getNorth() {
        return north;
    }

    public void setNorth(GameRoom north) {
        this.north = north;
    }

    public GameRoom getSouth() {
        return south;
    }

    public void setSouth(GameRoom south) {
        this.south = south;
    }

    public GameRoom getEast() {
        return east;
    }

    public void setEast(GameRoom east) {
        this.east = east;
    }

    public GameRoom getWest() {
        return west;
    }

    public void setWest(GameRoom west) {
        this.west = west;
    }

    public boolean isMonsterKilled() {
        return monsterKilled;
    }

    public void setMonsterKilled(boolean monsterKilled) {
        this.monsterKilled = monsterKilled;
    }

}
