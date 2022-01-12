from gpiozero import DigitalOutputDevice
from time import sleep
import sys


class Motor:
    def __init__(self, DIR_PIN, PUL_PIN, ENA_PIN):
        self.__DIR_PIN = DigitalOutputDevice(DIR_PIN, initial_value=True, active_high=False)
        self.__PUL_PIN = DigitalOutputDevice(PUL_PIN, initial_value=True, active_high=False)
        self.__ENA_PIN = DigitalOutputDevice(ENA_PIN, initial_value=True, active_high=False)
        self.__PUL_T = 100
        self.disable()

    def setSpeed(self, speed):
        self.__PUL_T = 200 - speed

    def rotate(self, steps, dir):
        if dir:
            self.__DIR_PIN.on()
        else:
            self.__DIR_PIN.off()

        for step in range(0, steps):
            self.__PUL_PIN.on()
            sleep(self.__PUL_T / 1000000)
            self.__PUL_PIN.off()
            sleep(self.__PUL_T / 1000000)

    def enable(self):
        self.__ENA_PIN.on()

    def disable(self):
        self.__ENA_PIN.off()


if __name__ == '__main__':
    motorX = Motor(23, 24, 25)
    motorZ = Motor(26, 27, 22)

    if sys.argv[1] == 'execute':
        motor = None
        if sys.argv[2] == 'X':
            motor = motorX
        elif sys.argv[2] == 'Z':
            motor = motorZ

        motor.rotate(int(sys.argv[3]), int(sys.argv[4]))
