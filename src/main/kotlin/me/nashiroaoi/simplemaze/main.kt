package me.nashiroaoi.simplemaze

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.requests.GatewayIntent
import java.awt.Color
import java.io.ByteArrayInputStream

val tempChannel : String = System.getenv("Temp_Channel")

class Game(private var maze : Maze):ListenerAdapter() {
    override fun onButtonClick(event : ButtonClickEvent) {
        println(event.button?.id)
        event.deferEdit().queue()
        when(event.button?.id){
            "top"->this.maze.moveUser(0)
            "bottom"->this.maze.moveUser(1)
            "left"->this.maze.moveUser(2)
            "right"->this.maze.moveUser(3)
            "reset"->{event.jda.removeEventListener(this);event.message?.delete()?.queue();return}
        }
        val imgURL = sendTemp(event.jda, this.maze.image())
        event.message?.editMessage(EmbedBuilder().also {
            it.setTitle("迷路 (${this.maze.mazeSize}×${this.maze.mazeSize})")
            it.setColor(Color.BLUE)
            it.setImage(imgURL)
        }.build())?.queue()
        if(this.maze.cleared()){
            event.jda.removeEventListener(this)
        }
    }
}

class BotClient:ListenerAdapter(){
    private lateinit var jda: JDA
    fun main(token:String){
        jda = JDABuilder.createLight(token,
            GatewayIntent.GUILD_MESSAGES)
            .addEventListeners(this)
            .build()
    }
    
    override fun onReady(event : ReadyEvent) {
        event.jda.presence.activity = Activity.playing("迷路ゲーム")
    }
    
    override fun onMessageReceived(event : MessageReceivedEvent) {
        if(!checkMention(event)){return}
        val command = Regex("(create|c)( +?)([0-9]+)").find(event.message.contentDisplay)?.groups
        if(command?.get(1)!=null && command[3]!=null){
            val size = command[3]!!.value.toInt()
            val m = Maze().create(size)
            val imgURL = sendTemp(jda,m.image())
            val embed = EmbedBuilder().also {
                it.setTitle("迷路 (${m.mazeSize}×${m.mazeSize})")
                it.setColor(Color.BLUE)
            it.setImage(imgURL)}
            
            event.channel.sendMessage(embed.build())
                .setActionRow(Button.primary("left","左").asEnabled(),
                    Button.primary("top","上").asEnabled()
                    ,Button.primary("bottom","下").asEnabled(),
                    Button.primary("right","右").asEnabled(),
                    Button.danger("reset","削除").asEnabled())
                .queue{event.jda.addEventListener(Game(m))}
        }
    }
    
}

private fun sendTemp(jda : JDA,inputStream : ByteArrayInputStream): String? {
    return jda.getTextChannelById(tempChannel)?.sendFile(inputStream,"maze.jpg")?.complete()?.attachments?.first()?.url
}
    
private fun checkMention(event : MessageReceivedEvent):Boolean{
    return (event.message.mentionedUsers.count() == 1
            && event.message.mentionedUsers.contains(event.jda.selfUser))
}

fun main(){
    val token = System.getenv("Discord_Bot_Token")
    val bot = BotClient()
    bot.main(token)
}