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
    
    /**
     * The parseDataLines function reads the data from a file and creates a list of towns.
     *
     *
     * @param final LineNumberReader in Read the lines from a file
     *
     * @return A places object
     *
     */
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

    /**
     * The splitLineIntoValues function takes a String line and splits it into an array of Strings.
     * The function uses the split method to split the line by commas, but also accounts for empty values.
     * If a value is empty, then it will be replaced with null in the array of Strings that is returned.

     *
     * @param String line Split the line into values
     *
     * @return An array of strings
     */
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

    /**
     * The createTown function takes in a String array of lines and an index.
     * It then extracts the name, feature description, and province from the lines using helper functions.
     * If all three are not null, it checks if the feature description is either &quot;Town&quot; or &quot;Urban Area&quot;.
     * If so, it creates a new Town object with those parameters and adds it to townList.

     *
     * @param String[] lines Extract the name, feature description and province from the line
     *
     * @return A town object
     *
     */
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
    /**
     * The extractProvince function takes in a String array of values and an integer indexProvince.
     * It then checks if the indexProvince is less than the length of values, and if so, it sets value to be equal to
     * the value at that index in values. Then it adds this value to provinceList and returns it. If not, then null is returned.

     *
     * @param String[] values Store the values of each line in the csv file
     * @param int indexProvince Specify the index of the province in the values array
     *
     * @return The province at the given index
     *
     */
    public String extractProvince(String[] values, int indexProvince) {
        if (indexProvince < values.length) {
            String value = values[indexProvince];
            provinceList.add(value);
            return value;
        }
        return null;
    }
    
    /**
     * The extractName function takes in a String array and an integer index.
     * It returns the value at that index if it exists, or null otherwise.

     *
     * @param String[] values Store the values of a line in the csv file
     * @param int indexName Specify the index of the array that will be used to extract a name
     *
     * @return The value of the indexname element in values
     *
     */
    public static String extractName(String[] values, int indexName) {
        if (indexName < values.length) {
            String value = values[indexName];
            return value;
        }
        return null;
    }
    
    /**
     * The extractFeatureDescription function takes in an array of strings and an integer index.
     * It returns the string at that index, or null if there is no such element.

     *
     * @param String[] values Pass the values of a row in the csv file
     * @param int indexFeatureDescription Specify the index of the feature description in the array
     *
     * @return The value of the feature description at the specified index
     *
     */
    public static String extractFeatureDescription(String[] values, int indexFeatureDescription) {
        if (indexFeatureDescription < values.length) {
            String value = values[indexFeatureDescription];
            return value;
        }
        return null;
    }

    /**
     * The getCsvFile function takes a String as an argument and returns a File object.
     * The function checks to see if the file exists, and if it does not exist, throws
     * an exception. If the file does exist, then it is returned as a File object.

     *
     * @param String csvFileName Specify the name of the file that is to be read
     *
     * @return A file object
     *
     */
    public File getCsvFile( String csvFileName ) throws FileNotFoundException {
        File csvFile = new File( csvFileName );
        if (csvFile.exists()) {
            return csvFile;
        } else {
            throw new FileNotFoundException( "File " + csvFileName + " does not exist." );
        }
    }

    }
