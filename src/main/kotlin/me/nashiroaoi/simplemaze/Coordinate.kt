package me.nashiroaoi.simplemaze

open class Coordinate{
    /**
     * 迷路の各座標クラス
     * x:横
     * y:縦
     * state:道か壁か
     */
    enum class PointerState{
        Wall,
        Road;
    }
    var x : Int=0
    var y : Int=0
    var state : PointerState=PointerState.Road
    var buried:Boolean=this.state==PointerState.Wall
    
    fun updateState(state : PointerState) : Coordinate =this.also { this.state=state }
    
    fun isWall(maze : Maze):Boolean=(
            this.x > 1 && this.x < maze.gameData.size-1 && this.y > 1&& this.y < maze.gameData.size-1)
    
    fun printByState():String{
        return when(this.state){
            PointerState.Wall->"＠"
            PointerState.Road->"　"
        }
    }
    
    fun checkRoad():Int{
        var result = 0
        if(!this.top().buried){result+=1}
        if(!this.bottom().buried){result+=1}
        if(!this.right().buried){result+=1}
        if(!this.left().buried){result+=1}
        return result
    }

    fun bury()=this.apply { this.state=PointerState.Wall }
    
    fun top():Coordinate=this.apply { this.y +=1 }
    fun bottom():Coordinate=this.apply { this.y -=1 }
    fun left():Coordinate=this.apply { this.x -=1 }
    fun right():Coordinate=this.apply { this.x +=1 }
}
