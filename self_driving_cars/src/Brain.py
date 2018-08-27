import numpy as np
import Const as const

from DagNetwork import DagNetwork
from Network import Network

class Brain:

    ### network can be either Network or DagNetwork
    def __init__(self, network):
        self.network = network

    def scale_speed(self, speed):
        return speed * const.car_max_acc

    def scale_turn(self, alpha):
        return alpha * const.car_max_turn

    def scale_inputs(self, inputs):
        inputs[:const.car_sensor_count] /= const.car_sensor_length
        inputs[const.car_sensor_count] /= const.car_max_speed
        return inputs

    ### inputs - some number of sensors and speed
    ### output - dV dAlpha
    def make_decision(self, inputs):
        inputs = self.scale_inputs(inputs)
        decision = self.network.feed_forward(inputs)
        decision[0] = self.scale_speed(decision[0])
        decision[1] = self.scale_turn(decision[1])
        return decision
