package com.shojishunsuke.myapplication

import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.FileNotFoundException

class Sudoku(texts: FirebaseVisionText) {
    private val data = Array(9) { Array(9, { it }) }
    private val texts = texts

    fun readQuestion() {
        try {
//            val file =
//                File("/Users/shojishunsuke/AndroidStudioProjects/MyApplication2/app/src/main/res/assets/question.txt")
//
//            val fileReader = FileReader(file)
//            val bufferedReader = BufferedReader(fileReader)
            for (block in texts.textBlocks)
                for (line in block.lines) {
                    var count = 0
                    while (count < 9) {

                        val lineText = line.text

                        for (i in 0..8) {

                            if (count < 9) {
                                var w: Int = Integer.parseInt(lineText[i].toString())
                                data[count][i] = w
                            }
                        }

                        count++
                    }
                }


        } catch (e: FileNotFoundException) {
            println(e)
        }

        System.out.println(data[8][6])

    }


    fun solve() {
        var isDone = false

        do {
            for (x in 0..8) {
                for (y in 0..8) {
                    var num = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
                    if (data[x][y] == 0) {
                        var count = 0
                        var renew = 0
                        var qtX = x / 3
                        var qtY = y / 3


                        for (n in 0..8) {
                            if (data[x][n] != 0) num[data[x][n] - 1] = 1
                        }

                        for (n in 0..8) {
                            if (data[n][y] != 0) num[data[n][y] - 1] = 1
                        }

                        for (s in qtX * 3..qtX * 3 + 2) {
                            for (t in qtY * 3..qtY * 3 + 2) {
                                if (data[s][t] != 0) num[data[s][t] - 1] = 1
                            }
                        }

                        for (i in 0..8) {
                            if (num[i] == 1) count++
                            else if (num[i] == 0) renew = i + 1
                        }

                        if (count == 8) {
                            data[x][y] = renew
                        }
                    }
                }


            }

            var count = 0
            for (x in 0..8) {
                for (y in 0..8) {
                    if (data[x][y] != 0) count++
                }
            }

            if (count == 81) isDone = true


        } while (!isDone)


    }

    fun view() {
        for (x in 0..8) {
            for (y in 0..8) {
                System.out.print(data[x][y])
            }
        }
    }
}



