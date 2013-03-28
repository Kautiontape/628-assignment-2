628 Assignment 2
================

628 Assignment 2 - Team Awesome [Justin Ermer + Shawn Squire]

Description
----------------
**Due: March 28th, 2013, 11:59 pm**

**Goal**: the goal of this assignment is to get accustomed to using sensors on the mobile phone, accessing 
location data, and using maps. You will build a location profiler for a smartphone user.
A location profiler is a simple tool that keeps track of the locations that a user went to and some hints 
on what activity the user was performing. Specifically, the app keeps track of the latitude and longitude 
of a location, the corresponding human readable address of that location, <x,y,z> values of the 
accelerometer, the orientation of the phone, and what activity the user was performing (if any). The app 
should display on a map all the past locations that the user has been to using push pins (or any fancy UI 
element on a map overlay). By clicking on a particular push pin, the app should display the 
accelerometer values, the location address (as a String), the orientation of the phone when the user was 
at that address, and the logged activity. 

Here is how the Activities for the app should work. The app should continuously log the user location. 
When it figures out that the location of the user has changed by more than 100 m, it logs the 
accelerometer values and the phone orientation, and then it prompts the user to input his/her activity. 
When the user inputs his activity all these pieces of information (latitude, longitude, address (using 
reverse geocoding), accelerometer values, and orientation) is pushed to a table on a mysql lite database 
resident on the phone. The Main activity of the app should be a map which should periodically load data 
from the mysql lite database and display it on the map. Here are some of the challenges you need to 
address.

1. Using the GPS unit continuously can be energy consuming. While you will get full credit for the assignment if you just use the GPS unit, think/implement ways in which you can minimize the use of the GPS unit (using the accelerometer perhaps) [Extra Credit = 2 points]

2. The goal of keeping the orientation is to figure out whether the phone is in a user’s pocket?  While this is again not necessary for the assignment, think/implement ways through which you can determine whether the phone is in the user’s pocket or in his/her hand [Extra Credit = 2 points].

The submission would be similar to the last assignment. Please make a video on youtube and share it
with the TA and the instructor. Also share your source code on the google site page.
