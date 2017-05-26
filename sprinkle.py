import os
import time
from sense_hat import SenseHat

sense = SenseHat()

sense.set_pixel(5, 7, 0, 0, 0)
sense.set_pixel(4, 7, 0, 0, 0)
sense.set_pixel(3, 7, 0, 0, 0)

sense.set_pixel(5, 6, 0, 255, 0)
sense.set_pixel(4, 6, 0, 255, 0)
sense.set_pixel(3, 6, 0, 255, 0)

time.sleep(3)

sense.set_pixel(5, 6, 0, 0, 0)
sense.set_pixel(4, 6, 0, 0, 0)
sense.set_pixel(3, 6, 0, 0, 0)
