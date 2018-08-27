import numpy as np
import matplotlib.pyplot as plt
from Geometry import Line, Point, Polyline
import Const as const

class Network:

    ### layers - list of arrays responsible for wieghts
    def __init__(self, layers):
        self.layers = layers

    def feed_forward(self, inputs):
        for layer in self.layers:
            inputs = np.tanh(np.dot(inputs, layer))
        return inputs

    def create_layer_points(self, point_distance, layer_distance, layer_size, layer_depth):
        layer_points = []
        p = Point(layer_depth*layer_distance, float(layer_size-1)/2 * point_distance)
        for j in range(layer_size):
            layer_points.append(p)
            p = p.translation(0, -point_distance)
        return layer_points

    def create_layer_lines(self, points1, points2, weights):
        lines = []
        for i in range(weights.shape[0]):
            for j in range(weights.shape[1]):
                color = "red"
                if weights[i][j] < 0:
                    color = "blue"
                lines.append( ( Line(points1[i], points2[j]), color, weights[i][j]) )
        return lines

    def draw_points(self, points):
        for layer in points:
            for p in layer:
                plt.scatter(p.x, p.y, s=100, color="black", zorder=100)

    def draw_lines(self, lines):
        for layer_lines in lines:
            for line in layer_lines:
                X, Y = line[0].plot_data()
                plt.plot(X, Y, zorder=abs(line[2]), color=line[1], linewidth=line[2])


    def draw(self, save=False, path_root=const.evolution_path_root):

        point_distance = 5
        layer_distance = 10

        plt.axis('equal')
        plt.axis('off')

        points = []
        lines = []

        for i, layer in enumerate(self.layers):
            n = layer.shape[0]
            points.append( self.create_layer_points(point_distance, layer_distance, n, i) )

        points.append(self.create_layer_points(point_distance, layer_distance, self.layers[-1].shape[1], len(self.layers) ) )

        for i in range(1,len(points)):
            lines.append(self.create_layer_lines(points[i-1], points[i], self.layers[i-1]) )

        self.draw_points(points)
        self.draw_lines(lines)
        if save == False:
            plt.show()
        else:
            plt.savefig(path_root+'brain')


def create_network(gene, layers_shape):
    layers = []
    beg = 0
    end = 0
    for layer_shape in layers_shape:
        beg = end
        end += layer_shape[0]*layer_shape[1]
        layers.append( gene[beg:end].reshape(layer_shape) )
    return Network(layers)
