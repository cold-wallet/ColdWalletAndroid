package com.murano500k.coldwallet.assets

import android.content.Context
import androidx.room.CoroutinesRoom
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Asset::class), version = 1, exportSchema = false)
public abstract class AssetRoomDatabase : RoomDatabase() {

    abstract fun assetDao(): AssetDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AssetRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AssetRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AssetRoomDatabase::class.java,
                    "word_database"
                )
                    .addCallback(AssetDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class AssetDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.assetDao())
                }
            }
        }

        suspend fun populateDatabase(wordDao: AssetDao) {
            // Delete all content here.
            wordDao.deleteAll()

            // Add sample words.
            var asset = Asset(0,0, 1000.0f, "USD", "USD amount", "my USD amount")
            wordDao.insert(asset)
            asset = Asset(0,0, 700.0f, "EUR", "EUR amount", "my EUR amount")
            wordDao.insert(asset)
            asset = Asset(0,0, 1500.0f, "UAH", "UAH amount", "my UAH amount")
            wordDao.insert(asset)

            // TODO: Add your own words!
        }
    }
}