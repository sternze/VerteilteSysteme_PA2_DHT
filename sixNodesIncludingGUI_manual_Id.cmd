taskkill.exe /F /IM java.exe /T

call ant clean
call ant build


set countOfNodes=3

set MyIp=143.205.199.224

rem for /f "usebackq tokens=13 delims=: " %%i in ('ipconfig ^| find "IPv4 Address" ') do set MyIp=%%i
set GuiIp=%MyIp%
set ConnectingNodeIp=%MyIp%

rem the command 'rem' is just for commenting lines out
rem set GuiIp=143.205.192.146
rem set ConnectingNodeIp=143.205.192.146

rem start cmd /C "ant runGui -DMyIp=%MyIp%"

timeout 1
rem start cmd /C "ant runNode -DMyIp=%MyIp% -DmanualID=0"

FOR %%G IN (1,3) DO (
	timeout 1
	start cmd /C "ant runNode -DServiceName=PA2_MyKV -DNodeIP:Port=%ConnectingNodeIp%:8000 -DGraphViewIP:Port=%GuiIp%:7998 -DMyIp=%MyIp% -DmanualID=%%G"
)