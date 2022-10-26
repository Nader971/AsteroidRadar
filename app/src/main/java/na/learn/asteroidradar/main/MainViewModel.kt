package na.learn.asteroidradar.main


import android.app.Application
import androidx.lifecycle.*
import na.learn.asteroidradar.models.Asteroid
import na.learn.asteroidradar.models.PictureOfDay
import na.learn.asteroidradar.database.getDatabase
import na.learn.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import na.learn.asteroidradar.api.getPictureOfDay
import na.learn.asteroidradar.repository.seventhDay
import na.learn.asteroidradar.repository.today


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?>
        get() = _pictureOfDay

    private val _navigateToDetailAsteroid = MutableLiveData<Asteroid?>()
    val navigateToDetailAsteroid: LiveData<Asteroid?>
        get() = _navigateToDetailAsteroid

    fun doneNavigated() {
        _navigateToDetailAsteroid.value = null
    }


    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        todayClicked()
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            refreshPictureOfDay()
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetailAsteroid.value = asteroid
    }

    private suspend fun refreshPictureOfDay()  {

        _pictureOfDay.value = getPictureOfDay()

    }



    fun todayClicked() {
        viewModelScope.launch {
            database.asteroidDao.getAsteroids(today(), today())
                .collect { asteroids ->
                    _asteroids.value = asteroids
                }
        }
    }

    fun nextWeekClicked() {
        viewModelScope.launch {
            database.asteroidDao.getAsteroids(seventhDay(), seventhDay())
                .collect { asteroids ->
                    _asteroids.value = asteroids
                }
        }
    }

    fun savedClicked() {
        viewModelScope.launch {
            database.asteroidDao.getAllAsteroids().collect { asteroids ->
                _asteroids.value = asteroids
            }
        }
    }

}