package com.none.eunoia

import Fragments.*
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView

class MainActivity : AppCompatActivity() {
    private val onNavigationItemSelectedListener=BottomNavigationView.OnNavigationItemSelectedListener {
        item->when(item.itemId) {
            R.id.nav_home -> {
                moveToFragment(homeFragment())
                return@OnNavigationItemSelectedListener true
            }
        R.id.nav_donate ->{
            item.isChecked=false
            startActivity(Intent(this@MainActivity,AddPostActivity::class.java))
            return@OnNavigationItemSelectedListener true

        }
        R.id.nav_profile->{
            moveToFragment(profileFragment())
            return@OnNavigationItemSelectedListener true

        }
        R.id.nav_search->{
            moveToFragment(searchFragment())
                return@OnNavigationItemSelectedListener true


        }
        R.id.nav_notifications-> {
            moveToFragment(notificationsFragment())
            return@OnNavigationItemSelectedListener true

        }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveToFragment(homeFragment())
    }
    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans=supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }

}