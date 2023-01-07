
import ch.skyfy.ghuperms.api.config.Player
import ch.skyfy.ghuperms.api.db.DatabaseManager
import ch.skyfy.ghuperms.utils.hasPermission2
import kotlin.test.Test

class Tests {

    @Test
    fun tests(){
        DatabaseManager

        println()
//        println(hasPermission2(Player("ebb5c153-3f6f-4fb6-9062-20ac564e7490", "Skyfy16"), "ghuperms.commands.testcmd"))
        println(hasPermission2(Player("ebb5c153-3f6f-4fb6-9062-20ac564e7490", "Skyfy16"), "ghuperms.commands.testcmd"))
    }

}