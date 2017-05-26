#!/usr/bin/python3
import time
import datetime
from sense_hat import SenseHat


sense = SenseHat()
temperature = sense.get_temperature()
print(temperature)
