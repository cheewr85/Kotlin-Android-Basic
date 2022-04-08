/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.navigation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // DrawerLayout의 변수를 선언함
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // binding 객체에서 DrawerLayout을 불러옴
        drawerLayout = binding.drawerLayout

        // Up Button을 추가하기 위해서 선언하는 객체
        val navController = this.findNavController(R.id.myNavHostFragment)
        // 기존에서 Up Button과 함께 drawerLayout을 ActionBar에 추가를 함
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        // navigation drawer를 나타내는 것을 허용하는 함수 추가함
        // xml에서 추가한 NavigationView를 연결함(하지만 아래만 추가하면 스와이프해서 밖에 못 부름)
        NavigationUI.setupWithNavController(binding.navView, navController)


    }

    // Up Button을 추가하기 위해 오버라이딩 한 함수
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
//        return navController.navigateUp()
        // Up Button 외에 navigationView 버튼도 추가함
        return NavigationUI.navigateUp(navController, drawerLayout)
    }


}
