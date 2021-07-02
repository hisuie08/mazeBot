package me.nashiroaoi.simplemaze

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO


class Maze {
    lateinit var gameData: Array<BooleanArray>
    var mazeSize:Int=0
    var pointerRow = 0
    var pointerCol = 0
    var rowStack = Stack<Int>()
    var colStack = Stack<Int>()
    var usrRow : Int = this.mazeSize - 1
    var usrCol = 1
    var goalRow = 0
    var goalCol : Int = this.mazeSize - 2
    
    fun create(size:Int):Maze{
        this.mazeSize=size
        this.gameData=Array(size){ BooleanArray(size){true} }
    
        val rnd = Random()
        this.pointerRow = rnd.nextInt(this.mazeSize - 2) + 1
        this.pointerCol = rnd.nextInt(this.mazeSize - 2) + 1
        this.gameData[this.pointerRow][this.pointerCol]=false
        this.rowStack.push(this.pointerRow)
        this.colStack.push(this.pointerCol)
        
        var continueFlag=true
        while(continueFlag){
            this.extend()
            continueFlag = false
            while(!this.rowStack.empty() && !this.colStack.empty()) {
                this.pointerRow = this.rowStack.pop()
                this.pointerCol = this.colStack.pop()
                if(this.canExtendAny()) {
                    continueFlag = true
                    break
                }
            }
        }
        this.resetUsr()
        this.resetGoal()
        return this
    }
    
    private fun extend() {
        var extendFlag = true
        while(extendFlag) {
            extendFlag = this.extendSub()
        }
    }
    
    private fun extendSub() : Boolean {
        // 上: 0, 下: 1, 左: 2, 右: 3
        for(i in IntRange(0,3).toList().shuffled()) {
            if(this.canExtend(i)) {
                this.move(i)
                return true
            }
        }
        return false
    }
    
    private fun canExtend(direction : Int) : Boolean {
        var exRow : Int = this.pointerRow
        var exCol : Int = this.pointerCol
        when(direction) {
            0 -> exRow--
            1 -> exRow++
            2 -> exCol--
            3 -> exCol++
        }
        return this.countSurroundingPath(exRow, exCol) <= 1
    }
    
    private fun countSurroundingPath(row : Int, col : Int) : Int {
        var num = 0
        if(row - 1 < 0 || !this.gameData[row - 1][col]) {
            num++
        }
        if(row + 1 > this.mazeSize - 1 || !this.gameData[row + 1][col]) {
            num++
        }
        if(col - 1 < 0 || !this.gameData[row][col - 1]) {
            num++
        }
        if(col + 1 > this.mazeSize - 1 || !this.gameData[row][col + 1]) {
            num++
        }
        return num
    }
    
    private fun canExtendAny():Boolean=canExtend(0)||canExtend(1)||canExtend(2)||canExtend(3)
    
    private fun move(direction:Int){
        when(direction){
            0->{this.pointerRow--}
            1->{this.pointerRow++}
            2->{this.pointerCol--}
            3->{this.pointerCol++}
        }
        this.gameData[this.pointerRow][this.pointerCol]=false
        this.rowStack.push(this.pointerRow)
        this.colStack.push(this.pointerCol)
    }
    
    private fun resetUsr() : Maze {
        this.usrRow = this.mazeSize - 1
        this.usrCol = 1
        while(true) {
            if(this.gameData[this.usrRow - 1][this.usrCol]) {
                this.usrCol++
            } else {
                break
            }
        }
        this.gameData[this.usrRow][this.usrCol] = false
        return this
    }
    
    private fun resetGoal() : Maze {
        this.goalRow = 0
        this.goalCol = this.mazeSize - 2
        while(true) {
            if(this.gameData[this.goalRow + 1][this.goalCol]) {
                this.goalCol--
            } else {
                break
            }
        }
        this.gameData[this.goalRow][this.goalCol] = false
        return this
    }
    
    fun moveUser(direction : Int) : Maze {
        var exUsrRow : Int = this.usrRow
        var exUsrCol : Int = this.usrCol
        when(direction){
            0->{exUsrRow--}
            1->{exUsrRow++}
            2->{exUsrCol--}
            3->{exUsrCol++}
        }
        if(exUsrRow > this.mazeSize - 1 || this.gameData[exUsrRow][exUsrCol]) { return this }
        this.usrRow=exUsrRow
        this.pointerRow=exUsrRow
        this.usrCol=exUsrCol
        this.pointerCol=exUsrCol
        return this
    }
    fun cleared():Boolean{
        return this.usrRow==this.goalRow && this.usrCol == this.goalCol
    }
    
    fun image() : ByteArrayInputStream {
        val wall = BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB).apply {
            this.createGraphics().also {
                it.color = Color.WHITE
                it.fillRect(0, 0, this.width, this.height)
            }
        }
        val road = BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB).apply {
            this.createGraphics().also {
                it.color = Color.BLACK
                it.fillRect(0, 0, this.width, this.height)
            }
        }
        
        val goal = BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB).apply {
            this.createGraphics().also { it.drawString("🚩", 2, 13) }
        }
        
        val user = BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB).apply {
            this.createGraphics().also { it.drawString("ℹ️", 3, 13) }
        }
        
        val result = BufferedImage(16 * this.mazeSize, 16 * this.mazeSize, BufferedImage.TYPE_INT_RGB)
        for(x in 0 until this.mazeSize) {
            for(y in 0 until this.mazeSize) {
                when {
                    x == usrRow && y == usrCol -> {
                        result.graphics.drawImage(user,  16 * y,16 * x, null)
                    }
                    x == goalRow && y == goalCol -> {
                        result.graphics.drawImage(goal, 16 * y,16 * x, null)
                    }
                    gameData[x][y] -> {
                        result.graphics.drawImage(wall, 16 * y,16 * x, null)
                    }
                    !gameData[x][y] -> {
                        result.graphics.drawImage(road, 16 * y,16 * x, null)
                    }
                }
            }
        }
        val baos = ByteArrayOutputStream()
        ImageIO.write(result, "jpg", baos)
        baos.flush()
        val imageByte = baos.toByteArray()
        baos.close()
        return ByteArrayInputStream(imageByte)
    }
    
}
