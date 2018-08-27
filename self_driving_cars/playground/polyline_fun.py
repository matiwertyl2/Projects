import numpy as np
import matplotlib.pyplot as plt
import random

def polyline(n):
    X = np.zeros((n, 2))
    Y = np.zeros((n, 2))
    a = 0
    xprv = 0.0
    yprv = 0.0
    for i in range(n):
        a += np.random.normal(0, 0.4)
        l = np.random.random() * 3 + 1
        x = xprv + l*np.cos(a)
        y = yprv + l*np.sin(a)
        X[i, :] = np.array([xprv, x])
        Y[i, :] = np.array([yprv, y])
        xprv = x
        yprv = y
    return X, Y

def line_equation(line):
    x1, x2, y1, y2 = line[0][0], line[0][1], line[1][0], line[1][1]
    a = (y2 - y1) / (x2 - x1)
    b = y1 - a*x1
    return a, -1.0, b

def inside(line, P):
    x1, x2, y1, y2 = line[0][0], line[0][1], line[1][0], line[1][1]
    x, y = P[0], P[1]
    if (x < x1 and x < x) or (x > x1 and x > x2):
        return False
    if (y < y1 and y < y2) or (y > y1 and y > y2):
        return False
    return True

def intersection(line1, line2): # line - (X, Y)
    a1, b1, c1 = line_equation(line1)
    a2, b2, c2 = line_equation(line2)
    if np.absolute(a2 - a1) < 0.000001:
        return False
    #print "MAtrix ", [a1, b1], [a2, b2]
    x = np.linalg.solve([[a1, b1], [a2, b2]], [-c1, -c2])
    if inside(line1, x) and inside(line2, x):
        return x
    else:
        return False

def cross_product(x0, y0, x1, y1, x2, y2):
    return np.sign((x1-x0)*(y2 - y0) - (x2 - x0) * (y1 - y0))

def translation(X, Y):
    x1, x2, y1, y2 = X[0], X[1], Y[0], Y[1]
    width = 6
    a = np.arctan(-1 / ((y2 - y1) / (x2 - x1)) )
    sgn = cross_product(x1, y1, x2, y2, x2 + np.cos(a) * width, y2 + np.sin(a) * width)
    X = np.array([x1, x2]) + np.cos(a) * width * sgn
    Y = np.array([y1, y2]) + np.sin(a) * width * sgn
    return X, Y

def poly_translation(X, Y):
    n = len(X)
    Xt = []
    Yt = []
    xt, yt = translation(X[0], Y[0])
    Xt.append(xt)
    Yt.append(yt)
    for i in range(1, n):
        xt, yt = translation(X[i], Y[i])
        x =  intersection((Xt[-1], Yt[-1]), (xt, yt))
        if type(x) != bool:
            Xt[-1][1] = x[0]
            Yt[-1][1] = x[1]
            xt[0] = x[0]
            yt[0] = x[1]
        else:
            Xt.append([Xt[-1][1], xt[0]])
            Yt.append([Yt[-1][1], yt[0]])
        Xt.append(xt)
        Yt.append(yt)
    return np.array(Xt), np.array(Yt)

def random_path(n):
    X, Y = polyline(n)
    Xt, Yt = poly_translation(X, Y)
    return X, Y, Xt, Yt

def check_intersections(X, Y, Xt, Yt):
    for i in range(len(X)):
        for j in range(i+2, len(X)):
            if j - i == len(X):
                continue
            x = intersection( (X[i], Y[i]), (X[j], Y[j]) )
            if type(x) != bool:
                return (i, j)
    for i in range(len(X)):
        for j in range(len(Xt)):
            x = intersection((X[i], Y[i]), (Xt[j], Yt[j]))
            if type(x) != bool:
                return True
    return False

def remove_intersections(X, Y):
    while True:
        inter = check_intersections(X, Y, [], [])
        if inter:
            x = intersection( ( X[inter[0]], Y[inter[0]] ), (X[inter[1]], Y[inter[1]]) )
            X[inter[0]][1] = x[0]
            X[inter[1]][0] = x[0]
            Y[inter[0]][1] = x[1]
            Y[inter[1]][0] = x[1]
            X = np.delete(X, range(inter[0]+1, inter[1]), 0)
            Y = np.delete(Y, range(inter[0]+1, inter[1]), 0)
            print x
            for i in range(len(X)):
                for j in range(len(X)):
                    x = 1
        else:
            break;
    return X, Y

def generate_path(n):
    i = 0
    while True:
        i += 1
        print i
        X, Y, Xt, Yt = random_path(n)
        if check_intersections(X, Y, Xt, Yt) == False:
            Xt, Yt = remove_intersections(Xt, Yt)
            return X, Y, Xt, Yt

X, Y, Xt, Yt = generate_path(150)
plt.axis('equal')
for i in range(len(X)):
    plt.plot(X[i], Y[i], color="black")
for i in range(len(Xt)):
    plt.plot(Xt[i], Yt[i], color="red")
plt.show()
