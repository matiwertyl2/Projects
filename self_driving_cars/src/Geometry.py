import numpy as np
import matplotlib.pyplot as plt
import random
from matplotlib.patches import Ellipse
import time
import timeit
import matplotlib.animation as animation


class Point:

    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __eq__(self, other):
        return self.x == other.x and self.y == other.y

    def inside(self, line):
        x1, x2, y1, y2 = line.a.x, line.b.x, line.a.y, line.b.y
        x, y = self.x, self.y
        if (x < x1 and x < x2) or (x > x1 and x > x2):
            return False
        if (y < y1 and y < y2) or (y > y1 and y > y2):
            return False
        return True

    def translation(self, dx, dy):
        return Point(self.x+dx, self.y+dy)

    def rotation(self, alpha, p0):
        A = np.array([ [np.cos(alpha), -np.sin(alpha)],
                       [np.sin(alpha),  np.cos(alpha)]
                     ])
        r = np.transpose(np.array([[self.x - p0.x, self.y - p0.y]]))
        r = np.dot(A, r)
        return Point(r[0][0]+p0.x, r[1][0]+p0.y)


def cross_product(p0, p1, p2): ### cross product of p1 and p2 with p0 as 0 0
    return np.sign((p1.x-p0.x)*(p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y))


class Line:

    def __init__(self, a, b):
        self.a = a
        self.b = b

    def __eq__(self, other):
        return self.a == other.a and self.b == other.b

    def __str__(self):
        return "%f %f | %f %f" %(self.a.x, self.a.y, self.b.x, self.b.y)

    def plot_data(self):
        return np.array([self.a.x, self.b.x]), np.array([self.a.y, self.b.y])

    def equation(self):
        a = 0.
        if self.b.x - self.a.x != 0:
            a = (self.b.y - self.a.y) / (self.b.x - self.a.x)
            b = self.a.y - a*self.a.x
            return a, -1.0, b
        else:
            return 1., 0., -self.a.x

    def length(self):
        return np.sqrt((self.b.x - self.a.x) ** 2 + (self.b.y - self.a.y) ** 2)

    def translation(self, width):
        x1, x2, y1, y2 = self.a.x, self.b.x, self.a.y, self.b.y
        alpha = 0.
        if x2 - x1 != 0:
            alpha = np.arctan(-1 / ((y2 - y1) / (x2 - x1)) )

        dx = np.cos(alpha) * width
        dy = np.sin(alpha) * width
        transb = self.b.translation(dx, dy)

        sgn = cross_product(self.a, self.b, transb)

        transb = self.b.translation(dx*sgn, dy*sgn)
        transa = self.a.translation(dx*sgn, dy*sgn)
        return Line(transa, transb)

    def adjust_to_polyline(self, polyline):
        for line in polyline.lines:
            p = intersection(self, line, strong=True)
            if p:
                self.b = p

    def adjust_to_track(self, track):
        self.adjust_to_polyline(track.left)
        self.adjust_to_polyline(track.right)


def intersection(line1, line2, strong=False): # line - (X, Y)
    if line1 == line2:
        return False
    if line1.a == line2.a or line1.a == line2.b or line1.b == line2.a or line1.b == line2.b:
        if strong:
            if line1.a == line2.a or line1.a== line2.b:
                return Point(line1.a.x, line1.a.y)
            else:
                return Point(line1.b.x, line1.b.y)
        return False
    a1, b1, c1 = line1.equation()
    a2, b2, c2 = line2.equation()
    if np.absolute(a2 - a1) < 0.000001:
        return False
    #print "MAtrix ", [a1, b1], [a2, b2], [-c1,  -c2]
    x = np.linalg.solve([[a1, b1], [a2, b2]], [-c1, -c2])
    #print x
    P = Point(x[0], x[1])
    if P.inside(line1) and P.inside(line2):
        return P
    else:
        return False


class Polyline:

    def __init__(self, lines):
        self.lines = lines

    def plot_data(self):
        X = []
        Y = []
        for line in self.lines:
            X.append(line.a.x)
            X.append(line.b.x)
            Y.append(line.a.y)
            Y.append(line.b.y)
        return X, Y

    def translation(self, width):
        lines = []
        for line in self.lines:
            if len(lines) == 0:
                lines.append(line.translation(width))
                continue
            lt = line.translation(width)
            p =  intersection(lt, lines[-1])
            if p:
                lines[-1].b = p
                lt.a = p
                lines.append(lt)
            else:
                lines.append(Line(lines[-1].b, lt.a) )
                lines.append(lt)
        return Polyline(lines)

    def find_intersections(self):
        for i in range(len(self.lines)):
            for j in range(len(self.lines)):
                if intersection(self.lines[i], self.lines[j]):
                    return (i, j)
        return False

    def remove_intersections(self):
        while True:
            inter = self.find_intersections()
            if inter:
                p = intersection( self.lines[inter[0]], self.lines[inter[1]] )
                self.lines[inter[0]].b = p
                self.lines[inter[1]].a = p
                self.lines[inter[0]+1:inter[1]] = []
            else:
                break;

    def length(self):
        res = 0.
        for line in self.lines:
            res += line.length()
        return res

    def point_distance(self, p):
        res = 0.
        for line in self.lines:
            if p.inside(line):
                res += Line(line.a, p).length()
                break
            else:
                res += line.length()
        return res

### checks if 2 polylines have intersections
### poly1 may be the same as poly2
def find_intersections(poly1, poly2):
    for line1 in poly1.lines:
        for line2 in poly2.lines:
            p = intersection(line1, line2)
            if p:
                return p
    return False
