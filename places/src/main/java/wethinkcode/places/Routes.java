package wethinkcode.places;

import java.util.Collection;

import org.apache.commons.text.similarity.LevenshteinDistance;

import io.javalin.Javalin;
import wethinkcode.places.model.Places;
import wethinkcode.places.model.Town;

public class Routes {
    public static void configureRoutes(Javalin app, Places places) {
        
        app.get("/provinces", context -> {
            Collection<String> provinces = places.provinces();
            context.json(provinces);
        });

        app.get("/towns/{province-name}", context -> {
            String province = context.pathParam("province-name");
            Collection<Town> towns = places.townsIn(province);
            if (towns.isEmpty()) {

                LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

                for (String listItem : places.provinces()) {
                    int distance = levenshteinDistance.apply(province, listItem);
                    
                    if (distance <= 3   ) { 
                        String suggestion = "{\"Error\\\" : Did you mean " + listItem + "? }";
                        context.json(suggestion);
                        return;
                    }
                }
                context.json("{\"Error\" : No towns found for province " + province+"}");
                return;
            }
            context.json(towns);
        });
    }

}
