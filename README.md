# DBot - A Custom Discord Bot
DBot is a customized Discord bot that I have created for my friends and I to utilize in our discord servers. It contains several features that we thought would be convenient and useful to have. Whether it be automating tasks, or just pulling certain information from the web, this bot has made our lives just a little bit easier. The main focus of the bot is to interact with the Steam Web API, pulling information from it and displaying it in the discord chat. The bot was created using the JDA (Java Discord API).

## Currently Supported Features

### Basic Commands

/help - Display a list of all commands  
/ping - Get the ping of the bot in ms   
/count - Get the total number of members in the server  
/joindate - Get the date (month and day) that a user joined the server

### Steam Commands
/displayname [steamid64] - Get a users Steam display name.
/numgames [steamid64] - Get a steam users total number of games   
/mostplayed [steamid64] - Get a steam users most played game  
/usergamestats [steamid64] [Game] - Get a users stats on a specified game   
/sharedgames [steamid64] [steamid64] [steamid64] [steamid64] [steamid64] - Find all the shared games of the given steam users. A maximum of five users is supported.

## Credits

JDA (Java Discord API) - https://github.com/DV8FromTheWorld/JDA   
Retrofit - https://square.github.io/retrofit/   
Steam Web API - https://steamcommunity.com/dev  


