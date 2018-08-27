import numpy as np
import matplotlib.pyplot as plt
import random

from Car import Car
from Track import Track
from Brain import Brain
from Network import Network
import Network as net
import DagNetwork as dag
import Simulation as sim
from Track import create_benchmarks
from Geometry import Point, Line, Polyline
import Const as const
import Movies as movies

def initial_car(network):
    initial_position = Point(const.car_initial_x, const.car_initial_y)
    initial_angle = const.car_initial_angle
    initial_speed = const.car_initial_speed
    return Car(initial_position, initial_angle, initial_speed, Brain(network))

network = None
path_root = ""
layers_shape = const.network_layers_shape


choice = raw_input("Select Evolution type you want to assess (normal / dag)\n")
if choice == "normal":
    path_root = const.evolution_path_root
    gene = np.loadtxt(path_root + 'population.txt')[0]
    network = net.create_network(gene, layers_shape)
    network.draw(save=True)
else:
    path_root = const.dag_evolution_path_root
    network = dag.load_population(path_root+'population.txt')[0]
    network.draw()
    movies.dag_brain_movie(path_root)

################################################################################

movies.learning_movie(path_root)

tracks = create_benchmarks()
for i in range(len(tracks)):
    track = tracks[i]
    cars = [initial_car(network)]
    title = path_root + "benchmark_" + str(i)
    sim.simulation_movie(track[0], cars, title=title, frames_limit=track[1], show_demo=False, save=True)

movies.benchmark_movie(path_root)
