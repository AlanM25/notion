package upvictoria.pm_may_ago_2025.iti_271415.pi1u1.notion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRepository {
    private DatabaseHelper dbHelper;

    public DatabaseRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Operaciones para Notebooks
    public long addNotebook(Notebook notebook) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOTEBOOK_ID, notebook.getId());
        values.put(DatabaseHelper.COLUMN_NOTEBOOK_TITLE, notebook.getTitle());
        values.put(DatabaseHelper.COLUMN_CREATED_AT, notebook.getCreatedAt());
        values.put(DatabaseHelper.COLUMN_UPDATED_AT, notebook.getUpdatedAt());

        long result = db.insert(DatabaseHelper.TABLE_NOTEBOOKS, null, values);
        db.close();
        return result;
    }

    public List<Notebook> getAllNotebooks() {
        List<Notebook> notebooks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTEBOOKS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Notebook notebook = new Notebook();
                notebook.setId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTEBOOK_ID)));
                notebook.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTEBOOK_TITLE)));
                notebook.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)));
                notebook.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT)));

                notebooks.add(notebook);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notebooks;
    }

    public int updateNotebook(Notebook notebook) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOTEBOOK_TITLE, notebook.getTitle());
        values.put(DatabaseHelper.COLUMN_UPDATED_AT, System.currentTimeMillis());

        int result = db.update(DatabaseHelper.TABLE_NOTEBOOKS, values,
                DatabaseHelper.COLUMN_NOTEBOOK_ID + " = ?",
                new String[]{notebook.getId()});
        db.close();
        return result;
    }

    public int deleteNotebook(String notebookId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Primero eliminar todas las páginas del cuaderno
        db.delete(DatabaseHelper.TABLE_PAGES,
                DatabaseHelper.COLUMN_PAGE_NOTEBOOK_ID + " = ?",
                new String[]{notebookId});

        // Luego eliminar el cuaderno
        int result = db.delete(DatabaseHelper.TABLE_NOTEBOOKS,
                DatabaseHelper.COLUMN_NOTEBOOK_ID + " = ?",
                new String[]{notebookId});
        db.close();
        return result;
    }

    // Operaciones para Páginas
    public long addPage(Page page) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PAGE_ID, page.getId());
        values.put(DatabaseHelper.COLUMN_PAGE_TITLE, page.getTitle());
        values.put(DatabaseHelper.COLUMN_PAGE_CONTENT, page.getContent());
        values.put(DatabaseHelper.COLUMN_PAGE_NOTEBOOK_ID, page.getNotebookId());
        values.put(DatabaseHelper.COLUMN_CREATED_AT, page.getCreatedAt());
        values.put(DatabaseHelper.COLUMN_UPDATED_AT, page.getUpdatedAt());

        long result = db.insert(DatabaseHelper.TABLE_PAGES, null, values);
        db.close();
        return result;
    }

    public List<Page> getPagesByNotebook(String notebookId) {
        List<Page> pages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PAGES,
                null,
                DatabaseHelper.COLUMN_PAGE_NOTEBOOK_ID + " = ?",
                new String[]{notebookId},
                null, null,
                DatabaseHelper.COLUMN_PAGE_ORDER + " ASC, " +
                        DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Page page = new Page();
                page.setId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGE_ID)));
                page.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGE_TITLE)));
                page.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGE_CONTENT)));
                page.setNotebookId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGE_NOTEBOOK_ID)));
                page.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)));
                page.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT)));

                pages.add(page);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return pages;
    }

    public int updatePage(Page page) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PAGE_TITLE, page.getTitle());
        values.put(DatabaseHelper.COLUMN_PAGE_CONTENT, page.getContent());
        values.put(DatabaseHelper.COLUMN_UPDATED_AT, System.currentTimeMillis());

        int result = db.update(DatabaseHelper.TABLE_PAGES, values,
                DatabaseHelper.COLUMN_PAGE_ID + " = ?",
                new String[]{page.getId()});
        db.close();
        return result;
    }

    public int deletePage(String pageId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(DatabaseHelper.TABLE_PAGES,
                DatabaseHelper.COLUMN_PAGE_ID + " = ?",
                new String[]{pageId});
        db.close();
        return result;
    }

    public Page getPageById(String pageId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PAGES,
                null,
                DatabaseHelper.COLUMN_PAGE_ID + " = ?",
                new String[]{pageId},
                null, null, null);

        Page page = null;
        if (cursor.moveToFirst()) {
            page = new Page();
            page.setId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGE_ID)));
            page.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGE_TITLE)));
            page.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGE_CONTENT)));
            page.setNotebookId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAGE_NOTEBOOK_ID)));
            page.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)));
            page.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT)));
        }
        cursor.close();
        db.close();
        return page;
    }
}