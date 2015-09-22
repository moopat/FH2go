package at.fhj.app.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleDatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 6;
	public static final String DATABASE_NAME = "fh2go.db";
    public static final String SCHEDULE_TABLE_NAME = "schedule";
    public static final String EVENT_TYPE_TABLE_NAME = "event_type";
    
    public static final String EVENT_TYPE_KEY_TYPE = "type";
    public static final String EVENT_TYPE_KEY_COLOR = "color";
    public static final String EVENT_TYPE_KEY_ACTIVE = "active";
    
    public static final String SCHEDULE_KEY_ID = "id";
    public static final String SCHEDULE_KEY_START = "start";
    public static final String SCHEDULE_KEY_END = "end";
    public static final String SCHEDULE_KEY_SUBJECT = "subject";
    public static final String SCHEDULE_KEY_LECTURER = "lecturer";
    public static final String SCHEDULE_KEY_LOCATION = "location";
    public static final String SCHEDULE_KEY_TYPE = "type";
    public static final String SCHEDULE_KEY_COURSE = "course";
    public static final String SCHEDULE_KEY_YEAR = "year";
    public static final String SCHEDULE_KEY_DATE = "key_date";
    public static final String SCHEDULE_KEY_UPDATE = "updated";
    
    Context context;
    
    private static final String SCHEDULE_TABLE_CREATE =
                "CREATE TABLE " + SCHEDULE_TABLE_NAME + " (" +
                SCHEDULE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SCHEDULE_KEY_START + " INTEGER, " +
                SCHEDULE_KEY_END + " INTEGER, " +
                SCHEDULE_KEY_SUBJECT + " TEXT, " +
                SCHEDULE_KEY_LECTURER + " TEXT, " +
                SCHEDULE_KEY_LOCATION + " TEXT, " +
                SCHEDULE_KEY_TYPE + " TEXT, " +
                SCHEDULE_KEY_COURSE + " TEXT, " +
                SCHEDULE_KEY_DATE + " TEXT, " +
                SCHEDULE_KEY_UPDATE + " INTEGER, " +
                SCHEDULE_KEY_YEAR + " TEXT);";
    
    public ScheduleDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCHEDULE_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + SCHEDULE_TABLE_NAME);
		onCreate(db);	
	}
}