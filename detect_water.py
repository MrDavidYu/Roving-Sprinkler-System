import pygame
import os
from sense_hat import SenseHat



sense = SenseHat()

water_used = 0

for y in range(8):
    if(sense.get_pixel(0,y) == [0,0,0]):
        water_used = water_used + 1

print(water_used)
