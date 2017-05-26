import pygame
import os
from sense_hat import SenseHat



sense = SenseHat()

power_used = 0

for y in range(8):
    if(sense.get_pixel(1,y) == [0,0,0]):
        power_used = power_used + 1

print(power_used)
