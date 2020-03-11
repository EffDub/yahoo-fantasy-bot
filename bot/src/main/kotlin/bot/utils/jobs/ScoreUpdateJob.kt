package bot.utils.jobs

import bot.bridges.MessageBridge
import bot.bridges.ScoreUpdateBridge
import bot.messaging_services.Message
import bot.utils.DataRetriever
import org.quartz.Job
import org.quartz.JobExecutionContext

class ScoreUpdateJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Score Update Job...")

        val header = Message.Generic("📣 <b>SCORE ALERT</b> 💯\\n━━━━━━━━━━")
        MessageBridge.dataObserver.onNext(header)

        val data = DataRetriever.getTeamsData()
        ScoreUpdateBridge.dataObserver.onNext(data)
    }
}
