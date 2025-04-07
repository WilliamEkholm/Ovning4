package se.su.ovning4;

import java.util.Objects;

public class Record implements Node{
    private final String name;
    private final String artist;

    public Record(String name, String artist){
        this.name = name;
        this.artist = artist;
    }

    @Override
    public String getName(){
        return name;
    }

    public String getArtist(){
        return artist;
    }

    @Override
    public boolean equals(Object other){
        if (other instanceof Record r){
            return name.equals(r.name) && artist.equals(r.artist);
        }
        else
            return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, artist);
    }


    @Override
    public String toString(){
        return name + " (" + artist +")";
    }
}
