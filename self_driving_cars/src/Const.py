import numpy as np

learn_track_width = 6
frame_dt = 0.1

car_width = 2.
car_length = 4.

### meters / s
car_max_speed = 39.

### angle in radians the car can turn in one tick
car_max_turn = 0.2
### maximum acceleration in one tick
car_max_acc = 1.2
### in meters
car_sensor_length = 30.
car_sensor_count = 5

### initial positioning of cars on the track
car_initial_angle = np.pi/2
car_initial_x = -3.
car_initial_y = 4.
car_initial_speed = 0.

### each tuple is the dimensions of matrix between layers
###network_layers_shape = [(6, 8), (8, 4), (4, 2)]
network_layers_shape = [(6, 8), (8, 2)]
network_weights_limit = 5

chromosome_length = np.array([dim[0]*dim[1] for dim in network_layers_shape]).sum()


evolution_path_root = '../images/sim/'
dag_evolution_path_root = '../images/simdag/'
