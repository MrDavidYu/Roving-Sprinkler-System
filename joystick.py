import pygame
import os
from pygame.locals import *
from sense_hat import SenseHat

pygame.init()
pygame.display.set_mode((640, 480))

sense = SenseHat()
sense.clear()

running = True

water_list = [0,1,2,3,4,5,6,7]
power_list = [0,1,2,3,4,5,6,7]

def disp_empty():
    sense.set_pixel(5, 5, 255, 255, 0)
    sense.set_pixel(5, 4, 255, 255, 0)
    sense.set_pixel(5, 3, 255, 255, 0)
    sense.set_pixel(5, 2, 255, 255, 0)
    sense.set_pixel(5, 1, 255, 255, 0)
    sense.set_pixel(4, 5, 255, 255, 0)
    sense.set_pixel(4, 3, 255, 255, 0)
    sense.set_pixel(4, 1, 255, 255, 0)
    sense.set_pixel(3, 5, 255, 255, 0)
    sense.set_pixel(3, 1, 255, 255, 0)

def disp_full():
    sense.set_pixel(5, 5, 0, 0, 0)
    sense.set_pixel(5, 4, 0, 0, 0)
    sense.set_pixel(5, 3, 0, 0, 0)
    sense.set_pixel(5, 2, 0, 0, 0)
    sense.set_pixel(5, 1, 0, 0, 0)
    sense.set_pixel(4, 5, 0, 0, 0)
    sense.set_pixel(4, 3, 0, 0, 0)
    sense.set_pixel(4, 1, 0, 0, 0)
    sense.set_pixel(3, 5, 0, 0, 0)
    sense.set_pixel(3, 1, 0, 0, 0)

while running:
    for event in pygame.event.get():
        if event.type == KEYDOWN:
            for y in range(8):
                sense.set_pixel(0, y, 0, 0, 0)
                sense.set_pixel(1, y, 0, 0, 0)
            if event.key == K_DOWN and len(water_list) < 8:
                water_list.append(len(water_list))
            elif event.key == K_UP and len(water_list) > 0:
                water_list.pop()
            elif event.key == K_RIGHT and len(power_list) < 8:
                power_list.append(len(power_list))
            elif event.key == K_LEFT and len(power_list) > 0:
                power_list.pop()
            if len(power_list) == 0 or len(water_list) == 0:
                disp_empty()
                os.system("java EnqueueDevice")
            if len(power_list) == 8 and len(water_list) == 8:
                disp_full()
                os.system("java DequeueDevice")

        for y in water_list:
            sense.set_pixel(0, y, 0, 0, 255)
        for y in power_list:
            sense.set_pixel(1, y, 255, 255, 255)
        if event.type == QUIT:
            running = False
            for y in range(8):
                sense.set_pixel(0, y, 0, 0, 0)
                sense.set_pixel(1, y, 0, 0, 0)
            print("Terminating Joystick Script")
