package me.nashiroaoi.simplemaze

import java.util.*
import kotlin.random.Random

class  _Maze {
    lateinit var gameData: Array<Array<Coordinate>>
    lateinit var pointer:Coordinate
    var allCoordinate = ArrayList<Coordinate>()
    
    private fun coordinate(x:Int, y:Int) : Coordinate = this.gameData[x][y]
    
    private fun canExtend(coordinate : Coordinate,direction:Int) : Boolean {
        var result=true
        when(direction){
            1-> {if(coordinate.top().checkRoad()>1){result=false}}
            2->{if(coordinate.bottom().checkRoad()>1){result=false}}
            3->{if(coordinate.right().checkRoad()>1){result=false}}
            4->{if(coordinate.left().checkRoad()>1){result=false}}
        }
        return result
    }
    
    fun create(s:Int) : Maze {
        /*一辺が偶数だったら1足して奇数面にする*/
        var size = s
        if(s%2==0){size+=1}
        /*gameDataにマップの二次元配列を代入。ついでに全座標のリストも作っとく。*/
        this.gameData = Array(size+2) { Array(size+2) { Coordinate() } }.also {c->
            c.forEach { l->l.forEach {it.y=c.indexOf(l);it.x=l.indexOf(it);this.allCoordinate.add(it)}}}
        
        /*迷路の最外周はアルゴリズム判定用の道。それ以外をまず壁で埋める*/
        this.gameData.forEach {
            if(this.gameData.indexOf(it)!=0 && this.gameData.indexOf(it)!=this.gameData.lastIndex){
                it.forEach { c->
                    if(it.indexOf(c) !=0 && it.indexOf(c)!=it.lastIndex){
                        c.state=Coordinate.PointerState.Wall}}}}
        /*座標からランダムに一つ選ぶ*/
        
        val start = allCoordinate.shuffled()[0]
        
    
        return this
    }
}

fun main(){
    val m = Maze().create(15)
    for(l in m.gameData){
        for(k in l){
            print(k.printByState()+" ")
        }
        println()
    }
    
}