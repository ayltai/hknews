@echo off

set CWD=%~p0
set CWD=%CWD:~0,-1%

for %p in ("%CWD%") do (set DIRNAME=%%~nxp)

docker build --tag "%DIRNAME%:latest" .

docker run ^
  --privileged ^
  -v "%cd%":/"%DIRNAME%" ^
  -v /var/run/docker.sock:/var/run/docker.sock ^
  -v /sys/fs/cgroup:/sys/fs/cgroup:ro ^
  -it --rm ^
  "%DIRNAME%:latest"
