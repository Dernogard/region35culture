package ru.dernogard.region35culture.ui.main.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.dernogard.region35culture.CultureApiService
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.database.repo.CultureObjectLocalRepo
import ru.dernogard.region35culture.worker.UpdateCultureWorker
import javax.inject.Inject

/**
 * I will use the Hilt because I'd most like it than Dagger.
 * However the Hilt is compatible with Dagger2 and can be used together with it.
 */

private const val WORK_UPDATE_CULTURE_INFO = "updateCultureWork"

@AndroidEntryPoint
class CultureActivity : AppCompatActivity() {

    @Inject lateinit var repo: CultureObjectLocalRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.culture_activity)
        //setupBackgroundWork()
        update()
        NavigationUI
            .setupActionBarWithNavController(this, findNavController(R.id.main_nav_host_fragment))
    }

    private fun update() {
        //CultureApiService(repo).getDataAndSaveIt()
    }

    private fun setupBackgroundWork() {
        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                WORK_UPDATE_CULTURE_INFO,
                ExistingPeriodicWorkPolicy.KEEP,
                UpdateCultureWorker.installBackgroundWork()
            )
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.main_nav_host_fragment).navigateUp()
}