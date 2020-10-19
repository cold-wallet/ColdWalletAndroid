package com.murano500k.coldwallet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(
    Asset::class,
    CryptoCode::class,
    CryptoPricePair::class
), version = 3, exportSchema = false)
public abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun assetDao(): AssetDao

    abstract fun cryptoCodeDao(): CryptoCodeDao

    abstract fun cryptoPricePairDao(): CryptoPricePairDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MyRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyRoomDatabase::class.java,
                    "word_database"
                )
                    .addCallback(AssetDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
        fun getDatabase(context: Context): MyRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyRoomDatabase::class.java,
                    "word_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class AssetDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
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