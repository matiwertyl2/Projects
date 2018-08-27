import numpy as np
import matplotlib.pyplot as plt
import random
from matplotlib.patches import Ellipse
import time
import timeit
import matplotlib.animation as animation

import Geometry as Geo
from Geometry import Polyline, Line, Point
import Const as const

def random_path(n, sigma, l):
    lines = [Line(Point(0., 0.), Point(0.5, l))]
    a = np.pi/2
    xprv = 0.5
    yprv = l
    for i in range(n):
        turn = np.random.normal(0, sigma)
        if turn < 0:
            turn = max(turn, -sigma)
        else:
            turn = min(turn, sigma)
        a += turn
        l = np.random.random() * l + 2
        x = xprv + l*np.cos(a)
        y = yprv + l*np.sin(a)
        lines.append( Line(Point(xprv, yprv), Point(x, y) ) )
        xprv = x
        yprv = y
    return Polyline(lines)

def circular(n, circ_dir):
    lines = [Line(Point(0., 0.), Point(0., 10.))]
    a = np.pi/2
    xprv = 0.
    yprv = 10.0
    sgn = -1 ### turn right
    if circ_dir == 'left':
        sgn = 1
    for i in range(n):
        a += 0.2 * sgn
        l = 2*np.sqrt(i+10);
        x = xprv + l*np.cos(a)
        y = yprv + l*np.sin(a)
        lines.append( Line(Point(xprv, yprv), Point(x, y) ) )
        xprv = x
        yprv = y
    return Polyline(lines)

def horseshoe(n, circ_dir):
    lines = [Line(Point(0., 0.), Point(1., 10.*n))]
    a = np.pi/2
    xprv = 1.
    yprv = 10.*n
    sgn = -1
    if circ_dir == 'left':
        sgn = 1
    for i in range(9):
        a += np.pi / 10 * sgn
        x = xprv + 10*np.cos(a)
        y = yprv + 10*np.sin(a)
        lines.append( Line(Point(xprv, yprv), Point(x, y) ) )
        xprv = x
        yprv = y
    x = xprv + 10*n*np.cos(a)
    y = yprv + 10*n*np.sin(a)
    lines.append(Line(Point(xprv, yprv), Point(x, y)))
    return Polyline(lines)


def serpent(segments, sharpness):
    lines = [Line(Point(0., 0.), Point(0., 10.))]
    a = np.pi/2
    xprv = 0.
    yprv = 10.
    sgn =  -1
    for s in range(segments):
        for i in range(sharpness):
            a += (np.pi / sharpness) *  sgn
            x = xprv + 10.*np.cos(a)
            y = yprv + 10.*np.sin(a)
            lines.append(Line(Point(xprv, yprv), Point(x, y)))
            xprv = x
            yprv = y
        sgn *= -1
    return Polyline(lines)

def straight(n):
    lines = [Line(Point(0., 0.), Point(0.1, 10.*n))]
    return Polyline(lines)


def generate_path(n, width, curvature, path_type='random', circ_dir="right", segments=5, sharpness=10):
    while True:
        p = None
        if path_type == 'random':
            p = random_path(n, curvature, 10)
        elif path_type == 'circular':
            p = circular(n, circ_dir)
        elif path_type == 'serpent':
            p = serpent(segments, sharpness)
        elif path_type == 'horseshoe':
            p = horseshoe(n, circ_dir)
        elif path_type == "straight":
            p = straight(n)
        pt = p.translation(width)
        if p.find_intersections() or Geo.find_intersections(p, pt):
            continue
        else:
            pt.remove_intersections()
            return p, pt

class Track:

    def __init__(self, n, width=const.learn_track_width, curvature=0.5,
                path_type='random', circ_dir='right', segments=5, sharpness=10):

        self.left, self.right = generate_path(n, width, curvature, path_type=path_type,
                                circ_dir=circ_dir, segments=segments, sharpness=sharpness)
        self.finish = Polyline( [ Line (self.left.lines[-1].b, self.right.lines[-1].b)])
        self.start = Polyline( [Line(self.left.lines[0].a, self.right.lines[0].a)])

    def draw(self):
        x, y = self.right.plot_data()
        plt.plot(x, y, color="black")
        xt, yt = self.left.plot_data()
        plt.plot(xt, yt, color="black")

### return tuples of tracks and frame limits
def create_benchmarks():
    width = const.learn_track_width * 1.5
    benchmarks = []
    t = Track(50, path_type='circular', circ_dir="left", width=width)
    frames_limit = 250
    benchmarks.append((t, frames_limit))
    t = Track(50, path_type='circular', circ_dir="right", width=width)
    frames_limit = 250
    benchmarks.append((t, frames_limit))
    t = Track(0, path_type='serpent', sharpness=15, width=width)
    frames_limit= 300
    benchmarks.append((t, frames_limit))
    t = Track(0, path_type='serpent', sharpness=8, width=width)
    frames_limit = 200
    benchmarks.append((t, frames_limit))
    t = Track(10, path_type='horseshoe', circ_dir="left", width=width)
    frames_limit = 150
    benchmarks.append((t, frames_limit))
    t = Track(10, path_type='horseshoe', circ_dir="right", width=width)
    frames_limit=150
    benchmarks.append((t, frames_limit))
    t = Track(30, path_type='straight', width=width)
    frames_limit = 100
    benchmarks.append((t, frames_limit))
    return benchmarks
