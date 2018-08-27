import numpy as np
import matplotlib as matplotlib
#matplotlib.use('agg')
import matplotlib.pyplot as plt
import random
from matplotlib.patches import Ellipse
import matplotlib.animation as animation
import multiprocessing
import sys

from Geometry import Polyline, Line, Point
from Car import Car
from Track import Track
from Brain import Brain
from DagNetwork import DagNetwork
import Simulation as sim
import Const as const
import DagNetwork as dag

path_root = const.dag_evolution_path_root


def initial_car(network):
    initial_position = Point(const.car_initial_x, const.car_initial_y)
    initial_angle = const.car_initial_angle
    initial_speed = const.car_initial_speed
    return Car(initial_position, initial_angle, initial_speed, Brain(network))

def create_cars(networks):
    cars = []
    for network in networks:
        cars.append(initial_car(network))
    return cars

def objective_function(brain, tracks, frames_limit):
    results = np.zeros(len(tracks))
    for i in range(len(tracks)):
        car = initial_car(brain)
        results[i] = car.evaluate(tracks[i], frames_limit)
    print results, results.mean()
    return results.mean()


def evaluate_population(population, tracks, frames_limit):
    objective_values = np.zeros(len(population))
    for i in range(len(population)):
        objective_values[i] = objective_function(population[i], tracks, frames_limit)
    return objective_values

def initial_population(population_size, n):
    population = []
    for i in range(population_size):
        population.append(dag.random_DagNetwork(n))
    return population

def parent_selection(objective_values, population_size, number_of_offsprings):
    fitness_values = objective_values - objective_values.min()
    if fitness_values.sum() > 0:
        fitness_values = fitness_values / fitness_values.sum()
    else:
        fitness_values = np.ones(population_size) / population_size
    parent_indices = np.random.choice(population_size, number_of_offsprings, True, fitness_values).astype(np.int64)
    for i in range(number_of_offsprings/2):
        if parent_indices[2*i] == parent_indices[2*i+1]:
            parent_indices[2*i+1] = np.random.choice(population_size)
    return parent_indices



### n is the upper bound for number of vertices in network
### other variables speak for themselves
def evolve_dag(population_size=10, number_of_offsprings=10, n=22, edge_mutation_probability=0.2,
        weight_mutation_probability=1., iters=10, tracks=[], frames_limit=25):

    population = initial_population(population_size, n)
    objective_values = evaluate_population(population, tracks, frames_limit)


    matrix = population[0].edges
    results = np.zeros(iters)
    last_saved_value = 0.


    for t in range(iters):
        children = []
        parent_indices = parent_selection(objective_values, population_size, number_of_offsprings)
        ### crossover
        for i in range(len(parent_indices)/2):
            c1, c2 = dag.crossover(population[parent_indices[2*i]], population[parent_indices[2*i+1]], -const.network_weights_limit, const.network_weights_limit)
            children.append(c1)
            children.append(c2)

        ### mutation
        for child in children:
            if np.random.random() < edge_mutation_probability:
                child.edge_mutation()
            if np.random.random() < weight_mutation_probability:
                child.weight_mutation(-const.network_weights_limit, const.network_weights_limit)

        ### children evaluation and replacement
        children_objective_values = evaluate_population(children, tracks, frames_limit)

        objective_values = np.hstack((objective_values, children_objective_values))
        tmp_population = population + children
        I = np.argsort(objective_values)[::-1]
        population = []
        for i in range(population_size):
            population.append(tmp_population[I[i]])
        objective_values = objective_values[I[:population_size]]


        ### statistics
        if (population[0].edges == matrix ).sum() != population[0].n ** 2 or t == iters-1:
            matrix = population[0].edges
            population[0].draw(save=True, nr=t)
        results[t] = objective_values.max()

        print t, results[t]

        if objective_values.max() - last_saved_value > 0.07 or t == iters-1:
            last_saved_value = objective_values.max()
            dag.save_population(population)
            np.savetxt(path_root + 'results.txt', results)
            for i in range(len(tracks)):
                title = path_root + "track_" + str(i+1) + "_gen_" + str(t)
                sim.simulation_movie(tracks[i], create_cars(population[:5]), title=title, frames_limit=frames_limit, show_demo=False, save=True)

    return results, population


#############################################################################################



def create_tracks(lengths, types=['random', 'random', 'random', 'random'], curvatures=[0.5, 0.5, 0.5, 0.5],
                circ_dirs=['left', 'left', 'left', 'left']):
    tracks = []
    width= const.learn_track_width
    curvature = 0.5
    for i in range(len(lengths)):
        track = Track(lengths[i], width=width, curvature=curvatures[i], path_type=types[i], circ_dir=circ_dirs[i])
        plt.axis('equal')
        track.draw()
        plt.savefig(path_root + 'track' + str(i+1))
        plt.clf()
        tracks.append(track)
    return tracks

lengths = [50, 50, 50, 50]
tracks = None

while True:
    tracks = create_tracks(lengths)
    acc = raw_input("Do you accept tracks?\n")
    if acc == "yes":
        break
    elif acc == "exit":
        sys.exit()

results, population = evolve_dag(tracks=tracks, population_size=100, number_of_offsprings=100, iters=101, frames_limit=110)

dag.save_population(population)
np.savetxt(path_root + "results.txt", results)
