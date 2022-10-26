package na.learn.asteroidradar.repository


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import na.learn.asteroidradar.api.*
import na.learn.asteroidradar.database.AsteroidDatabase
import na.learn.asteroidradar.database.asDomainModel
import na.learn.asteroidradar.models.Asteroid
import na.learn.asteroidradar.utils.Constants
import okhttp3.ResponseBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    suspend fun  refreshAsteroids(
        startDate: String = today(),
        endDate: String = seventhDay()
    ) {
        var asteroidList: ArrayList<Asteroid>
        withContext(Dispatchers.IO) {
            val asteroidResponseBody: ResponseBody = Network.service.getAsteroidsAsync(
                startDate, endDate,
                Constants.NASA_API_KEY
            )
                .await()
            asteroidList = parseAsteroidsJsonResult(JSONObject(asteroidResponseBody.string()))
            database.asteroidDao.insertAll(*asteroidList.asDomainModel())
        }
    }

}


private fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(date)
}

fun today(): String {
    val calendar = Calendar.getInstance()
    return formatDate(calendar.time)
}

fun seventhDay(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 7)
    return formatDate(calendar.time)
}



