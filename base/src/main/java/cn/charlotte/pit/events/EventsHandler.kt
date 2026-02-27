package cn.charlotte.pit.events

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.EventQueue
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import net.mizukilab.pit.util.random.RandomUtil
import org.bukkit.Bukkit
import java.util.*
import kotlin.random.Random

object EventsHandler {
    val epicQueue: Queue<String> = LinkedList()
    val normalQueue: Queue<String> = LinkedList()

    fun refreshEvents() {
        val factory = ThePit.getInstance().eventFactory

        // 减少队列大小，从50改为30
        var count = epicQueue.size
        if (count < 30) {
            val need = 30 - count
            for (index in 0 until need) {
                val until = factory.epicEvents.size
                if (until > 0) {
                    val nextInt = Random.nextInt(until)
                    val event = factory.epicEvents[nextInt] as AbstractEvent
                    epicQueue.add(event.eventInternalName)
                }
            }
        }

        // 减少队列大小，从100改为60
        count = normalQueue.size
        if (count < 60) {
            val need = 60 - count
            for (index in 0 until need) {
                val until1 = factory.normalEvents.size
                if (until1 < 1) {
                    continue
                }
                val event = factory.normalEvents[Random.nextInt(until1)] as AbstractEvent
                if (event.eventInternalName.equals("auction") && RandomUtil.hasSuccessfullyByChance(0.75)) {
                    val until = factory.normalEvents.size
                    if (until > 0) {
                        val anotherEvent = factory.normalEvents[Random.nextInt(until)] as AbstractEvent
                        normalQueue.add(anotherEvent.eventInternalName)
                    }
                } else {
                    normalQueue.add(event.eventInternalName)
                }
            }
        }

        // 只有当队列被实际修改时才更新数据库
        if (epicQueue.size >= 25 || normalQueue.size >= 55) {
            val eventQueue = EventQueue().apply {
                this.normalEvents.addAll(normalQueue)
                this.epicEvents.addAll(epicQueue)
            }

            Bukkit.getScheduler().runTaskAsynchronously(ThePit.getInstance()) {
                ThePit.getInstance().mongoDB.eventQueueCollection.replaceOne(
                    Filters.eq("id", "1"),
                    eventQueue,
                    ReplaceOptions().upsert(true)
                )
            }
        }
    }

    fun loadFromDatabase() {
        this.epicQueue.clear()
        this.normalQueue.clear()
        val queue: EventQueue;
        try {
            queue = ThePit.getInstance().mongoDB.eventQueueCollection.findOne()
            if (queue == null) {
                refreshEvents()
                return
            }
        } catch (e: Exception){
            refreshEvents()
            return
        }

        normalQueue += queue.normalEvents
        epicQueue += queue.epicEvents

        try {
            if (this.epicQueue.size < 45 || this.normalQueue.size < 90) {
                this.refreshEvents()
            }
        } catch (e: Exception) {

        }
    }

    fun nextEvent(major: Boolean): String {
        if (epicQueue.isEmpty() || normalQueue.isEmpty()) {
            this.refreshEvents()
        }
        if (epicQueue.isEmpty() && normalQueue.isEmpty()) {
            return "NULL"
        }
        if (major) return epicQueue.poll() else return normalQueue.poll()
    }
}