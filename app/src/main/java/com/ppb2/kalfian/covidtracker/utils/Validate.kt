package com.ppb2.kalfian.covidtracker.utils

import com.ppb2.kalfian.covidtracker.models.TestCovid

class Validate {
    companion object {
        fun hasTestCovid(listTest: ArrayList<TestCovid>, test: String): Boolean {
            listTest.forEach { tc ->
                if(tc.type.equals(test, true)) {
                    return true
                }
            }

            return false
        }

        fun isAlreadyCheckIn(listUser: ArrayList<String>, uid: String): Boolean {
            listUser.forEach {
                if (it == uid) {
                    return true
                }
            }

            return false
        }
    }
}