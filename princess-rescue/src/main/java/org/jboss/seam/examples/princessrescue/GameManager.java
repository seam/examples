/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.examples.princessrescue;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named
public class GameManager implements Serializable {

    Instance<GameRoom> allRooms;

    @Inject
    GameMessage gameMessage;

    GameRoom startRoom;

    GameRoom currentRoom;

    String emptyRoomShootMessage;

    String startMessage;

    boolean gameOver = true;

    public String newGame() {
        for (GameRoom i : allRooms) {
            i.reset();
        }
        gameMessage.add(startMessage);
        runRoom(startRoom);
        gameOver = false;
        return "play";
    }

    public void runRoom(GameRoom room) {
        currentRoom = room;
        if (currentRoom.isMonsterKilled()) {
            gameMessage.add(room.getKilledRoomMessage());
        } else {
            gameMessage.add(room.getMessage());
        }
        if (room.getRoomType() == RoomType.GAMEOVER) {
            gameOver = true;
        } else {
            for (GameRoom g : getAdjacentRooms()) {
                if (!g.isMonsterKilled()) {
                    gameMessage.add(g.getAdjacentMessage());
                }
            }
        }
    }

    public void runShoot(GameRoom room) {
        if (room.getShootEffect() == ShootEffect.KILL) {
            room.setMonsterKilled(true);
        } else if (room.getShootEffect() == ShootEffect.ANNOY) {
            gameOver = true;
        }
        if (room.getShootMessage() != null) {
            gameMessage.add(room.getShootMessage());
        } else {
            gameMessage.add(emptyRoomShootMessage);
        }

    }

    private Collection<GameRoom> getAdjacentRooms() {
        Collection<GameRoom> ret = new LinkedList<GameRoom>();
        if (currentRoom.getNorth() != null) {
            ret.add(currentRoom.getNorth());
        }
        if (currentRoom.getSouth() != null) {
            ret.add(currentRoom.getSouth());
        }
        if (currentRoom.getEast() != null) {
            ret.add(currentRoom.getEast());
        }
        if (currentRoom.getWest() != null) {
            ret.add(currentRoom.getWest());
        }
        return ret;
    }

    public void shootNorth() {
        if (currentRoom.getNorth() != null) {
            runShoot(currentRoom.getNorth());
        } else {
            gameMessage.add("You cannot shoot that way");
        }
    }

    public void shootSouth() {
        if (currentRoom.getSouth() != null) {
            runShoot(currentRoom.getSouth());
        } else {
            gameMessage.add("You cannot shoot that way");
        }
    }

    public void shootEast() {
        if (currentRoom.getEast() != null) {
            runShoot(currentRoom.getEast());
        } else {
            gameMessage.add("You cannot shoot that way");
        }
    }

    public void shootWest() {
        if (currentRoom.getWest() != null) {
            runShoot(currentRoom.getWest());
        } else {
            gameMessage.add("You cannot shoot that way");
        }
    }

    public void moveNorth() {
        if (currentRoom.getNorth() != null) {
            runRoom(currentRoom.getNorth());
        } else {
            gameMessage.add("You cannot move that way");
        }
    }

    public void moveSouth() {
        if (currentRoom.getSouth() != null) {
            runRoom(currentRoom.getSouth());
        } else {
            gameMessage.add("You cannot move that way");
        }
    }

    public void moveEast() {
        if (currentRoom.getEast() != null) {
            runRoom(currentRoom.getEast());
        } else {
            gameMessage.add("You cannot move that way");
        }
    }

    public void moveWest() {
        if (currentRoom.getWest() != null) {
            runRoom(currentRoom.getWest());
        } else {
            gameMessage.add("You cannot move that way");
        }
    }

    public GameRoom getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(GameRoom currentRoom) {
        this.currentRoom = currentRoom;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
