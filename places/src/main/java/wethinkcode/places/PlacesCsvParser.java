package wethinkcode.places;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.FileReader;

import com.google.common.annotations.VisibleForTesting;

import wethinkcode.places.db.memory.PlacesDb;
import wethinkcode.places.model.Places;
import wethinkcode.places.model.Town;


/**
 * PlacesCsvParser : I parse a CSV file with each line containing the fields (in order):
 * <code>Name, Feature_Description, pklid, Latitude, Longitude, Date, MapInfo, Province,
 * fklFeatureSubTypeID, Previous_Name, fklMagisterialDistrictID, ProvinceID, fklLanguageID,
 * fklDisteral, Local Municipality, Sound, District Municipality, fklLocalMunic, Comments, Meaning</code>.
 * <p>
 * For the PlaceNameService we're only really interested in the <code>Name</code>,
 * <code>Feature_Description</code> and <code>Province</code> fields.
 * <code>Feature_Description</code> allows us to distinguish towns and urban areas from
 * (e.g.) rivers, mountains, etc. since our PlaceNameService is only concerned with occupied places.
 */
public class PlacesCsvParser{
    public List<String> provinceList = new ArrayList<>();
    public Collection<Town> townList = new ArrayList<>();
    public String[] listOfValidProvinces = {"Eastern Cape", "Free State", "Gauteng", "KwaZulu-Natal", "Limpopo", "Mpumalanga", "North West", "Northern Cape", "Western Cape"};
    List<String> listOfValidProvincesAsList = Arrays.asList(listOfValidProvinces);
    int indexName =0;
    int indexFeatureDescription =1;
    int indexProvince=7;


    public Places parseCsvSource( File csvFile ) throws FileNotFoundException, IOException {
        LineNumberReader csvData = new LineNumberReader( new FileReader( csvFile ));
        Places places = parseDataLines( csvData );
        return places;
    }
    
    @VisibleForTesting
    Places parseDataLines( final LineNumberReader in ) throws IOException{
        
        try {
            String line;
            
            while ((line = in.readLine()) != null) {
                if (in.getLineNumber() == 1) {
                    
                    int i = 0;
                    String[] values = line.split(",");
                    for (String value : values) {
                        
                        if(value.equals("Name")){
                            indexName = i;
                        }
                        else if(value.equals("Feature_Description")){
                            indexFeatureDescription = i;
                        }
                        else if(value.equals("Province")){
                            indexProvince = i;
                        }
                        i ++;
                    }
                }
                else{
                    String[] lines = splitLineIntoValues(line);
                    createTown(lines);
                    
                }                

            }
        Places places = new PlacesDb(townList);
        return places;

        } finally {            
            in.close();
        }
    }

    public String[] splitLineIntoValues(String line) {
        String[] values = line.split(",", -1); 

        for (int i = 0; i < values.length; i++) {
            if (values[i].isEmpty()) {
                values[i] = null;
            }
        }
        return values;
    }
    

    public  Collection<Town> getTownList() {
        return townList;
    }

    public Town createTown(String[] lines){

        String name = extractName(lines,indexName);
        String featureDescription = extractFeatureDescription(lines,indexFeatureDescription);
        String province = extractProvince(lines,indexProvince);

        if ((featureDescription != null && name != null && province != null && listOfValidProvincesAsList.contains(province) ) && (featureDescription.equals("Town") || featureDescription.equals("Urban Area"))) {
            if (featureDescription.equals("Town") || featureDescription.equals("Urban Area")) {
                Town town = new Town(name, province);
                if (town != null) {
                    townList.add(town);
                    return town;
                }
            }
        
        }
           
        return null;
    }
    public String extractProvince(String[] values, int indexProvince) {
        if (indexProvince < values.length) {
            String value = values[indexProvince];
            provinceList.add(value);
            return value;
        }
        return null;
    }
    
    public static String extractName(String[] values, int indexName) {
        if (indexName < values.length) {
            String value = values[indexName];
            return value;
        }
        return null;
    }
    
    public static String extractFeatureDescription(String[] values, int indexFeatureDescription) {
        if (indexFeatureDescription < values.length) {
            String value = values[indexFeatureDescription];
            return value;
        }
        return null;
    }

    public File getCsvFile( String csvFileName ) throws FileNotFoundException {
        File csvFile = new File( csvFileName );
        if (csvFile.exists()) {
            return csvFile;
        } else {
            throw new FileNotFoundException( "File " + csvFileName + " does not exist." );
        }
    }

    // public static void main(String[] args) throws FileNotFoundException, IOException {
    //     PlacesCsvParser parser = new PlacesCsvParser();
    //     File csvFile = parser.getCsvFile("places/src/main/resources/PlaceNamesZA2008.csv");

    //     // Now, create a LineNumberReader using the csvFile

    //     parser.parseCsvSource(csvFile);
    //     int count =0;
    //     for (Town town : townList) {
    //         System.out.println(town.getName() + " in the " + town.getProvince());
    //         count ++;
            
    //     }
    // }

    }
