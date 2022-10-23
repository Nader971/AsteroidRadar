package na.learn.asteroidradar.work

import android.content.Context
import android.content.ContextParams
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import na.learn.asteroidradar.database.getDatabase
import na.learn.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "RefreshAstroidWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}