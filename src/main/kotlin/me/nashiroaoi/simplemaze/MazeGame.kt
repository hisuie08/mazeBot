package me.nashiroaoi.simplemaze

import java.util.*


object Maze {
    var mazeSize = 0
    
    // 壁: true, 道: false
    private lateinit var wall : Array<BooleanArray>
    var row = 0
    var col = 0
    var rowStack = Stack<Int>()
    var colStack = Stack<Int>()
    var usrRow : Int = mazeSize - 1
    var usrCol = 1
    var goalRow = 0
    var goalCol : Int = mazeSize - 2
    @JvmStatic
    fun main(args : Array<String>) {
        if(args.size != 1) {
            println("Usage: java Maze [mazeSize]")
            return
        }
        mazeSize = args[0].toInt()
        wall = Array(mazeSize) { BooleanArray(mazeSize) }
        printUsage()
        createMaze()
        resetUsr()
        resetGoal()
        val scan = Scanner(System.`in`)
        var keys = ""
        var key : Char
        val start = System.currentTimeMillis()
        val end : Long
        while(true) {
            printMaze()
            keys = scan.next()
            key = keys[keys.length - 1]
            moveUsr(key)
            if(usrRow == goalRow && usrCol == goalCol) {
                end = System.currentTimeMillis()
                printRezult((end - start) / 1000)
                break
            }
        }
    }
    
    // 新しく迷路を作るメソッド
    fun createMaze() {
        // 初期化
        for(i in 0 until mazeSize) {
            for(j in 0 until mazeSize) {
                wall[i][j] = true
            }
        }
        
        // ランダムに開始位置を選ぶ（1 〜 mazeSize - 2）
        val rnd = Random()
        row = rnd.nextInt(mazeSize - 2) + 1
        col = rnd.nextInt(mazeSize - 2) + 1
        wall[row][col] = false
        rowStack.push(row)
        colStack.push(col)
        var continueFlag = true
        
        // 以下、wall[][]全体を埋めるまで繰り返し
        while(continueFlag) {
            
            // 上下左右のいずれかに限界まで道を伸ばす
            Maze.extendPath()
            
            // 既にある道から次の開始位置を選ぶ（0 〜 mazeSize - 1（かつ 偶数？））
            continueFlag = false
            while(!Maze.rowStack.empty() && !Maze.colStack.empty()) {
                row = Maze.rowStack.pop()
                col = Maze.colStack.pop()
                if( /*row % 2 == 0 && col % 2 == 0 && */Maze.canExtendPath()) {
                    continueFlag = true
                    break
                }
            }
        }
    }
    
    // 迷路を表示するメソッド
    private fun printMaze() {
        for(i in 0 until mazeSize) {
            for(j in 0 until mazeSize) {
                if(i == usrRow && j == usrCol) {
                    print("**")
                } else if(i == goalRow && j == goalCol) {
                    print("GO")
                } else if(wall.get(i).get(j)) {
                    print("[]")
                } else {
                    print("  ")
                }
            }
            println()
        }
    }
    
    // 道を拡張するメソッド
    private fun extendPath() {
        var extendFlag = true
        while(extendFlag) {
            extendFlag = Maze.extendPathSub()
        }
    }
    
    // 道の拡張に成功したらtrue、失敗したらfalseを返すメソッド
    private fun extendPathSub() : Boolean {
        val rmd = Random()
        // 上: 0, 下: 1, 左: 2, 右: 3
        var direction : Int = rmd.nextInt(4)
        for(i in 0..3) {
            direction = (direction + i) % 4
            if(Maze.canExtendPathWithDir(direction)) {
                Maze.movePoint(direction)
                return true
            }
        }
        return false
    }
    
    // 指定した方向へ拡張可能ならばtrue、不可能ならばfalseを返すメソッド
    private fun canExtendPathWithDir(direction : Int) : Boolean {
        var exRow : Int = row
        var exCol : Int = col
        when(direction) {
            0 -> exRow--
            1 -> exRow++
            2 -> exCol--
            3 -> exCol++
        }
        return Maze.countSurroundingPath(exRow, exCol) <= 1
    }
    
    // 周囲1マスにある道の数を数えるメソッド
    private fun countSurroundingPath(row : Int, col : Int) : Int {
        var num = 0
        if(row - 1 < 0 || !wall[row - 1][col]) {
            num++
        }
        if(row + 1 > mazeSize - 1 || !wall[row + 1][col]) {
            num++
        }
        if(col - 1 < 0 || !wall.get(row).get(col - 1)) {
            num++
        }
        if(col + 1 > mazeSize - 1 || !wall.get(row).get(col + 1)) {
            num++
        }
        return num
    }
    
    // 指定した方向へ1マスrowとcolを移動させるメソッド
    fun movePoint(direction : Int) {
        when(direction) {
            0 -> row--
            1 -> row++
            2 -> col--
            3 -> col++
        }
        wall[row][col] = false
        rowStack.push(row)
        colStack.push(col)
    }
    
    // 上下左右いずれかの方向へ移動できるならtrue、できないならfalseを返すメソッド
    fun canExtendPath() : Boolean {
        return Maze.canExtendPathWithDir(0) || Maze.canExtendPathWithDir(1) || Maze.canExtendPathWithDir(2) || Maze.canExtendPathWithDir(
            3
        )
    }
    
    // ユーザを初期位置に動かすメソッド
    fun resetUsr() {
        usrRow = mazeSize - 1
        usrCol = 1
        while(true) {
            if(wall.get(usrRow - 1).get(usrCol)) {
                usrCol++
            } else {
                break
            }
        }
        wall[usrRow][usrCol] = false
    }
    
    // ゴールを初期位置に動かすメソッド
    fun resetGoal() {
        goalRow = 0
        goalCol = mazeSize - 2
        while(true) {
            if(wall.get(goalRow + 1).get(goalCol)) {
                goalCol--
            } else {
                break
            }
        }
        wall[goalRow][goalCol] = false
    }
    
    // ユーザを動かすメソッド
    fun moveUsr(key : Char) {
        val errMes = "You can not move there."
        var exUsrRow : Int = usrRow
        var exUsrCol : Int = usrCol
        when(key) {
            'w' -> exUsrRow--
            's' -> exUsrRow++
            'a' -> exUsrCol--
            'd' -> exUsrCol++
            'R' -> {
                resetUsr()
                return
            }
            'N' -> {
                createMaze()
                resetUsr()
                resetGoal()
                return
            }
            else -> {
                println(errMes)
                return
            }
        }
        if(exUsrRow > mazeSize - 1 || wall.get(exUsrRow).get(exUsrCol)) {
            println(errMes)
            return
        }
        usrRow = exUsrRow
        usrCol = exUsrCol
    }
    
    // 結果を表示するメソッド
    fun printRezult(secondTime : Long) {
        println()
        println("+-+-+-+-+-+-+-+-+-+")
        println("|c|o|n|g|r|a|t|s|!|")
        println("+-+-+-+-+-+-+-+-+-+")
        println()
        println("Your time is $secondTime seconds.")
        println()
    }
    
    // 遊び方を表示するメソッド
    fun printUsage() {
        println("Welcome to $mazeSize*$mazeSize Maze!")
        println()
        println("Usage:")
        println("** in the lower left is YOU.")
        println("GO in the upper right is GOAL.")
        println()
        println("Press the w key and the enter key to move UP.")
        println("Press the s key and the enter key to move DOWN.")
        println("Press the a key and the enter key to move LEFT.")
        println("Press the d key and the enter key to move RIGHT.")
        println()
        println("Press the R key and the enter key to RESTART game.")
        println("Press the N key and the enter key to start NEW game.")
        println()
        println("GAME START!!")
    }
}

fun main(){
    Maze.main(arrayOf("10"))
}