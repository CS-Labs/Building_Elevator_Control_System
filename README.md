# TODO Deliverable One 
### (Add/Remove things as needed)
##### Note: For the first deliverable if we go ahead with what he suggested a majority of the work is on the GUI. His suggestion is effectively the entire GUI implemented with very little to no functionality (nothing happens with you interact with it). By GUI I am referencing the Control panel. It's also possible we can move some of the bullet points below to later deliverables, except for bullet points that are related to the control panels (ignoring functionality/action listener implementations)

* Finalize the frames of the three panels, they have to be three seperate panels since some of them are static through scenes where others are not. Right now they are not created through fxml, this can be changed if desired. The colors on all 3 panels are just black for now
so we can see where they are, we should probably change the color? What ever color/texture we choose the three panels should be the same since they are all part of the control panel. 
* I wasn't able to get the panel image that goes behind the buttons to size right so right now it's just a grey rectangle.
* The floor button panel is missing the three bottom buttons, I didn't add these because it was messing up the alignment of the other button rows.
* Add other buttons/controls to the control panels, some include: Button to switch to overview mode, buttons to switch between 
1 to 4 of the elevator CCTV views (this should probably be a drop down list since the number of elevators is configurable?), button to lock the floor panel buttons, etc. (See his requirements). Note we do not have to implement the functionality of most of these buttons yet.
* Get a continual update loop running in the BuildingControl class, I implemented pulse entity but the pulse method is not being called, should this be a logic entity?
* Add directional arrow inside cabin. 
* Figure out a way to wipe out all render entities in the CCTV view while not losing reference. (These get wipped out when switching
to overview mode)
* Add support to floor button presses. This includes:
  * Updating internal floor number (this should automatically update the floor sign animation)
  * Trigger door open and close animations. (Support partially implemented)
* Add overview mode, this is still only with a single elevator. This mode is triggered by a button in the control panel.

# TODO Deliverable Two-Four 
### (Add/Remove things as needed). 
#### Note, we should break these up into chunks later. This list is probably missing many things
* Implement all the sensors mentioned in the power points, i.e the door sensors. 
* Add the rest of the elevators.
* Expand on the overview mode to show all events.
* Display simulation information.
* Add fire alarm
* Add random people generation/button presses.
* Implement elevator algorithm. This relays peoples requests to the appropriate cabin requests (elevator that will handle the press)
* Add other random events, fire, maintence. We need to add a maintence key? (I dont know how we should use this).


Sources: 

Non-listed images were created with InkScape,Gimp and Paint. 

CC License:  
https://pngtree.com/freepng/fire-alarm-circular-cartoon-vector-material_2427371.html  
http://all-free-download.com/free-vector/download/website-numbered-buttons-vector_533063.html  
https://www.shareicon.net/human-people-male-men-users-user-person-man-account-profile-avatar-98071  
http://www.myfreephotoshop.com/up-and-down-buttons-psd-material.html  
https://commons.wikimedia.org/wiki/File:Circle-icons-flame.svg  
Fonts created with:  
https://fontmeme.com/fonts/ds-digital-font/  
