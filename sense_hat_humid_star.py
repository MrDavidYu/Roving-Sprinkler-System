#!/usr/bin/python3
import time
import datetime
from sense_hat import SenseHat


sense = SenseHat()
humidity = sense.get_humidity()
print(humidity)
