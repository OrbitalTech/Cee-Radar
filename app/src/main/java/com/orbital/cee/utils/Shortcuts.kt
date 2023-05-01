package com.orbital.cee.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.orbital.cee.R
import com.orbital.cee.core.GeofenceBroadcastReceiver
import com.orbital.cee.view.MainActivity
import com.orbital.cee.view.home.HomeActivity

const val shortcut_website_id = "id_website"
const val shortcut_messages_id = "id_messages"

@RequiresApi(Build.VERSION_CODES.N_MR1)
object Shortcuts {

    fun setUp(context: Context) {
        val shortcutManager =
            getSystemService<ShortcutManager>(context, ShortcutManager::class.java)


        val shortcut = ShortcutInfo.Builder(context, shortcut_website_id)
            .setShortLabel("Place Report")
            .setLongLabel("Place Road Camera")
            .setIcon(Icon.createWithResource(context, R.drawable.ic_marker_road_camera))
            .setIntent(
                Intent(Intent.ACTION_VIEW, null, context, HomeActivity::class.java).apply {
                    putExtra("action_type",1)
                }

            )
            .build()
//        val shortcut2 = ShortcutInfo.Builder(context, shortcut_messages_id)
//            .setShortLabel("Trip")
//            .setLongLabel("Start Trip")
//            .setIcon(Icon.createWithResource(context, R.drawable.ic_play))
//            .setIntent(Intent(Intent.ACTION_VIEW, null, context, HomeActivity::class.java).apply {
//                putExtra("action_type",2)
//            })
//            .build()

        shortcutManager!!.dynamicShortcuts = listOf(shortcut)



    }

}