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

    t_width = 6

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

    def translation(self):
        x1, x2, y1, y2 = self.a.x, self.b.x, self.a.y, self.b.y
        alpha = 0.
        if x2 - x1 != 0:
            alpha = np.arctan(-1 / ((y2 - y1) / (x2 - x1)) )

        dx = np.cos(alpha) * self.t_width
        dy = np.sin(alpha) * self.t_width
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

    def translation(self):
        lines = []
        for line in self.lines:
            if len(lines) == 0:
                lines.append(line.translation())
                continue
            lt = line.translation()
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

def random_polyline(n, sigma, l):
    lines = [Line(Point(0., 0.), Point(0., 10.))]
    a = 1.
    xprv = 0.
    yprv = 10.0
    for i in range(n):
        a += np.random.normal(0, sigma)
        l = np.random.random() * l + 2
        x = xprv + l*np.cos(a)
        y = yprv + l*np.sin(a)
        lines.append( Line(Point(xprv, yprv), Point(x, y) ) )
        xprv = x
        yprv = y
    return Polyline(lines)

def generate_path(n):
    i = 0
    while True:
        i += 1
        print i
        p = random_polyline(n, 0.3, 10)
        pt = p.translation()
        if p.find_intersections() or find_intersections(p, pt):
            continue
        else:
            pt.remove_intersections()
            return p, pt

class Car:

    width = 1.
    height = 2.
    dt = 0.1
    sensor_length = 10.

    def __init__(self, pos, alpha, speed, brain):
        self.pos = pos
        self.alpha = alpha
        self.speed = speed
        self.brain = brain

    def get_dx(self):
        return self.speed * self.dt * np.cos(self.alpha)

    def get_dy(self):
        return self.speed * self.dt * np.sin(self.alpha)

    def get_polyline(self):
        dxh = self.height * np.cos(self.alpha)
        dyh = self.height * np.sin(self.alpha)
        dxw = self.width  * np.cos(self.alpha - np.pi/2)
        dyw = self.width  * np.sin(self.alpha - np.pi/2)
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

    def fitness_value(self, track):
        poly = self.get_polyline()
        pl = find_intersections(poly, track.left)
        pr = find_intersections(poly, track.right)
        if pl:
            return track.left.point_distance(pl) / track.left.length()
        elif pr:
            return track.right.point_distance(pr) / track.right.length()
        else:
            return 1.

    def is_finished(self, track):
        if find_intersections(self.get_polyline(), track.finish):
            return True
        return False

    def is_crashed(self, track):
        if find_intersections(self.get_polyline(), track.right) or find_intersections(self.get_polyline(), track.left):
            return True
        return False

    def set_position(self, pos, alpha):
        self.pos = pos
        self.alpha = alpha

    def get_inputs(self, track):
        sensors = self.get_sensors(track)
        inputs = np.zeros(6)
        for i in range(5):
            inputs[i] = sensors[i].length()
        inputs[5] = self.speed
        return inputs

    def move(self, track):
        if self.is_crashed(track) == False and self.is_finished(track) == False:
            inputs = self.get_inputs(track)
            decision = self.brain.make_decision(inputs)
            self.alpha += decision[1]
            self.speed += decision[0]
            if (self.speed < 0):
                 self.speed = 0
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
                fitness = max (fitness, self.fitness_value(track))
        return fitness, positions, angles

class Track:

    def __init__(self, n):
        self.left, self.right = generate_path(n)
        self.finish = Polyline( [ Line (self.left.lines[-1].b, self.right.lines[-1].b)])

    def draw(self):
        x, y = self.right.plot_data()
        plt.plot(x, y, color="black")
        xt, yt = self.left.plot_data()
        plt.plot(xt, yt, color="black")

class Brain:

    def __init__(self, network):
        self.network = network

    ### inputs - 5 sensors and speed
    ### output - dV dAlpha
    def make_decision(self, inputs):
        return np.array([np.random.randn(), np.random.randn() * 0.1] )

################################################################################
fig = plt.figure()

plt.axis('equal')
plt.axis('off')


track = Track(10)

brain = Brain([])

cars = [Car(Point(-4., 4.), np.pi/2, 3., brain) for _ in range(5)]

N = len(cars)
lines = [plt.plot([], [], linewidth = 2)[0] for _ in range(N)]
sensors = [plt.plot([], [], color="black", linewidth=0.5)[0] for _  in range(N*5)]

perf = 0

def init():
    perf = time.time()
    track.draw()
    for line in lines:
        line.set_data([], [])
    for sensor in sensors:
        sensor.set_data([], [])
    return lines + sensors

FRAMES_LIMIT = 100
angles = []
positions = []

for car in cars:
    f, p, a = car.drive(track, FRAMES_LIMIT)
    print "FITNESS ", f
    angles.append(a)
    positions.append(p)

def animate(i):
    global perf
    tmp = time.time()
    print "frame ", i, "render time ", tmp- perf
    perf = tmp
    for j,line in enumerate(lines):
        cars[j].set_position(positions[j][i], angles[j][i])
        s = cars[j].get_sensors(track)
        for k in range(5):
            sx, sy = s[k].plot_data()
            sensors[5*j+k].set_data(sx, sy)
        X, Y = cars[j].get_polyline().plot_data()
        line.set_data(X, Y)
    return sensors + lines

# Set up formatting for the movie files
Writer = animation.writers['ffmpeg']
writer = Writer(fps=10, metadata=dict(artist='Me'), bitrate=1800)

anim = animation.FuncAnimation(fig, animate, init_func=init,
                               frames=FRAMES_LIMIT, interval=50, blit=False, repeat=False)
anim.save('lines.mp4', writer=writer)

plt.show()
plt.ion()
plt.close('all')
