package upvictoria.pm_may_ago_2025.iti_271415.pi1u1.notion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notion_app.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla de cuadernos
    public static final String TABLE_NOTEBOOKS = "notebooks";
    public static final String COLUMN_NOTEBOOK_ID = "id";
    public static final String COLUMN_NOTEBOOK_TITLE = "title";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // Tabla de páginas
    public static final String TABLE_PAGES = "pages";
    public static final String COLUMN_PAGE_ID = "id";
    public static final String COLUMN_PAGE_TITLE = "title";
    public static final String COLUMN_PAGE_CONTENT = "content";
    public static final String COLUMN_PAGE_NOTEBOOK_ID = "notebook_id";
    public static final String COLUMN_PAGE_ORDER = "page_order";

    // SQL para crear tabla de cuadernos
    private static final String CREATE_NOTEBOOKS_TABLE = "CREATE TABLE " + TABLE_NOTEBOOKS + " (" +
            COLUMN_NOTEBOOK_ID + " TEXT PRIMARY KEY, " +
            COLUMN_NOTEBOOK_TITLE + " TEXT NOT NULL, " +
            COLUMN_CREATED_AT + " INTEGER NOT NULL, " +
            COLUMN_UPDATED_AT + " INTEGER NOT NULL);";

    // SQL para crear tabla de páginas
    private static final String CREATE_PAGES_TABLE = "CREATE TABLE " + TABLE_PAGES + " (" +
            COLUMN_PAGE_ID + " TEXT PRIMARY KEY, " +
            COLUMN_PAGE_TITLE + " TEXT NOT NULL, " +
            COLUMN_PAGE_CONTENT + " TEXT, " +
            COLUMN_PAGE_NOTEBOOK_ID + " TEXT NOT NULL, " +
            COLUMN_PAGE_ORDER + " INTEGER DEFAULT 0, " +
            COLUMN_CREATED_AT + " INTEGER NOT NULL, " +
            COLUMN_UPDATED_AT + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + COLUMN_PAGE_NOTEBOOK_ID + ") REFERENCES " +
            TABLE_NOTEBOOKS + "(" + COLUMN_NOTEBOOK_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTEBOOKS_TABLE);
        db.execSQL(CREATE_PAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTEBOOKS);
        onCreate(db);
    }
}