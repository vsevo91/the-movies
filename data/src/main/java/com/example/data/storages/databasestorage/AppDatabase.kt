package com.example.data.storages.databasestorage

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.R
import com.example.data.storages.databasestorage.entities.*
import com.example.domain.utilities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserCollectionDb::class,
        CollectionMovieCrossRefDb::class,
        MovieDb::class,
        GenreDb::class,
        CountryDb::class,
        MovieGenreCrossRefDb::class,
        MovieCountryCrossRefDb::class,
        AddingInfoDb::class,
        IsThisFirstEntranceDb::class
    ],
    views = [
        MovieWithGenreAndCountryDb::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userCollectionDao(): UserCollectionDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, DATABASE_NAME
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d(APPLICATION_TAG, "DB is being created")
                        val scope = CoroutineScope(Dispatchers.IO)
                        scope.launch {
                            getInstance(context).userCollectionDao().addAllUserCollections(
                                defineDefaultData(context)
                            )
                            getInstance(context).userCollectionDao().noEntranceWasCommitted()
                        }
                    }
                })
                .build()

        private fun defineDefaultData(context: Context): List<UserCollectionDb> {
            return listOf(
                UserCollectionDb(
                    collectionId = FAVORITE_COLLECTION_ID,
                    icon = R.drawable.ic_heart,
                    name = context.resources.getString(R.string.favorite),
                    canBeDeleted = false,
                    isHidden = false
                ),
                UserCollectionDb(
                    collectionId = TO_WATCH_COLLECTION_ID,
                    icon = R.drawable.ic_bookmark,
                    name = context.resources.getString(R.string.to_watch),
                    canBeDeleted = false,
                    isHidden = false
                ),
                UserCollectionDb(
                    collectionId = WATCHED_COLLECTION_ID,
                    icon = R.drawable.ic_eye,
                    name = context.resources.getString(R.string.watched),
                    canBeDeleted = false,
                    isHidden = true
                ),
                UserCollectionDb(
                    collectionId = WAS_INTERESTING_COLLECTION_ID,
                    icon = R.drawable.ic_eye,
                    name = context.resources.getString(R.string.was_interesting),
                    canBeDeleted = false,
                    isHidden = true
                )
            )
        }
    }
}