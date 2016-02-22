package guardian.dns.com.guardian.data;

import android.provider.BaseColumns;

/**
 * Created by dinusha on 2/22/16.
 */
public class LocationDataContract {

    public static abstract class TrackEntry implements BaseColumns {
        public static final String TABLE_NAME = "track";
        public static final String COLUMN_NAME_TRACK_ID = "id";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LON = "lon";
        public static final String COLUMN_NAME_TIME = "time";

    }
}

