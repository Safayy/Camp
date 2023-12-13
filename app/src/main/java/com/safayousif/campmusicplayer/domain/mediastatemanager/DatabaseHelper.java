package com.safayousif.campmusicplayer.domain.mediastatemanager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import com.safayousif.campmusicplayer.R;
import com.safayousif.campmusicplayer.domain.model.PlaylistModel;
import com.safayousif.campmusicplayer.domain.model.SongModel;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    // Database Variables
    private static String DATABASE_NAME;
    private static int DATABASE_VERSION;
    private static String KEY_ID ;

    // Playlist Table
    private static String TABLE_PLAYLISTS;
    private static String KEY_PLAYLIST_NAME;

    // Song Table
    private static String TABLE_SONGS;
    private static String KEY_SONG_IDENTIFIER; //Identifier for songs using name+duration
    private static String KEY_SONG_ISFAVORITE;
    private static String KEY_SONG_ISARCHIVED; // should song be arched in next sync // TODO remove

    // Playlist Song
    private static String TABLE_ASSOCIATION_PLAYLIST_SONGS;
    private static String KEY_ASSOC_PLAYLIST_ID;
    private static String KEY_ASSOC_SONG_ID;

    // Archived Songs
    private static String TABLE_ARCHIVED_SONGS;
    private static String KEY_ARCHIVED_SONGS_ID;
    private static String KEY_ARCHIVED_SONGS_SONG_ID;

    // Table Creation Queries
    private static String QUERY_CREATE_TABLE_SONGS;
    private static String QUERY_CREATE_TABLE_PLAYLISTS;
    private static String QUERY_CREATE_TABLE_ASSOCIATION_PLAYLIST_SONGS;
    private static String QUERY_CREATE_TABLE_ARCHIVED_SONGS;

    public DatabaseHelper(Context context) {
        super(context, context.getString(R.string.database_name), null, 13); //TODO ++version if schema changed
        Log.d(TAG, "DatabaseHelper: Constructor");

        // Populate Variables
        DATABASE_NAME = context.getString(R.string.database_name);
        DATABASE_VERSION = Integer.parseInt(context.getString(R.string.database_version));
        KEY_ID = context.getString(R.string.key_id);
        TABLE_PLAYLISTS = context.getString(R.string.table_playlists);
        KEY_PLAYLIST_NAME = context.getString(R.string.key_playlist_name);
        TABLE_SONGS = context.getString(R.string.table_songs);
        KEY_SONG_IDENTIFIER = context.getString(R.string.key_song_identifier);
        KEY_SONG_ISFAVORITE = context.getString(R.string.key_song_is_favorite);
        KEY_SONG_ISARCHIVED = context.getString(R.string.key_song_is_archived);
        TABLE_ASSOCIATION_PLAYLIST_SONGS = context.getString(R.string.table_association_playlist_songs);
        KEY_ASSOC_PLAYLIST_ID = context.getString(R.string.key_assoc_playlist_id);
        KEY_ASSOC_SONG_ID = context.getString(R.string.key_assoc_song_id);
        TABLE_ARCHIVED_SONGS = context.getString(R.string.table_archived_songs);
        KEY_ARCHIVED_SONGS_ID = context.getString(R.string.key_archived_songs_id);
        KEY_ARCHIVED_SONGS_SONG_ID = context.getString(R.string.key_archived_songs_song_id);

        // Generate Queries
        QUERY_CREATE_TABLE_SONGS = context.getString(
                R.string.query_create_table_songs,
                TABLE_SONGS, KEY_ID, KEY_SONG_IDENTIFIER,
                KEY_SONG_ISFAVORITE, KEY_SONG_ISARCHIVED
        );
        QUERY_CREATE_TABLE_PLAYLISTS = context.getString(
                R.string.query_create_table_playlists,
                TABLE_PLAYLISTS, KEY_ID, KEY_PLAYLIST_NAME
        );
        QUERY_CREATE_TABLE_ASSOCIATION_PLAYLIST_SONGS = context.getString(
                R.string.query_create_table_association_playlist_songs,
                TABLE_ASSOCIATION_PLAYLIST_SONGS, KEY_ID,
                KEY_ASSOC_SONG_ID, KEY_ASSOC_PLAYLIST_ID,
                KEY_ASSOC_SONG_ID, TABLE_SONGS, KEY_ID,
                KEY_ASSOC_PLAYLIST_ID, TABLE_PLAYLISTS, KEY_ID
        );
        QUERY_CREATE_TABLE_ARCHIVED_SONGS = context.getString(
                R.string.query_create_table_archived_songs,
                TABLE_ARCHIVED_SONGS,
                KEY_ARCHIVED_SONGS_ID,
                KEY_ARCHIVED_SONGS_SONG_ID,
                KEY_ARCHIVED_SONGS_SONG_ID, TABLE_SONGS, KEY_ID
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Creating Tables");

        // Create tables
        db.execSQL(QUERY_CREATE_TABLE_SONGS);
        db.execSQL(QUERY_CREATE_TABLE_PLAYLISTS);
        db.execSQL(QUERY_CREATE_TABLE_ASSOCIATION_PLAYLIST_SONGS);
        db.execSQL(QUERY_CREATE_TABLE_ARCHIVED_SONGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade:");

        // Remove old tables and create new ones
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSOCIATION_PLAYLIST_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARCHIVED_SONGS);

        onCreate(db);
    }

    // Playlist operations
    public long addPlaylist(PlaylistModel playlist) {
        // Add playlist
        Log.d(TAG, "addPlaylist: " + playlist.getPlaylistName());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PLAYLIST_NAME, playlist.getPlaylistName());
        long rowIdPlaylist = db.insert(TABLE_PLAYLISTS, null, values);

        // Insert song entries for playlist
        if (rowIdPlaylist != -1) {
            for (SongModel song : playlist.getSongs()) {
                long songId = getSongId(song, db);
                if (songId != -1) {
                    ContentValues associationValues = new ContentValues();
                    associationValues.put(KEY_ASSOC_SONG_ID, songId);
                    associationValues.put(KEY_ASSOC_PLAYLIST_ID, rowIdPlaylist);
                    db.insert(TABLE_ASSOCIATION_PLAYLIST_SONGS, null, associationValues);
                }
            }
        }
        db.close();
        return rowIdPlaylist;
    }

    public void associateSongWithPlaylists(SongModel song, ArrayList<PlaylistModel> playlists) {
        SQLiteDatabase db = this.getWritableDatabase();

        long songId = getSongId(song, db);

        // Remove song playlist entries
        db.delete(TABLE_ASSOCIATION_PLAYLIST_SONGS, KEY_ASSOC_SONG_ID + "=?", new String[]{String.valueOf(songId)});

        // Insert new playlist entries
        int i = 0;
        for (PlaylistModel playlist : playlists) {
            i++;
            long playlistId = getPlaylistId(db, playlist);
            ContentValues associationValues = new ContentValues();
            associationValues.put(KEY_ASSOC_SONG_ID, songId);
            associationValues.put(KEY_ASSOC_PLAYLIST_ID, playlistId);
            db.insert(TABLE_ASSOCIATION_PLAYLIST_SONGS, null, associationValues);
            Log.d(TAG, "INSERTED INTO association table " + songId );
        }
        Log.d(TAG, "Ran times = " + i + "\n Playlists were = " + playlists.size() );
        db.close();
    }

    // Helper method to get the Playlist ID based on its name
    private long getPlaylistId(SQLiteDatabase db, PlaylistModel playlist) {
        long playlistId = -1;

        // Get Playlist ID Based on Name
        String playlistName = playlist.getPlaylistName();
        Cursor cursor = db.rawQuery(
                "SELECT " + KEY_ID + " FROM " + TABLE_PLAYLISTS +
                        " WHERE " + KEY_PLAYLIST_NAME + "=?",
                new String[]{playlistName});

        int idCursorIndex = cursor.getColumnIndex(KEY_ID);
        if (cursor.moveToFirst() && idCursorIndex != -1) {
            playlistId = cursor.getLong(idCursorIndex);
        }

        cursor.close();
        return playlistId;
    }

    private long getSongId(SongModel song, SQLiteDatabase db) {
        long songId = -1;

        // Get Song ID Based on Identifier
        String songIdentifier = song.getTitle() + song.getDuration();
        Cursor cursor = db.rawQuery(
                "SELECT " + KEY_ID + " FROM " + TABLE_SONGS +
                        " WHERE " + KEY_SONG_IDENTIFIER + "=?",
                new String[]{songIdentifier});

        int idCursorIndex = cursor.getColumnIndex(KEY_ID);
        if (cursor.moveToFirst() && idCursorIndex != -1) {
            songId = cursor.getLong(idCursorIndex);
        }

        cursor.close();
        return songId;
    }

    public ArrayList<PlaylistModel> getPlaylists(Context context) {
        ArrayList<PlaylistModel> playlists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to fetch playlist names from the playlists table
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PLAYLISTS, null);

        int indexColsKeyPlaylistName = cursor.getColumnIndex(KEY_PLAYLIST_NAME);
        int indexColsKeyID = cursor.getColumnIndex(KEY_ID);
        if (cursor != null && indexColsKeyPlaylistName >= 0 && indexColsKeyID >= 0 && cursor.moveToFirst()) {
            do {
                String playlistName = cursor.getString(indexColsKeyPlaylistName);
                long playlistId = cursor.getLong(indexColsKeyID);
                // Fetch songs associated with the playlist from the association table
                ArrayList<SongModel> songs = getSongsForPlaylist(db, playlistId);

                // Construct PlaylistModel object and add it to the list
                PlaylistModel playlist = new PlaylistModel(playlistName, songs, context);
                playlists.add(playlist);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        Log.i(TAG, "getPlaylists: Successfully got " + playlists.size() + " playlists");
        return playlists;
    }

    private ArrayList<SongModel> getSongsForPlaylist(SQLiteDatabase db, long playlistId) {
        ArrayList<SongModel> songs = new ArrayList<>();

        // Query to get Song IDs
        Cursor cursor = db.rawQuery(
                "SELECT " + KEY_ASSOC_SONG_ID + " FROM " + TABLE_ASSOCIATION_PLAYLIST_SONGS +
                        " WHERE " + KEY_ASSOC_PLAYLIST_ID + "=?",
                new String[]{String.valueOf(playlistId)});

        int indexColsKeyAssocSongId = cursor.getColumnIndex(KEY_ASSOC_SONG_ID);
        if (cursor != null && indexColsKeyAssocSongId >= 0 && cursor.moveToFirst()) {
            do {
                // Get Identifier String
                long songId = cursor.getLong(indexColsKeyAssocSongId);
                String songIdentifier = getSongIdentifierById(db, songId);

                // Get Song from Identifier
                if (songIdentifier != null) {
                    SongModel song = getSongById(db, songIdentifier);
                    if (song != null) {
                        songs.add(song);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.i(TAG, "getSongsForPlaylist: Successfully got " + songs.size() + " songs");
        return songs;
    }

    private String getSongIdentifierById(SQLiteDatabase db, long songId) {
        Cursor cursor = db.rawQuery(
                "SELECT " + KEY_SONG_IDENTIFIER + " FROM " + TABLE_SONGS +
                        " WHERE " + KEY_ID + "=?",
                new String[]{String.valueOf(songId)});

        int indexColsKeySongIdentifier = cursor.getColumnIndex(KEY_SONG_IDENTIFIER);
        if (cursor != null && indexColsKeySongIdentifier >= 0 && cursor.moveToFirst()) {
            String songIdentifier = cursor.getString(indexColsKeySongIdentifier);
            cursor.close();
            return songIdentifier;
        }

        return null;
    }

    private SongModel getSongById(SQLiteDatabase db, String songIdentifier) {
        SongModel song = FileManager.getInstance().getSongByIdentifier(songIdentifier);
        return song;
    }

    /* Displays the data of any table
     * Example Usage: dbHelper.displayTable(context.getString(R.string.table_association_playlist_songs)); */
    public void displayTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Get columns
            cursor = db.query(tableName, null, null, null, null, null, null);

            // Log column names
            if (cursor != null) {
                String[] columnNames = cursor.getColumnNames();
                Log.d(TAG, "Columns: " + Arrays.toString(columnNames));
            }

            // Log data
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    StringBuilder rowData = new StringBuilder();

                    // Loop through columns and append to rowData
                    for (String columnName : cursor.getColumnNames()) {
                        int columnIndex = cursor.getColumnIndex(columnName);
                        if (columnIndex >= 0) {
                            rowData.append(columnName)
                                    .append(": ")
                                    .append(cursor.getString(columnIndex))
                                    .append(", ");
                        }
                    }

                    // Log rowData
                    Log.d(TAG, rowData.toString());

                } while (cursor.moveToNext());
            }
        } finally {
            // Close cursor
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public void addSong(SongModel song) {
        SQLiteDatabase db = this.getWritableDatabase();
        String songIdentifier = song.getTitle() + song.getDuration();

        // Check if the song already exists in the database
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_SONGS +
                    " WHERE " + KEY_SONG_IDENTIFIER + "=?",
                new String[]{songIdentifier});

        if (cursor.getCount() > 0) {
            // Update existing model is favorite
            cursor.moveToFirst();
            int isFavoriteColumnIndex = cursor.getColumnIndex(KEY_SONG_ISFAVORITE);
            int isFavoriteValue = cursor.getInt(isFavoriteColumnIndex);
            song.setIsFavorite(isFavoriteValue == 1);
        } else {
            // Insert song into the table
            ContentValues values = new ContentValues();
            values.put(KEY_SONG_IDENTIFIER, songIdentifier);
            values.put(KEY_SONG_ISFAVORITE, song.isFavorite() ? 1 : 0);

            db.insert(TABLE_SONGS, null, values);
        }

        cursor.close();
        db.close();
    }

    public void updateSongIsFavorite(SongModel song) {
        SQLiteDatabase db = this.getWritableDatabase();

        String songIdentifier = song.getTitle() + song.getDuration();

        // Check if the song already exists in the database
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_SONGS +
                        " WHERE " + KEY_SONG_IDENTIFIER + "=?",
                new String[]{songIdentifier});

        if (cursor.getCount() > 0) {
            // Song exists, update the isFavorite value
            ContentValues values = new ContentValues();
            values.put(KEY_SONG_ISFAVORITE, song.isFavorite() ? 1 : 0);
            db.update(TABLE_SONGS, values, KEY_SONG_IDENTIFIER + "=?", new String[]{songIdentifier});
        }

        cursor.close();
        db.close();
    }
}