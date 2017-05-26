#!/usr/bin/python3
import time
import sys
import datetime
import requests

# Channel link: https://thingspeak.com/channels/273958/private_show

write_api_key = "KQJBWUD9G7Y9Q5OE"

avg_temp = float(sys.argv[1])
avg_humid = float(sys.argv[2])
power = int(sys.argv[3])
water = int(sys.argv[4])
print("Sending temperature:",avg_temp,"humidity:",avg_humid,"power usage:",power,"and water usage:",water,"to ThingSpeak Cloud!")


def log_data_to_thing_speak(temperature, humidity, power, water):
    data = {"api_key":write_api_key, "field1":temperature, "field2":humidity, "field3":power, "field4":water}
    req = requests.post("https://api.thingspeak.com/update", data=data)
    return req.text
    
def log_data_to_file(ret_val, temperature, humidity):
    # Open (or create if not exists) the file name "sense_hat_thingspeak_http.txt"
    # in "append" mode
    data_log = open("sense_hat_thingspeak_http.txt", "a")
    # Write the line to the given file
    d = datetime.datetime.now()
    if ret_val == "0":
        ret_val = "FAILED"
    data_log.write("[%s]" % d)
    data_log.write(" ID: %s" % ret_val)
    data_log.write(" / T: %.2f" % temperature)
    data_log.write(" / H: %.2f" % humidity)
    # Close the file after writing
    data_log.close()

ret_val = log_data_to_thing_speak(avg_temp, avg_humid, power, water)
#   log_data_to_file(ret_val, temperature, humidity)
