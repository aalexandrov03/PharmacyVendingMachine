import RPi.GPIO as GPIO
from time import sleep
import sys


class Motor:
    def __init__(self, DIR_PIN, PUL_PIN, ENA_PIN):
        GPIO.setmode(GPIO.BCM)

        self.__DIR_PIN = DIR_PIN
        self.__PUL_PIN = PUL_PIN
        self.__ENA_PIN = ENA_PIN
        self.__PUL_T = 100

        GPIO.setup(DIR_PIN, GPIO.OUT)
        GPIO.setup(PUL_PIN, GPIO.OUT)
        GPIO.setup(ENA_PIN, GPIO.OUT)

        GPIO.output(DIR_PIN, False)
        GPIO.output(PUL_PIN, False)
        GPIO.output(ENA_PIN, False)

        self.enable()

    def rotate(self, steps, dir):
        if dir == "LEFT" or dir == "DOWN":
            GPIO.output(self.__DIR_PIN, True)
        else:
            GPIO.output(self.__DIR_PIN, False)

        for step in range(0, steps):
            GPIO.output(self.__PUL_PIN, False)
            sleep(self.__PUL_T / 1000000)
            GPIO.output(self.__PUL_PIN, True)
            sleep(self.__PUL_T / 1000000)

    def enable(self):
        GPIO.output(self.__ENA_PIN, True)


    def disable(self):
        GPIO.output(self.__ENA_PIN, False)


def fetch_route(args):
    route = []

    offset = 1
    for i in range(0, len(args)/3):
        route.append([args[offset], int(args[offset + 1]), args[offset + 2]])
        offset = offset + 3

    return route

if __name__ == '__main__':
    motorX = Motor(23, 24, 25)
    motorZ = Motor(26, 27, 22)

    route = fetch_route(sys.argv)

    for i in range(0, len(route)):
        motor = motorX
        if route[i][0] == 'Z':
            motor = motorZ

        motor.rotate(route[i][1], route[i][2])

    GPIO.cleanup()