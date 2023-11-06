package wethinkcode.places;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import org.junit.jupiter.api.*;
import wethinkcode.places.model.Places;
import wethinkcode.places.model.Town;

import  static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit-test suite for the CSV parser.
 */
public class PlacesCsvParserTest
{
    private LineNumberReader input;

    private PlacesCsvParser parser;

    private Places places;

    @BeforeEach
    public void setUp(){
        input = new LineNumberReader( new StringReader( PlacesTestData.CSV_DATA ));
        parser = new PlacesCsvParser();
    }

    @AfterEach
    public void tearDown(){
        places = null;
        parser = null;
        input = null;
    }

    @Test
    public void firstLineGetsSkipped() throws IOException {
        Places places= parser.parseDataLines( input );
        assertEquals(5, places.size());
    }

    @Test
    public void splitLineIntoValuesProducesCorrectNoOfValues(){
        final String testLine = "Brakpan,Non_Perennial,92797,-26.60444444,26.34,01-06-1992,,North West,66,,262,8,16,DC40,Matlosana,,,NW403,,";
        String[] splitLine = parser.splitLineIntoValues(testLine);
        assertThat(splitLine.length).isEqualTo( 20);
    }

    @Test
    public void urbanPlacesAreWanted(){
        final String testLine = "Brakpan,Urban Area,92799,-26.23527778,28.37,31-05-1995,,Gauteng,114,,280,3,16,EKU,Ekurhuleni Metro,,,EKU,,\n";
        String[] splitLine = parser.splitLineIntoValues(testLine);
        Town town = parser.createTown(splitLine);
        assertThat(town!=null).isTrue();
    }

    @Test
    public void townsAreWanted(){
        final String testLine = "Brakpan,Town,92802,-27.95111111,26.53333333,30-05-1975,,Free State,68,,155,2,16,DC18,Matjhabeng,,,FS184,,";
        String[] splitLine = parser.splitLineIntoValues(testLine);
        Town town = parser.createTown(splitLine);
        assertThat(town!=null).isTrue();
    }

    @Test
    public void otherFeaturesAreNotWanted(){
        final String testLine = "Amatikulu,Station,95756,-29.05111111,31.53138889,31-05-1989,,KwaZulu-Natal,79,,237,4,16,DC28,uMlalazi,,,KZ284,,";
        String[] splitLine = parser.splitLineIntoValues(testLine);
        Town town = parser.createTown(splitLine);
        assertThat(town==null).isTrue();
    }

    @Test
    public void parseBulkTestData() throws IOException{
        final Places db = parser.parseDataLines( input );
        assertEquals( 5, db.size() );
    }

    @Test
    public void openCSVTest() throws FileNotFoundException, IOException{
        File csvFile = new File("src/main/resources/places.csv");
        assertThat(csvFile.exists()).isTrue();
    }

    @Test
    public void fileNotFoundTest() throws FileNotFoundException, IOException{
        File csvFile = new File("src/main/resources/nonExistentFile.csv");
        assertThat(csvFile.exists()).isFalse();
    }
}