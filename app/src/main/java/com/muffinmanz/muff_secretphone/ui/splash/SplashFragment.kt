package com.muffinmanz.muff_secretphone.ui.splash

import android.Manifest
import android.app.PendingIntent
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.muffinmanz.muff_secretphone.MUFF_FOLDER
import com.muffinmanz.muff_secretphone.MainActivity
import com.muffinmanz.muff_secretphone.R
import com.muffinmanz.muff_secretphone.ZIP_FOLDER
import com.muffinmanz.muff_secretphone.broadcast.ShortcutReceiver
import com.muffinmanz.muff_secretphone.databinding.FragmentSplashBinding
import com.muffinmanz.muff_secretphone.extensions.observe
import com.muffinmanz.muff_secretphone.ui.base.BaseFragment

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>(R.layout.fragment_splash) {
    override val viewModel: SplashViewModel by viewModels()

    private val TAG = this.tag;

    private var shortcutBroadCast: ShortcutReceiver? = null

/*
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result: Map<String, Boolean> ->
        val deniedList: List<String> = result.filter {
            !it.value
        }.map {
            it.key
        }

        when {
            deniedList.isNotEmpty() -> {
                val map = deniedList.groupBy { permission ->
                    if(shouldShowRequestPermissionRationale(permission)) "DENIED" else "EXPLAINED"
                }
                map["DENIED"]?.let {
                    // request denied , request again
                    // 거부 한 번 했을경우 재요청
                    showToast("처음으로 권한 거부")
                }
                map["EXPLAINED"]?.let {
                    // request denied ,send to settings
                    // 거부 두 번 했을경우 설정
                    showToast("두번째 권한 거부")
                }
            }
            else -> {
                // All request are permitted
                createFolderCheck()
            }
        }
    }
*/
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val deniedPermissions = permissions.filterValues { !it }.keys
        if(deniedPermissions.isEmpty()) {
            createFolderCheck()
        } else {
            deniedPermissions.forEach {
                showToast("$it 권한이 필요합니다.")
            }
        }
    }
    private val allFilesPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                createFolderCheck()
            } else {
                showToast("모든 파일 액세스 권한이 필요합니다.")
            }
        }
    }

    /**
     * 퍼미션 확인
     * 승인: PackageManager.PERMISSION_GRANTED
     * 거부: PackageManager.PERMISSION_DENIED
     */
    private val allPermissionsGranted : Boolean by lazy {
/*
        var requiredPermissions = arrayOf(Manifest.permission.CAMERA);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Begin 6 => 23
            requiredPermissions.plus(Manifest.permission.RECORD_AUDIO);
        }

        if(
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M &&
            android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S_V2
        ) {
            requiredPermissions.plus(Manifest.permission.READ_EXTERNAL_STORAGE);
            requiredPermissions.plus(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        REQUIRED_PERMISSION = requiredPermissions
*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSION = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        } else {
            REQUIRED_PERMISSION = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        var permissionChk = true
        REQUIRED_PERMISSION.forEach {
            if(ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_DENIED) {
                permissionChk = false
            }
        }
        permissionChk
    }

    override fun init() {
        /* 숏컷 물어보기 */
        lifecycleScope.launchWhenResumed { createShortCut() }

        /* 숏컷 완성 여부 */
        observe(shortCutCompletedLiveData, ::afterShortCutCreate)

        // LG 테블릿 숏컷 에러 예상 되므로 바로 다음 단계로 이동
        afterShortCutCreate(true)

        /* ViewModel 연결 */
        subscribeUi()
    }

    private fun subscribeUi() {
        with(viewModel) {
            nextSignal.observe(viewLifecycleOwner, ::navigateToLogin)
        }
    }

    private fun afterShortCutCreate(isCompleted: Boolean) {
        if(isCompleted) {
            if(!allPermissionsGranted) {
                requestPermissions()
            } else {
                createFolderCheck()
            }
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(REQUIRED_PERMISSION)
    }

    private fun createFolderCheck() {
        var successToMakeMUFFFolder = true;
        //외부저장소 접근 권한 확인
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)

                allFilesPermissionLauncher.launch(intent)
                return
            }
        }

        Log.d(TAG, "# MUFF_FOLDER : " + MUFF_FOLDER);

        // if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        //     MUFF_FOLDER = File(context?.getExternalFilesDir(null)!!, "MUFF");
        //     ZIP_FOLDER = File(context?.getExternalFilesDir(null)!!, "zipData");
        // }

        if(!MUFF_FOLDER.exists()) {
            showToast("MUFF 폴더 없음.")
            successToMakeMUFFFolder = MUFF_FOLDER.mkdirs()
        }

        if(!successToMakeMUFFFolder) {
            showToast("MUFF 폴더를 생성하는데 실패하였습니다");
            // activity?.finishAndRemoveTask();
            // return;
        }

        var successToMakeZipFolder = true;

        Log.d(TAG, "# ZIP_FOLDER : " + ZIP_FOLDER);

        if(!ZIP_FOLDER.exists()) {
            showToast("ZIP 폴더 없음.")
            successToMakeZipFolder = ZIP_FOLDER.mkdirs()
        }

        if(!successToMakeZipFolder) {
            showToast("ZIP 폴더를 생성하는데 실패하였습니다");
            // activity?.finishAndRemoveTask();
            // return;
        }

        viewModel.updateIpConfig()
    }

    /* 숏컷 제작 */
    private fun createShortCut() {
        val prefs = requireContext().getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE)

        if(!prefs.getBoolean("shortCutExist", false)) {
            val drawable: Drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher, null)!!
            val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap

            val shortCutIntent = Intent(requireContext(), MainActivity::class.java)
            shortCutIntent.action = Intent.ACTION_CREATE_SHORTCUT

            val shortcutInfo: ShortcutInfoCompat = ShortcutInfoCompat
                .Builder(requireContext(), "EPV_head")
                .setIntent(shortCutIntent)
                .setShortLabel("MUFF\n시험본부")
                .setIcon(IconCompat.createWithBitmap(bitmap))
                .build()

            val pinnedShortcutCallbackIntent = Intent(shortcutBroadCast?.shortcutInstallAction)
            val successCallback = PendingIntent.getBroadcast(
                context,
                0,
                pinnedShortcutCallbackIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            ShortcutManagerCompat.requestPinShortcut(
                requireContext(),
                shortcutInfo,
                successCallback.intentSender
            )
        } else {
            shortCutCompletedLiveData.postValue(true)
        }
        val editor = prefs.edit()

        editor.putBoolean("shortCutExist", true)
        editor.apply()
    }

    private fun navigateToLogin(isNext: Boolean) {
        if(isNext && findNavController().currentDestination?.id == R.id.splashFragment) {
            val directions = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
            findNavController().navigate(directions)
        }
    }

    private fun registerShortcutAddedReceiver() {
        val shortcutAddedFilter = IntentFilter(shortcutBroadCast?.shortcutInstallAction)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            requireActivity().registerReceiver(
                shortcutBroadCast,
                shortcutAddedFilter,
                RECEIVER_EXPORTED
            )
        else
            requireActivity().registerReceiver(shortcutBroadCast, shortcutAddedFilter)
    }

    private fun unregisterShortcutAddedReceiver() {
        requireActivity().unregisterReceiver(shortcutBroadCast)
    }

    override fun onStart() {
        super.onStart()
        if(shortcutBroadCast == null) {
            shortcutBroadCast = ShortcutReceiver()
        }
        registerShortcutAddedReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterShortcutAddedReceiver()
        if(shortcutBroadCast != null) shortcutBroadCast = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unSubscribeUi()
    }

    private fun unSubscribeUi() {
        with(viewModel) {
            nextSignal.value = false
        }
    }

    companion object {
        val shortCutCompletedLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

        // Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
        private var REQUIRED_PERMISSION = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            "android.permission.READ_MEDIA_VISUAL_USER_SELECTED"
        )
    }
}