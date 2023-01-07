# GhuPerms

This mod allow you to deny or allow commands from minecraft vanilla

For example:
    If you want a specific player can only use the /seed command,
    add this permission <<minecraft.commands.seed>> at TRUE to that player

There is a config option called "opPlayerCanUseAllCommands". If it's true, op player don't need permission to use command

This project works with a mariadb server

    a short tutorial to install a mariadb server on windows
        1. DL this file "https://dlm.mariadb.com/2690831/MariaDB/mariadb-10.10.2/winx64-packages/mariadb-10.10.2-winx64.zip" and extract it
        2. Open a terminal and go to the folder where you extracted the files
        3. create a folder called "data"
        4. go the the bin folder now
        5. type this: .\mariadb-install-db.exe --datadir='..\data\' --password='Pa$$w0rd' --port='3307'
        6. start the server with this: .\mariadbd.exe --console --datadir='..\data\' --port='3307'

