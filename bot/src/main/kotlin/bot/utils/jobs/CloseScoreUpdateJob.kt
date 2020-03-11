package bot.utils.jobs

import bot.bridges.CloseScoreUpdateBridge
import bot.bridges.MessageBridge
import bot.messaging_services.Message
import bot.utils.DataRetriever
import org.quartz.Job
import org.quartz.JobExecutionContext

class CloseScoreUpdateJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Running Close Score Update Job...")

        val header = Message.Generic("📣 <b>CLOSE SCORE ALERT</b> 🤞\\n━━━━━━━━━━━━━")
        MessageBridge.dataObserver.onNext(header)

        val data = DataRetriever.getTeamsData()
        CloseScoreUpdateBridge.dataObserver.onNext(data)
    }
}
