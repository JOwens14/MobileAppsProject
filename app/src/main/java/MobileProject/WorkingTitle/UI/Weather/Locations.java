package MobileProject.WorkingTitle.UI.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Locations {

    public static final List<Location> LOCATIONS = new ArrayList<Location>();
    public static final Map<String, Location> LOCATIONS_MAP = new HashMap<String, Location>();

    private static String[] locos = {"TACOMA,WA,US", "SEATTLE,WA,US"};

    static {
        // Add some sample items.
        for (int i = 0; i < locos.length; i++) {
            addLocation(createLocation(locos[i]));
        }
    }

    public static void addLocation(Location loco) {
        LOCATIONS.add(loco);
        LOCATIONS_MAP.put(loco.location, loco);
    }

    public static Location createLocation(String location) {
        return new Location(location);
    }


    public static class Location {
        public final String location;


        public Location(String location) {
            this.location = location;
        }

        @Override
        public String toString() {
            return location.substring(0, location.length() - 3);
        }
    }
}
