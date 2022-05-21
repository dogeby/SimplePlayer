package com.yang.simpleplayer.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.yang.simpleplayer.R
import com.yang.simpleplayer.SimplePlayerApplication
import com.yang.simpleplayer.databinding.SettingsActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback{
    private var _binding:SettingsActivityBinding? = null
    private val binding:SettingsActivityBinding get() = requireNotNull(_binding)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        _binding = SettingsActivityBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader, pref.fragment!!
        )
        fragment.arguments = args
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            /** 최근 동영상 목록 초기화 */
            val initRecentVideoListPreference = findPreference<Preference>(getString(R.string.init_recent_video_list_key))
            initRecentVideoListPreference?.setOnPreferenceClickListener {
                createInitAskAlertDialog(R.string.init_recent_video_ask){_, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        (context?.applicationContext as SimplePlayerApplication).appContainer.videoRepository.updateAllVideoInfoPlaybackDateNull()
                    }
                    showToastMessage(R.string.initSuccess)
                }?.show()
                true
            }
            /** 재생 목록 초기화 */
            val initPlaylistPreference = findPreference<Preference>(getString(R.string.init_playlist_key))
            initPlaylistPreference?.setOnPreferenceClickListener {
                createInitAskAlertDialog(R.string.init_playlist_ask) { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        (context?.applicationContext as SimplePlayerApplication).appContainer.playlistRepository.deleteAllPlaylist()
                    }
                    showToastMessage(R.string.initSuccess)
                }?.show()
                true
            }
            /** 오픈소스 라이선스 */
            val showOpenSourceLicensesPreference = findPreference<Preference>(getString(R.string.show_open_source_licenses_key))
            showOpenSourceLicensesPreference?.setOnPreferenceClickListener {
                startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_source_license))
                true
            }
        }
        private fun createInitAskAlertDialog(messageResId:Int, positiveListener: DialogInterface.OnClickListener) =
            context?.let {
                AlertDialog.Builder(it)
                    .setMessage(messageResId)
                    .setPositiveButton(R.string.init, positiveListener)
                    .setNegativeButton(R.string.cancel){_,_->}
                    .create()
            }
        private fun showToastMessage(resId:Int) {
            Toast.makeText(context, getString(resId), Toast.LENGTH_SHORT).show()
        }
    }
}