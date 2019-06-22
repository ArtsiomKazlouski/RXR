package com.ank.rxr

import org.junit.Test

import org.junit.Assert.*
import java.util.*
import com.ank.rxr.ExampleUnitTest.Flag



/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private enum class Flag(val value: Int, val description: String) {
        Aparel(1, "Апарельный (чето там)"),
        Kniling(2, "Прыауал (фывлори)"),
        Nizkopolniy(4, "Низкопольный"),
        eschoOdno(8, "Что-то еще")
    }

    @Test
    fun addition_isCorrect() {

        val parametr = 13

        var options = EnumSet.allOf(Flag::class.java).filter { flag ->
            (parametr and flag.value) == flag.value
        }

        for (op in options){
            when(op){
                Flag.Aparel -> println("visible1")
                Flag.Kniling -> println("visible2")
                Flag.Nizkopolniy -> println("visible3")
                Flag.eschoOdno -> println("visible4")
            }
        }

        println("op count ${options.count()}")
    }
}
