import time
from sense_hat import SenseHat

sense = SenseHat()
sense.set_pixel(7,6,[0,255,0])
time.sleep(1)
sense.set_pixel(7,6,[0,0,0])
