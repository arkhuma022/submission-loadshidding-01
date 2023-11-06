package wethinkcode.places.db.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.text.WordUtils;
import wethinkcode.places.model.Places;
import wethinkcode.places.model.Town;

/**
 * TODO: javadoc PlacesDb
 */
public class PlacesDb implements Places{
    private Collection<Town> towns;
    private Collection<String> provinces = new ArrayList<>();
    private List<Town> townsInProvince = new ArrayList<>();
    public PlacesDb(Collection<Town> towns){
        this.towns = towns;
    }

    @Override
    public Collection<String> provinces(){
        /*
         * get a list of provinces from csv file
         */
        for(Town town : towns){
            provinces.add(town.getProvince());
        
        }
        Set<String> uniqueSet = new HashSet<>(provinces);
        List<String> uniqueList = new ArrayList<>(uniqueSet);

        return uniqueList;
    }

    @Override
    public Collection<Town> townsIn( String aProvince ){
        /*
         * get a list of towns in a province from csv file
         */
        
        townsInProvince.clear();
        for (Town town : towns){
            if (WordUtils.capitalizeFully(town.getProvince()).equals(WordUtils.capitalizeFully(aProvince))){
                townsInProvince.add(town);
            }
        }
        return townsInProvince;
    }

    @Override
    public int size(){
        /*
         * get the size of the list of towns
         */
        return towns.size();
    }
    
    
}