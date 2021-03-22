# Simple grid selector in plain html/js

Three basic things available
1. Grid - x*y grid with auto width
2. Buttons - Add as many buttons as needed in the array
3. Messages - Uses a global message object to print strings

* Input your x and y values in the global variables ROWS and COLUMNS
* Use the button listeners to write logic

Each cell has its data attribute set to a value corresponding to its location in the grid. This way it becomes easier to identify the cell on its click event. There are other ways to achieve this like setting the id of each cell with unique values.

On click of the cell, we read the "point" data attribute. In the listener we can then propagate this value wherever you want.

Just remember to use event.target and not event.currentTarget for this use case.
