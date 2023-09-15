rootProject.name = "grandmastery"

include("game")
include("server")
include("client")
include("gui")
include("conversation")
include("local")
include("tui")
include("bot-farm")
include("bots")
include("bots:ljedmitry-bot")
findProject(":bots:ljedmitry-bot")?.name = "ljedmitry-bot"
include("bots:melniknow-bots")
findProject(":bots:melniknow-bots")?.name = "melniknow-bots"
include("bots:yurkevich-bots")
