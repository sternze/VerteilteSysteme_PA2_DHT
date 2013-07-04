taskkill.exe /F /IM java.exe /T

call ant clean
call ant build


set countOfNodes=10

set MyIp=192.168.43.213

rem for /f "usebackq tokens=13 delims=: " %%i in ('ipconfig ^| find "IPv4 Address" ') do set MyIp=%%i
set GuiIp=%MyIp%
set ConnectingNodeIp=%MyIp%

rem the command 'rem' is just for commenting lines out
rem set GuiIp=143.205.192.146
rem set ConnectingNodeIp=143.205.192.146

start cmd /C "ant runGui -DMyIp=%MyIp%"

timeout 1
start cmd /C "ant runNode -DMyIp=%MyIp%"


:loop

timeout 1
start cmd /C "ant runNode -DServiceName=PA2_MyKV -DNodeIP:Port=%ConnectingNodeIp%:8000 -DGraphViewIP:Port=%GuiIp%:7998 -DMyIp=%MyIp%"

set /a countOfNodes-=1
if %countOfNodes% GTR 1 goto loop