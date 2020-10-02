package ru.dernogard.region35culture.ui.main.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.worker.UpdateCultureWorker

/**
 * I will use the Hilt because I'd most like it than Dagger.
 * However the Hilt is compatible with Dagger2 and can be used together with it.
 */

private const val WORK_UPDATE_CULTURE_INFO = "updateCultureWork"

@AndroidEntryPoint
class CultureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.culture_activity)
        setupBackgroundWork()
        NavigationUI
            .setupActionBarWithNavController(this, findNavController(R.id.main_nav_host_fragment))
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