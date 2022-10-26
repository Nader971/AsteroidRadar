package na.learn.asteroidradar.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import na.learn.asteroidradar.models.Asteroid


@Dao
interface AsteroidDao {

    @Query("SELECT * FROM asteroids WHERE closeApproachDate >= :startDate AND closeApproachDate <= :endDate ORDER BY closeApproachDate ASC")
    fun getAsteroids(startDate: String, endDate: String): Flow<List<Asteroid>>

    @Query("SELECT * FROM asteroids ORDER BY closeApproachDate ASC")
    fun getAllAsteroids(): Flow<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE FROM asteroids WHERE closeApproachDate < :today")
    fun deletePreviousDayAsteroids(today: String): Int
}

@Database(entities = [DatabaseAsteroid::class], version = 1, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidDatabase::class.java,
                "asteroids").build()
        }
    }
    return INSTANCE
}