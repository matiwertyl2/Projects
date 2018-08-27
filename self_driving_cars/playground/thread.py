import multiprocessing
import time

def f(x, y):
    for i in range(1000000):
        x+=y
    return x

def f_thread(arr):
    return f(arr[0], arr[1])

pool = multiprocessing.Pool()
arr = []
for i in range(80):
    arr.append((i, i))

a = time.time()
results = pool.map(f_thread, arr)
print "MULTI ", time.time() - a

a = time.time()
for i in range(80):
    x = f(arr[i][0], arr[i][1])
print "SINGLE ", time.time() - a
