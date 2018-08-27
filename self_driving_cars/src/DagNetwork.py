
import numpy as np
import matplotlib.pyplot as plt

from Geometry import Point, Line
import Const as const

class DagNetwork:

    ### first nodes are input nodes (sensors and speed)
    ### last nodes are output nodes (dV dAlpha)
    ### it forces DagNetwork to be at least const.car_sensor_count + 3 big
    def __init__(self, edges, weights=[], sigma=1.):
        self.edges = edges
        self.weights = weights
        self.n = len(edges)
        self.sigma = sigma
        self.edge_change_probability = 1./self.n

    def outdeg(self, v):
        for u in self.edges[v]:
            if u != 0:
                return True
        return False

    def get_layers(self):
        layers = []
        Q = []
        Qnext = []
        indeg = [0] * self.n
        for i in range(self.n):
            for j in range(self.n):
                if self.edges[i][j] == 1:
                    indeg[j] += 1
        for i in range(self.n):
            if indeg[i] == 0 and self.outdeg(i):
                Q.append(i)
        while len(Q) > 0:
            for v in Q:
                for i in range(self.n):
                    if self.edges[v][i]:
                        indeg[i]-=1
                        if indeg[i] == 0:
                            Qnext.append(i)
            layers.append(Q)
            Q = Qnext
            Qnext = []
        return layers

    def get_node_positions(self):
        layers = self.get_layers()
        layer_distance = 20
        node_distance = 15
        positions = [None] * self.n
        for i in range(len(layers)):
            layer = layers[i]
            y = (-1. + len(layer) ) / 2 * node_distance - i*(0.1*layer_distance ** 1.5)
            x = i * layer_distance
            for node in layer:
                positions[node] = Point(x, y)
                y -= node_distance
        return positions


    def draw(self, save=False, nr=0):
        plt.clf()
        plt.axis('off')
        plt.axis('equal')
        nodes = self.get_node_positions()
        for i in range(len(nodes)):
                node = nodes[i]
                if not node:
                    continue
                color = "black"
                if i <= const.car_sensor_count:
                    color = "purple"
                if i == self.n -1:
                    color = "green"
                if i == self.n - 2:
                    color = "pink"
                plt.scatter(node.x, node.y, s=100, color=color, zorder=100)
        for i in range(self.n):
            for j in range(self.n):
                if self.edges[i][j]:
                    line = Line(nodes[i], nodes[j])
                    X, Y = line.plot_data()
                    color = "red"
                    if self.weights[i][j] < 0:
                        color = "blue"
                    plt.plot(X, Y, zorder=abs(self.weights[i][j]), color=color, linewidth=self.weights[i][j])
        if save:
            plt.savefig(const.dag_evolution_path_root + 'brain' + str(nr))
        else:
            plt.show()

    def feed_forward(self, inputs):
        values = np.zeros(self.n)
        for i in range(len(inputs)):
            values[i] = inputs[i]
        layers = self.get_layers()
        for layer in layers:
            for v in layer:
                if values[v] == 0:
                    s = 0.
                    for u in range(self.n):
                        if self.edges[u][v]:
                            s += values[u] * self.weights[u][v]
                    values[v] = np.tanh(s)
        return values[-2:]

    def edge_mutation(self):
        p  = 1. / (self.n)
        self.edge_change_probability *= np.exp(np.random.normal(0, self.sigma/5))
        for i in range(self.n):
            for j in range(self.n):
                if np.random.random() < self.edge_change_probability and j > i:
                    if j <= const.car_sensor_count or i >= self.n -2:
                        continue
                    self.edges[i][j] = (self.edges[i][j] + 1 ) % 2


    def weight_mutation(self, L, U, mi=20):
        tau=1./self.n
        tau0=1./np.sqrt(2*(self.n))
        self.sigma = self.sigma * np.exp(np.random.normal(0, tau)  + np.random.normal(0, tau0) )
        for i in range(self.n):
            for j in range(self.n):
                if np.random.random() < 0.5:
                    self.weights[i][j] += np.random.normal(0, self.sigma)



def random_beta(n, mi=20):
    u = np.random.random()

    if u < 0.5:
        return np.power(2.*u, 1./(1. + mi))
    else:
        return np.power(0.5/(1.-u), 1./(1.+mi))

def inside_constraints(p, L, U):
    return True
    for i in range(p.shape[0]):
        for j in range(p.shape[1]):
            if p[i][j] < L or p[i][j] > U:
                return False
    return True

### http://wpmedia.wolfram.com/uploads/sites/13/2018/02/09-2-2.pdf
### https://www.slideshare.net/paskorn/simulated-binary-crossover-presentation?type=powerpoint
### crossover of weights
def weight_crossover(p1, p2, L, U, crossover_probability=0.9):
    while True:
        w1 = p1.weights.copy()
        w2 = p2.weights.copy()

        for i in range(p1.n):
            for j in range(p1.n):
                if np.random.random() < 0.5:
                    beta = random_beta(p1.n)
                    w1[i][j] = 0.5 * ( (1.+beta)*p1.weights[i][j] + (1.-beta)*p2.weights[i][j] )
                    w2[i][j] = 0.5 * ( (1.-beta)*p1.weights[i][j] + (1.+beta)*p2.weights[i][j] )

        if inside_constraints(w1, L, U) and inside_constraints(w2, L, U):
            return w1, w2

def edge_crossover(p1, p2, crossover_probability=0.9):
    e1 = np.zeros((p1.n, p1.n))
    e2 = np.zeros((p1.n, p1.n))
    sep = np.random.randint(p1.n)
    e1[:sep] = p1.edges[:sep].copy()
    e1[sep:] = p2.edges[sep:].copy()
    e2[:sep] = p2.edges[:sep].copy()
    e2[sep:] = p1.edges[sep:].copy()
    return e1, e2

def crossover(p1, p2, L, U):
    e1, e2 = edge_crossover(p1, p2)
    w1, w2 = weight_crossover(p1, p2, L, U)
    return DagNetwork(e1, weights=w1, sigma=p1.sigma), DagNetwork(e2, weights=w2, sigma=p2.sigma)


def random_DagNetwork(n):
    edges = np.zeros((n, n)).astype(int)
    for i in range(n):
        for j in range(n):
            if i < j and j > const.car_sensor_count and i < n-2:
                edges[i][j] = np.random.choice([0, 1], p=[0.75, 0.25])
    weights = np.random.randn(n, n)
    return DagNetwork(edges, weights=weights)


def save_population(population, path_root = const.dag_evolution_path_root):
    path = path_root + "population.txt"
    n = population[0].n
    arr = np.zeros((len(population)*n, 2*n))
    for i in range(len(population)):
        arr[ i*n:(i+1)*n, 0:n] = population[i].edges
        arr[ i*n:(i+1)*n, n:2*n] = population[i].weights
    np.savetxt(path, arr)

def load_population(path = const.dag_evolution_path_root + 'population.txt'):
    arr = np.loadtxt(path)
    n = arr.shape[1] / 2
    population = []
    population_size = arr.shape[0] / n
    for i in range(population_size):
        edges = arr[i*n:(i+1)*n, 0:n]
        weights = arr[i*n:(i+1)*n, n:2*n]
        population.append(DagNetwork(edges, weights=weights))
    return population
