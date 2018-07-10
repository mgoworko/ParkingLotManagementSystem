#!/bin/bash
nohup java -jar target/parkingLotSystem-1.0.0.jar > target/log.txt 2>&1 &
echo $! > target/pid.file
