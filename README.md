# JourneyMapQOL_1.7.10
A share waypoint button and the formatted text feature is missing in the 1.7.10 version of journeymap. 

This simple mod adds those features and some other quality of life improvements. 
## Requires Journey Map 1.7.10
https://www.curseforge.com/minecraft/mc-mods/journeymap
## Features
New commands: cleardeath, share, remove

If you type or receive correctly formatted text, it is converted into a waypoint that is automatically "clicked" or added to your waypoints list.

You can disable the auto click feature with the command and manually click on waypoints in chat.

The ApiWaypointManager class has some functions that handle the packets for sharing waypoints.
## Commands
`/waypoint cleardeath [number of recent death points to save]`

`/waypoint remove <waypoint name>`

`/waypoint disableautoclick (true/false)`

`/waypoint share <waypoint name> [player name]`
## Formatted Text
A waypoint in chat must be enclosed by square brackets. Parameters are split by "," and defining a parameter uses ":"
### Parameters: `name` (type | description | default)
`x` (integer | x coordinate | required)

`y` (integer | y coordinate | 60)

`z` (integer | z coordinate | required)

`dim` (integer | dimension number | 0)

`name` (string | waypoint name | x and z coordinate)

`color` (hexadecimal integer | waypoint color | #FFFF00)

`delete` (boolean | delete all other waypoints with same name | false)
### Example
`[x:105,y:72,z:-723,dim:0,name:Test,color:#B311CF,delete:false]`
## My Other Mods That Use This
https://www.curseforge.com/minecraft/mc-mods/journeymap-waypoint-radar
