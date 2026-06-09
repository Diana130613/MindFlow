package ru.mindflow.app.foundation.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.mindflow.app.foundation.local.dao.GardenDao
import ru.mindflow.app.foundation.local.dao.MeditationDao
import ru.mindflow.app.foundation.local.dao.MoodEntryDao
import ru.mindflow.app.foundation.local.entity.GardenEntity
import ru.mindflow.app.foundation.local.entity.MeditationEntity
import ru.mindflow.app.foundation.local.entity.MoodEntryEntity

@Database(
    entities = [MeditationEntity::class, MoodEntryEntity::class, GardenEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MindFlowDatabase : RoomDatabase() {

    abstract fun meditationDao(): MeditationDao
    abstract fun moodEntryDao(): MoodEntryDao
    abstract fun gardenDao(): GardenDao

    companion object {
        @Volatile private var INSTANCE: MindFlowDatabase? = null

        fun getInstance(context: Context): MindFlowDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MindFlowDatabase::class.java,
                    "mindflow.db"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
    }
}
