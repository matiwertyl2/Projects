import numpy as np
import matplotlib.pyplot as plt
import random
from matplotlib.patches import Ellipse
import time
import timeit
import matplotlib.animation as animation

from Geometry import Polyline, Line, Point
from Car import Car
from Track import Track
from Brain import Brain
from Network import Network
import Simulation as sim
import Const as const

def create_car(gene, layers_shape):
    layers = []
    beg = 0
    end = 0
    for layer_shape in layers_shape:
        beg = end
        end += layer_shape[0]*layer_shape[1]
        layers.append( gene[beg:end].reshape(layer_shape) )

    initial_position = Point(const.car_initial_x, const.car_initial_y)
    initial_angle = const.car_initial_angle
    initial_speed = const.car_initial_speed
    brain = Brain(Network(layers))
    return Car(initial_position, initial_angle, initial_speed, brain)

def create_cars(genes, layers_shape):
    cars = []
    for gene in genes:
        cars.append(create_car(gene, layers_shape))
    return cars

def objective_function(gene, tracks, layers_shape, frames_limit):
    results = np.zeros(len(tracks))
    for i in range(len(tracks)):
        car = create_car(gene, layers_shape)
        results[i] = car.evaluate(tracks[i], frames_limit)
    print results, results.mean()
    return results.mean()

def objective_function_thread(args):
    return objective_function(args[0], args[1], args[2], args[3])
