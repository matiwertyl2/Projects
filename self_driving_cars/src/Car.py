import numpy as np
import matplotlib.pyplot as plt
import random
from matplotlib.patches import Ellipse
import time
import timeit
import matplotlib.animation as animation

from Track import Track
from Geometry import Polyline, Line, Point
from Brain import Brain
import Geometry as Geo
import Const as const

class Car:

    width = const.car_width / 2
    height = const.car_length / 2
    dt = const.frame_dt
    sensor_length = const.car_sensor_length

    def __init__(self, pos, alpha, speed, brain):
        self.pos = pos
        self.alpha = alpha
        self.speed = speed
        self.brain = brain
        self.crashed = False
        self.finished = False

    def get_dx(self):
        return self.speed * self.dt * np.cos(self.alpha)

    def get_dy(self):
        return self.speed * self.dt * np.sin(self.alpha)

    def get_polyline(self, fat=1):
        dxh = self.height * fat * np.cos(self.alpha)
        dyh = self.height * fat * np.sin(self.alpha)
        dxw = self.width * fat  * np.cos(self.alpha - np.pi/2)
        dyw = self.width * fat * np.sin(self.alpha - np.pi/2)
        p1 = self.pos.translation(dxh, dyh).translation(dxw, dyw)
        p2 = self.pos.translation(dxh, dyh).translation(-dxw, -dyw)
        p3 = self.pos.translation(-dxh, -dyh).translation(dxw, dyw)
        p4 = self.pos.translation(-dxh, -dyh).translation(-dxw, -dyw)
        return Polyline([ Line(p1, p2), Line(p2, p4), Line(p3, p4), Line(p3, p1) ])


    def get_sensors(self, track):
        sensors = []
        angles = [np.pi/2 , np.pi/4 , 0. , -np.pi/4 , -np.pi/2]
        dx0 = self.height * np.cos(self.alpha)
        dy0 = self.height * np.sin(self.alpha)
        p0 = self.pos.translation(dx0, dy0)
        for angle in angles:
            dx = self.sensor_length * np.cos(self.alpha)
            dy = self.sensor_length * np.sin(self.alpha)
            p1 = p0.translation(dx, dy).rotation(angle, p0)
            sensors.append(Line(p0, p1))
        for sensor in sensors:
            sensor.adjust_to_track(track)
        return sensors

    def fitness_value(self, track, curr_frame, frames_limit, fat=1):
        poly = self.get_polyline(fat=fat)
        pl = Geo.find_intersections(poly, track.left)
        pr = Geo.find_intersections(poly, track.right)
        coeff = 1.
        if self.crashed:
            coeff = 0.8
        if self.finished:
            coeff = 1. + float(frames_limit - curr_frame) / frames_limit * 0.5
        if pl:
            return coeff * track.left.point_distance(pl) / track.left.length()
        elif pr:
            return coeff * track.right.point_distance(pr) / track.right.length()
        else:
            if Geo.find_intersections(poly, track.start):
                return 0
            return coeff

    def is_finished(self, track):
        if self.finished:
            return True
        if Geo.find_intersections(self.get_polyline(), track.finish):
            self.finished = True
            return True
        return False

    def is_crashed(self, track):
        if self.crashed:
            return True
        if Geo.find_intersections(self.get_polyline(), track.right) or Geo.find_intersections(self.get_polyline(), track.left) or Geo.find_intersections(self.get_polyline(), track.start):
            self.crashed = True
            return True
        return False

    def set_position(self, pos, alpha):
        self.pos = pos
        self.alpha = alpha

    def restart(self):
        self.finished = False
        self.crashed = False

    def get_inputs(self, track):
        sensors = self.get_sensors(track)
        inputs = np.zeros(const.car_sensor_count+1)
        for i in range(const.car_sensor_count):
            inputs[i] = sensors[i].length()
        inputs[const.car_sensor_count] = self.speed
        return inputs

    def move(self, track):
        if self.is_crashed(track) == False and self.is_finished(track) == False:
            inputs = self.get_inputs(track)
            decision = self.brain.make_decision(inputs)
            self.alpha += decision[1]
            self.speed += decision[0]
            if (self.speed < 0):
                 self.speed = 0
            self.speed=min(self.speed, const.car_max_speed)
            self.pos = self.pos.translation(self.get_dx(), self.get_dy())

    ### simulates driving, returns fitness_value and arrays
    ### of positions and angles of car
    def drive(self, track, frames_limit):
        positions = []
        angles = []
        fitness = 0.
        for t in range(frames_limit):
            self.move(track)
            positions.append(self.pos)
            angles.append(self.alpha)
            if self.is_crashed(track) == True or self.is_finished(track) == True:
                fitness = max (fitness, self.fitness_value(track, t, frames_limit))
        if fitness == 0:
            fitness = self.fitness_value(track, frames_limit, frames_limit, fat=3.0)
        return fitness, positions, angles

    def evaluate(self, track, frames_limit):
        fitness = 0.
        max_speed = 0.
        for t in range(frames_limit):
            self.move(track)
            if self.is_crashed(track) == True or self.is_finished(track) == True:
                fitness = max (fitness, self.fitness_value(track, t, frames_limit))
            max_speed= max(self.speed, max_speed)
        if fitness == 0:
            fitness = self.fitness_value(track, frames_limit, frames_limit, fat=3.0)
        #print "SPEED ", max_speed
        return fitness
