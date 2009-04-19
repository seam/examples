package org.jboss.seam.example.pdf;

import org.jboss.seam.annotations.*;
import java.util.*;

@Name("data")
public class ChartData {


    public List<Stuff> getPieData() {
        List<Stuff> allStuff = new ArrayList<Stuff>();

        allStuff.add(new Stuff("first thing",  10f));
        allStuff.add(new Stuff("second thing", 49f));
        allStuff.add(new Stuff("third thing",  25f));
        allStuff.add(new Stuff("fourth thing",  25f));

        return allStuff;        
    }

    public static class Stuff {
        String name;
        Float  value;

        public Stuff(String name, Float value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Float getValue() {
            return value;
        }
    }
}
